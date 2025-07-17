package com.example.prm392mnlv.data.dto.response;

import androidx.annotation.NonNull;

import com.squareup.moshi.Json;

public class CartItemResponse {
    public final @NonNull String id;

    @Json(name = "productID")
    public final @NonNull String productId;

    public final double unitPrice;
    public final int quantity;

    public CartItemResponse(@NonNull String id, @NonNull String productId, double unitPrice, int quantity) {
        this.id = id;
        this.productId = productId;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
    }
}
