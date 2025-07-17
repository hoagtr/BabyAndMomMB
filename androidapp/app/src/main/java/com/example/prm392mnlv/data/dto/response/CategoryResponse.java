package com.example.prm392mnlv.data.dto.response;

import androidx.annotation.NonNull;

public class CategoryResponse {
    public final @NonNull String id;
    public final @NonNull String categoryName;

    public CategoryResponse(@NonNull String id, @NonNull String categoryName) {
        this.id = id;
        this.categoryName = categoryName;
    }
}
