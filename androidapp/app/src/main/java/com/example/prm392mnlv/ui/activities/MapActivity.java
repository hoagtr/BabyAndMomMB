package com.example.prm392mnlv.ui.activities;

import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import com.example.prm392mnlv.R;
import com.example.prm392mnlv.data.dto.response.VietmapDirectionResponse;
import com.example.prm392mnlv.data.mappings.VietMapApiService;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import vn.vietmap.vietmapsdk.Vietmap;
import vn.vietmap.vietmapsdk.annotations.MarkerOptions;
import vn.vietmap.vietmapsdk.annotations.PolylineOptions;
import vn.vietmap.vietmapsdk.geometry.LatLng;
import vn.vietmap.vietmapsdk.maps.MapView;
import vn.vietmap.vietmapsdk.maps.Style;
import vn.vietmap.vietmapsdk.maps.VietMapGL;

public class MapActivity extends AppCompatActivity {

    private MapView mapView;
    private VietMapGL vietMapGL;

    private final String STYLE_URL = "https://maps.vietmap.vn/api/maps/light/styles.json?apikey=402f419bab73a9275007e8359102b3a8fe3af86beaa1144f";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Vietmap.getInstance(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            }, 1001);
        }

        mapView = findViewById(R.id.vmMapView);
        mapView.onCreate(savedInstanceState);

        mapView.getMapAsync(new vn.vietmap.vietmapsdk.maps.OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull VietMapGL map) {
                vietMapGL = map;
                vietMapGL.setStyle(new Style.Builder().fromUri(STYLE_URL), style -> {
                    Toast.makeText(MapActivity.this, "Bản đồ đã tải xong!", Toast.LENGTH_SHORT).show();

                    vietMapGL.addMarker(new MarkerOptions()
                            .position(new LatLng(10.84132779895594, 106.80990445443749))  // Tọa độ cửa hàng
                            .title("Cửa hàng của bạn")
                            .snippet("Đây là vị trí test"));
                });
            }
        });

        Button btnDirection = findViewById(R.id.btnDirection);
        btnDirection.setOnClickListener(view -> {
            getUserLocationAndDrawRoute();
        });

        FloatingActionButton btnMyLocation = findViewById(R.id.btnMyLocation);
        btnMyLocation.setOnClickListener(v -> {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                }, 1001);
                return;
            }

            FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
                if (location != null && vietMapGL != null) {
                    LatLng userLatLng = new LatLng(location.getLatitude(), location.getLongitude());

                    // 🔹 Di chuyển camera tới vị trí
                    vietMapGL.animateCamera(vn.vietmap.vietmapsdk.camera.CameraUpdateFactory.newLatLngZoom(userLatLng, 15));

                    // ✅ Thêm Marker tại vị trí hiện tại
                    vietMapGL.addMarker(new MarkerOptions()
                            .position(userLatLng)
                            .title("Vị trí của tôi")
                            .snippet("Đây là vị trí hiện tại của bạn"));
                } else {
                    Toast.makeText(this, "Không thể lấy vị trí hiện tại", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    private void getUserLocationAndDrawRoute() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            }, 1001);
            return;
        }

        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                double userLat = location.getLatitude();
                double userLng = location.getLongitude();

                LatLng userLatLng = new LatLng(userLat, userLng);
                LatLng storeLatLng = new LatLng(10.84132779895594, 106.80990445443749);

                fetchRouteAndDraw(userLatLng, storeLatLng);
            } else {
                Toast.makeText(this, "Không thể lấy vị trí hiện tại!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchRouteAndDraw(LatLng userLat, LatLng storeLat) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://maps.vietmap.vn/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        VietMapApiService apiService = retrofit.create(VietMapApiService.class);

        List<String> points = new ArrayList<>();
        points.add(userLat.getLatitude() + "," + userLat.getLongitude());
        points.add(storeLat.getLatitude() + "," + storeLat.getLongitude());

        Call<VietmapDirectionResponse> call = apiService.getRoute(
                "1.1",
                "402f419bab73a9275007e8359102b3a8fe3af86beaa1144f",
                points,
                false,
                "motorcycle"
        );

        Log.d("API_URL", call.request().url().toString());

        call.enqueue(new Callback<VietmapDirectionResponse>() {
            @Override
            public void onResponse(Call<VietmapDirectionResponse> call, Response<VietmapDirectionResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().paths != null && !response.body().paths.isEmpty()) {
                    JsonObject pointObj = response.body().paths.get(0).points;
                    JsonArray coordinates = pointObj.getAsJsonArray("coordinates");

                    List<LatLng> latLngList = new ArrayList<>();
                    for (int i = 0; i < coordinates.size(); i++) {
                        JsonArray coord = coordinates.get(i).getAsJsonArray();
                        double lng = coord.get(0).getAsDouble();  // [lng, lat]
                        double lat = coord.get(1).getAsDouble();
                        latLngList.add(new LatLng(lat, lng));
                    }

                    vietMapGL.addPolyline(new PolylineOptions()
                            .addAll(latLngList)
                            .color(Color.BLUE)
                            .width(6f));
                } else {
                    Toast.makeText(MapActivity.this, "Không có dữ liệu route từ API!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<VietmapDirectionResponse> call, Throwable t) {
                Log.e("API_ERROR", "Lỗi gọi API", t);
                Toast.makeText(MapActivity.this, "Lỗi gọi API: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void drawRoute(DirectionsRoute route) {
        List<Point> coords = LineString.fromPolyline(route.geometry(), 6).coordinates();
        List<LatLng> points = new ArrayList<>();

        for (Point point : coords) {
            points.add(new LatLng(point.latitude(), point.longitude()));
        }

        vietMapGL.addPolyline(new PolylineOptions()
                .addAll(points)
                .color(Color.BLUE)
                .width(5));
    }
}