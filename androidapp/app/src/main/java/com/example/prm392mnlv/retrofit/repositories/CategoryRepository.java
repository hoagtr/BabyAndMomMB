package com.example.prm392mnlv.retrofit.repositories;

import androidx.annotation.Nullable;

import com.example.prm392mnlv.data.dto.response.CategoryResponse;
import com.example.prm392mnlv.retrofit.client.ApiClient;
import com.example.prm392mnlv.retrofit.services.CategoryService;

import java.util.List;

import retrofit2.Callback;

public class CategoryRepository {
    private final CategoryService mCategoryService;

    public CategoryRepository() {
        mCategoryService = ApiClient.getClient().create(CategoryService.class);
    }

    public void getCategories(@Nullable String id, Callback<List<CategoryResponse>> callback) {
        mCategoryService.getCategories(id).enqueue(callback);
    }
}
