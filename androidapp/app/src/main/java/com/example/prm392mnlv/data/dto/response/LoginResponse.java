package com.example.prm392mnlv.data.dto.response;

import androidx.annotation.NonNull;

import java.util.Date;

public class LoginResponse {
    public final @NonNull String accessToken;
    public final @NonNull String refreshToken;
    public final @NonNull String tokenType;
    public final @NonNull String authType;
    public final @NonNull Date expiresIn;
    public final @NonNull UserInfo user;

    public LoginResponse(@NonNull String accessToken,
                         @NonNull String refreshToken,
                         @NonNull String tokenType,
                         @NonNull String authType,
                         @NonNull Date expiresIn,
                         @NonNull UserInfo user) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.tokenType = tokenType;
        this.authType = authType;
        this.expiresIn = expiresIn;
        this.user = user;
    }

    public static class UserInfo {
        public final @NonNull String email;
        public final @NonNull String[] roles;

        public UserInfo(@NonNull String email, @NonNull String[] roles) {
            this.email = email;
            this.roles = roles;
        }
    }
}
