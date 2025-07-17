package com.example.prm392mnlv.data.dto.request;

import androidx.annotation.NonNull;

public class LoginRequest {
    public final @NonNull String email;
    public final @NonNull String password;

    public LoginRequest(@NonNull String email, @NonNull String password) {
        this.email = email;
        this.password = password;
    }
}
