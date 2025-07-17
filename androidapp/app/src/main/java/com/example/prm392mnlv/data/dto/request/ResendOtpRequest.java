package com.example.prm392mnlv.data.dto.request;

import androidx.annotation.NonNull;

public class ResendOtpRequest {
    public final @NonNull String email;

    public ResendOtpRequest(@NonNull String email) {
        this.email = email;
    }
}
