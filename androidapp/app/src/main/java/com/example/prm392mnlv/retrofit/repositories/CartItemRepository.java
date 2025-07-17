package com.example.prm392mnlv.retrofit.repositories;

import androidx.annotation.NonNull;

import com.example.prm392mnlv.data.dto.request.CartItemCreateRequest;
import com.example.prm392mnlv.data.dto.request.CartItemUpdateRequest;
import com.example.prm392mnlv.data.dto.response.CartItemResponse;
import com.example.prm392mnlv.data.dto.response.MessageResponse;
import com.example.prm392mnlv.data.mappings.CartItemMapper;
import com.example.prm392mnlv.data.models.CartItem;
import com.example.prm392mnlv.retrofit.client.ApiClient;
import com.example.prm392mnlv.retrofit.services.CartService;

import java.util.List;

import retrofit2.Callback;

public class CartItemRepository {
    private static final CartService.OrderDetailsStatus IN_CART = CartService.OrderDetailsStatus.InCart;

    private final CartService mCartService;

    public CartItemRepository() {
        mCartService = ApiClient.getClient().create(CartService.class);
    }

    public void getCartItems(Callback<List<CartItemResponse>> callback) {
        mCartService.getOrderDetails(null, IN_CART, 1, 999)
                .enqueue(callback);
    }

    public void createCartItem(CartItem cartItem, Callback<MessageResponse> callback) {
        CartItemCreateRequest dto = CartItemMapper.INSTANCE.toCreateRequest(cartItem);
        mCartService.createOrderDetails(dto).enqueue(callback);
    }

    public void updateCartItem(@NonNull String id, CartItem cartItem, Callback<MessageResponse> callback) {
        CartItemUpdateRequest dto = CartItemMapper.INSTANCE.toUpdateRequest(cartItem);
        mCartService.updateOrderDetails(id, dto).enqueue(callback);
    }

    public void deleteCartItem(@NonNull String id, Callback<MessageResponse> callback) {
        mCartService.deleteOrderDetails(id).enqueue(callback);
    }
}
