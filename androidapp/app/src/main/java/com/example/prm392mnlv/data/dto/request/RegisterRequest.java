package com.example.prm392mnlv.data.dto.request;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class RegisterRequest {
    public final @NonNull String name;
    public final @NonNull String email;
    public final @Nullable String phoneNumber;
    public final @NonNull String password;

    public RegisterRequest(@NonNull String name, @NonNull String email, @Nullable String phoneNumber, @NonNull String password) {
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.password = password;
    }
}
