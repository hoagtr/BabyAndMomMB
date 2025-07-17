package com.example.prm392mnlv.data.dto.response;

import androidx.annotation.NonNull;

public class RefreshTokenResponse {
    public final @NonNull String accessToken;
    public final @NonNull String refreshToken;

    public RefreshTokenResponse(@NonNull String accessToken, @NonNull String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
