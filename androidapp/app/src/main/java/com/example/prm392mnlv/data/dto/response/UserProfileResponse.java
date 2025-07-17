package com.example.prm392mnlv.data.dto.response;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class UserProfileResponse {
    public final @NonNull String name;
    public final @NonNull String email;
    public final @NonNull String phoneNumber;
    public final @Nullable String shippingAddress;

    public UserProfileResponse(@NonNull String name, @NonNull String email, @NonNull String phoneNumber, @Nullable String shippingAddress) {
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.shippingAddress = shippingAddress;
    }
}
