package com.example.prm392mnlv.retrofit.services;

import androidx.annotation.Nullable;

import com.example.prm392mnlv.data.dto.response.CategoryResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface CategoryService {
    String SEGMENT = "category/";

    @GET(SEGMENT)
    Call<List<CategoryResponse>> getCategories(@Query("id") @Nullable String id);
}
