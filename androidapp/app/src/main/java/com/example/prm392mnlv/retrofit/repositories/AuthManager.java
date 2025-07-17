package com.example.prm392mnlv.retrofit.repositories;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.prm392mnlv.data.dto.request.ConfirmOtpResetPasswordRequest;
import com.example.prm392mnlv.data.dto.request.LoginRequest;
import com.example.prm392mnlv.data.dto.request.RegisterConfirmationRequest;
import com.example.prm392mnlv.data.dto.request.RegisterRequest;

import com.example.prm392mnlv.data.dto.request.ResendOtpRequest;
import com.example.prm392mnlv.data.dto.request.ResetPasswordRequest;
import com.example.prm392mnlv.data.dto.response.LoginResponse;
import com.example.prm392mnlv.data.dto.response.MessageResponse;
import com.example.prm392mnlv.retrofit.client.ApiClient;
import com.example.prm392mnlv.retrofit.json.JsonPath;
import com.example.prm392mnlv.retrofit.services.AuthService;

import retrofit2.Callback;

public class AuthManager {
    private final AuthService mAuthService;

    public AuthManager() {
        mAuthService = ApiClient.getClient().create(AuthService.class);
    }

    public void login(@NonNull String email, @NonNull String password, Callback<LoginResponse> callback) {
        LoginRequest loginRequest = new LoginRequest(email, password);
        mAuthService.logIn(loginRequest).enqueue(callback);
    }

    public void register(@NonNull String name, @NonNull String email, @Nullable String phoneNumber, @NonNull String password, Callback<MessageResponse> callback) {
        RegisterRequest registerRequest = new RegisterRequest(name, email, phoneNumber, password);
        mAuthService.register(registerRequest).enqueue(callback);
    }

    public void confirmEmail(@NonNull RegisterConfirmationRequest request, Callback<MessageResponse> callback){
        mAuthService.confirmEmail(request).enqueue(callback);
    }

    public void resendConfirmationEmail(@NonNull ResendOtpRequest request, Callback<MessageResponse> callback){
        mAuthService.resendConfirmationEmail(request).enqueue(callback);
    }

    public void forgotPassword(@NonNull ResendOtpRequest request, Callback<MessageResponse> callback){
        mAuthService.forgotPassword(request).enqueue(callback);
    }

    public void confirmOtpResetPassword(@NonNull ConfirmOtpResetPasswordRequest request, Callback<MessageResponse> callback){
        mAuthService.confirmOtpResetPassword(request).enqueue(callback);
    }

    public void resetPassword(@NonNull ResetPasswordRequest request, Callback<MessageResponse> callback){
        mAuthService.resetPassword(request).enqueue(callback);
    }
}
