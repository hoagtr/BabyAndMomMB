package com.example.prm392mnlv.data.dto.request;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class UserProfileUpdateRequest {
    public final @NonNull String name;
    public final @NonNull String email;
    public final @NonNull String phoneNumber;
    public final @Nullable String shippingAddress;

    public UserProfileUpdateRequest(@NonNull String name, @NonNull String email, @NonNull String phoneNumber, @Nullable String shippingAddress) {
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.shippingAddress = shippingAddress;
    }
}
