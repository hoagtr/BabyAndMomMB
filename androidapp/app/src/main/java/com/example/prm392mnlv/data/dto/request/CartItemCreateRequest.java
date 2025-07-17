package com.example.prm392mnlv.data.dto.request;

import androidx.annotation.NonNull;

public class CartItemCreateRequest {
    public final @NonNull String productId;
    public final int quantity;

    public CartItemCreateRequest(@NonNull String productId, int quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }
}
