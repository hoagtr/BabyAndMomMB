package com.example.prm392mnlv.ui.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.view.View;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392mnlv.R;
import com.example.prm392mnlv.data.dto.response.ProductResponse;
import com.example.prm392mnlv.data.mappings.ProductMapper;
import com.example.prm392mnlv.data.models.Category;
import com.example.prm392mnlv.data.models.MenuItem;
import com.example.prm392mnlv.data.models.Product;
import com.example.prm392mnlv.retrofit.repositories.ProductRepository;
import com.example.prm392mnlv.ui.adapters.CategoryFilterAdapter;
import com.example.prm392mnlv.ui.adapters.MenuAdapter;
import com.example.prm392mnlv.ui.adapters.ProductAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductListActivity extends AppCompatActivity {
    private RecyclerView rvProducts;
    private RecyclerView menuRecyclerView;
    private ProductAdapter productAdapter;
    private final List<Product> productList = new ArrayList<>();
    private ProductRepository productRepository;
    private Spinner spinnerFilter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);

        // Khởi tạo view cho menu và filter
        menuRecyclerView = findViewById(R.id.menuRecyclerView);
        spinnerFilter = findViewById(R.id.spinnerFilter);
        setupMenu();

        // Khởi tạo RecyclerView cho sản phẩm
        rvProducts = findViewById(R.id.rvProducts);
        rvProducts.setLayoutManager(new LinearLayoutManager(this));
        productAdapter = new ProductAdapter(this, productList);
        rvProducts.setAdapter(productAdapter);

        // Khởi tạo repository
        productRepository = new ProductRepository();

        // Lấy danh sách category từ API và cập nhật Spinner
        getCategories();

        // Nếu chưa chọn filter nào, load tất cả sản phẩm
        fetchProducts(null);
    }

    // Hàm gọi API lấy danh sách products theo category
    private void fetchProducts(String categoryName) {
        // Nếu categoryName == null hoặc "All", load tất cả sản phẩm
        String filterCategory = (categoryName == null || categoryName.equalsIgnoreCase("All")) ? null : categoryName;

        // Chú ý: truyền null cho id và productName, và filterCategory cho categoryName
        productRepository.getProducts(null, null, filterCategory, new Callback<List<ProductResponse>>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(Call<List<ProductResponse>> call, Response<List<ProductResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<ProductResponse> responses = response.body();
                    productList.clear();

                    // Map từng ProductResponse sang Product dùng ProductMapper
                    for (ProductResponse pr : responses) {
                        Product product = ProductMapper.INSTANCE.toModel(pr);
                        productList.add(product);
                    }
                    productAdapter.notifyDataSetChanged();
                } else {
                    Log.e("ProductListActivity", "Response không thành công hoặc rỗng");
                }
            }

            @Override
            public void onFailure(Call<List<ProductResponse>> call, Throwable t) {
                Log.e("ProductListActivity", "Lỗi khi gọi API: " + t.getMessage());
            }
        });
    }


    // Hàm lấy danh sách categoryName từ API
    private void getCategories() {
        productRepository.getCategories(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Category> categoryObjects = response.body();

                    // Tạo list String để chứa tên category
                    List<String> categoryNames = new ArrayList<>();

                    // Thêm lựa chọn "All" vào đầu danh sách
                    categoryNames.add("All");

                    // Duyệt danh sách categoryObjects, trích xuất categoryName
                    for (Category cat : categoryObjects) {
                        categoryNames.add(cat.getCategoryName());
                    }

                    // Cập nhật Spinner
                    updateSpinnerFilter(categoryNames);
                } else {
                    Log.e("ProductListActivity", "Lỗi khi lấy danh sách category");
                }
            }

            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {
                Log.e("ProductListActivity", "Lỗi khi gọi API lấy category: " + t.getMessage());
            }
        });
    }


    // Cập nhật Spinner với danh sách category lấy được
    private void updateSpinnerFilter(List<String> categories) {
        CategoryFilterAdapter adapter = new CategoryFilterAdapter(this, categories);
        spinnerFilter.setAdapter(adapter);

        spinnerFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String filterOption = parent.getItemAtPosition(position).toString();
                fetchProducts(filterOption);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Không làm gì khi không có lựa chọn nào
            }
        });
    }

    private void setupMenu() {
        menuRecyclerView.setLayoutManager(new GridLayoutManager(this, 4));

        List<MenuItem> menuList = Arrays.asList(
                new MenuItem("Cart", R.drawable.ic_cart),
                new MenuItem("Map", R.drawable.ic_map),
                new MenuItem("Chat", R.drawable.ic_chat),
                new MenuItem("Logout", R.drawable.ic_logout)
        );

        MenuAdapter adapter = new MenuAdapter(menuList, this);
        menuRecyclerView.setAdapter(adapter);
    }
}
