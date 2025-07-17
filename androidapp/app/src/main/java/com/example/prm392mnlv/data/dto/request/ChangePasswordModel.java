package com.example.prm392mnlv.data.dto.request;

import androidx.annotation.NonNull;

public class ChangePasswordModel {
    public final @NonNull String oldPassword;
    public final @NonNull String newPassword;

    public ChangePasswordModel(@NonNull String oldPassword, @NonNull String newPassword) {
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
    }
} 