package com.example.prm392mnlv.data.dto.request;

import androidx.annotation.NonNull;

public class BearerTokenRefreshRequest {
    public final @NonNull String refreshToken;

    public BearerTokenRefreshRequest(@NonNull String refreshToken) {this.refreshToken = refreshToken;}
}
