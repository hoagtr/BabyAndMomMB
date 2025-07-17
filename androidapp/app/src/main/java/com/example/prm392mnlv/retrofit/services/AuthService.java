package com.example.prm392mnlv.retrofit.services;


import com.example.prm392mnlv.data.dto.request.ConfirmOtpResetPasswordRequest;
import com.example.prm392mnlv.data.dto.request.LoginRequest;
import com.example.prm392mnlv.data.dto.request.BearerTokenRefreshRequest;
import com.example.prm392mnlv.data.dto.request.RegisterConfirmationRequest;
import com.example.prm392mnlv.data.dto.request.RegisterRequest;
import com.example.prm392mnlv.data.dto.request.ResendOtpRequest;
import com.example.prm392mnlv.data.dto.request.ResetPasswordRequest;
import com.example.prm392mnlv.data.dto.request.ChangePasswordModel;
import com.example.prm392mnlv.data.dto.request.TokenGoogleModel;
import com.example.prm392mnlv.data.dto.response.LoginResponse;
import com.example.prm392mnlv.data.dto.response.MessageResponse;
import com.example.prm392mnlv.data.dto.response.RefreshTokenResponse;
import com.example.prm392mnlv.retrofit.json.JsonPath;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.PATCH;
import retrofit2.http.POST;

public interface AuthService {
    String SEGMENT = "auth/";

    @JsonPath("data")
    @POST(SEGMENT + "auth_account")
    Call<LoginResponse> logIn(@Body LoginRequest loginRequest);

    @POST(SEGMENT + "new_account")
    Call<MessageResponse> register(@Body RegisterRequest registerRequest);

    @JsonPath("data")
    @POST(SEGMENT + "refreshtoken")
    Call<RefreshTokenResponse> refreshToken(@Body BearerTokenRefreshRequest bearerTokenRefreshRequest);

    @PATCH(SEGMENT + "confirm_email")
    Call<MessageResponse> confirmEmail(@Body RegisterConfirmationRequest request);

    @PATCH(SEGMENT + "resend_confirmation_email")
    Call<MessageResponse> resendConfirmationEmail(@Body ResendOtpRequest request);

    @POST(SEGMENT + "forgotpassword")
    Call<MessageResponse> forgotPassword(@Body ResendOtpRequest request);

    @PATCH(SEGMENT + "confirm_otp_resetpassword")
    Call<MessageResponse> confirmOtpResetPassword(@Body ConfirmOtpResetPasswordRequest request);

    @PATCH(SEGMENT + "resetpassword")
    Call<MessageResponse> resetPassword(@Body ResetPasswordRequest request);

    @PATCH(SEGMENT + "change_password")
    Call<MessageResponse> changePassword(@Body ChangePasswordModel request);

    @POST(SEGMENT + "signin-google")
    Call<LoginResponse> signInWithGoogle(@Body TokenGoogleModel request);

}
