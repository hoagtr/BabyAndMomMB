package com.example.prm392mnlv.data.dto.request;

import androidx.annotation.NonNull;

public class CartItemUpdateRequest {
    public final @NonNull String productId;
    public final int quantity;

    public CartItemUpdateRequest(@NonNull String productId, int quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }
}
