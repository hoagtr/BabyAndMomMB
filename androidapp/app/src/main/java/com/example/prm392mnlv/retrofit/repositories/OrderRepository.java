package com.example.prm392mnlv.retrofit.repositories;

import androidx.annotation.NonNull;

import com.example.prm392mnlv.data.dto.response.MessageResponse;
import com.example.prm392mnlv.retrofit.client.ApiClient;
import com.example.prm392mnlv.retrofit.services.OrderService;

import retrofit2.Callback;

public class OrderRepository {
    private final OrderService mService;

    public OrderRepository() {
        mService = ApiClient.getClient().create(OrderService.class);
    }

    public void createOrder(@NonNull String paymentMethod,
                            @NonNull String shippingType,
                            Callback<MessageResponse> callback) {
        mService.createOrder(paymentMethod, shippingType).enqueue(callback);
    }
}
