package com.example.prm392mnlv.retrofit.services;

import androidx.annotation.Nullable;

import com.example.prm392mnlv.data.dto.response.ProductResponse;
import com.example.prm392mnlv.data.models.Category;
import com.example.prm392mnlv.retrofit.json.JsonPath;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ProductService {
    String SEGMENT = "products/";

    @JsonPath("items")
    @GET(SEGMENT + "getproduct & pagging")
    Call<List<ProductResponse>> getProducts(@Query("id") @Nullable String id,
                                            @Query("productname") @Nullable String productName,
                                            @Query("categoryname") @Nullable String categoryName,
                                            @Query("pageIndex") int pageIndex,
                                            @Query("pageSize") int pageSize);
    @GET("category")
    Call<List<Category>> getCategories();
}
