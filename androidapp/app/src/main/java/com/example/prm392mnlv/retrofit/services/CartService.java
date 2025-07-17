package com.example.prm392mnlv.retrofit.services;

import androidx.annotation.Nullable;

import com.example.prm392mnlv.data.dto.request.CartItemCreateRequest;
import com.example.prm392mnlv.data.dto.request.CartItemUpdateRequest;
import com.example.prm392mnlv.data.dto.response.CartItemResponse;
import com.example.prm392mnlv.data.dto.response.MessageResponse;
import com.example.prm392mnlv.retrofit.json.JsonPath;
import com.squareup.moshi.Json;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface CartService {
    String SEGMENT = "orderdetails/";

    @JsonPath("data:items")
    @GET(SEGMENT + "get_personal_order_detail")
    Call<List<CartItemResponse>> getOrderDetails(@Query("orderId") @Nullable String orderId,
                                                 @Query("orderDetailStatus") @Nullable OrderDetailsStatus orderDetailsStatus,
                                                 @Query("page") int pageIndex,
                                                 @Query("pageSize") int pageSize);

    @POST(SEGMENT + "add_to_cart")
    Call<MessageResponse> createOrderDetails(@Body CartItemCreateRequest cartItemCreateRequest);

    @PUT(SEGMENT + "{id}")
    Call<MessageResponse> updateOrderDetails(@Path("id") String id, @Body CartItemUpdateRequest cartItemUpdateRequest);

    @DELETE(SEGMENT + "{id}")
    Call<MessageResponse> deleteOrderDetails(@Path("id") String id);

    enum OrderDetailsStatus {
        @Json(name = "InCart")
        InCart,
        @Json(name = "Ordered")
        Ordered,
        @Json(name = "Cancelled")
        Cancelled;
    }
}
