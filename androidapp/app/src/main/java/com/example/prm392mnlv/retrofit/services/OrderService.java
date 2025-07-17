package com.example.prm392mnlv.retrofit.services;

import androidx.annotation.NonNull;

import com.example.prm392mnlv.data.dto.response.MessageResponse;

import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface OrderService {

    @POST("checkout")
    Call<MessageResponse> createOrder(@Query("paymentMethod") @NonNull String paymentMethod,
                                      @Query("ShippingType") @NonNull String shippingType);
}
