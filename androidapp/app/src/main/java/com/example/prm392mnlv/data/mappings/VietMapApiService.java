package com.example.prm392mnlv.data.mappings;

import com.example.prm392mnlv.data.dto.response.VietmapDirectionResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface VietMapApiService {
    @GET("api/route")
    Call<VietmapDirectionResponse> getRoute(
            @Query("api-version") String apiVersion,
            @Query("apikey") String apiKey,
            @Query("point") List<String> points,  // lặp lại point=lat,lng
            @Query("points_encoded") boolean pointsEncoded,
            @Query("vehicle") String vehicle
    );
}