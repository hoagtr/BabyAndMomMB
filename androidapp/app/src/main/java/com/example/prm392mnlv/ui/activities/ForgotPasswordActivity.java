package com.example.prm392mnlv.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.prm392mnlv.R;
import com.example.prm392mnlv.data.dto.request.ConfirmOtpResetPasswordRequest;
import com.example.prm392mnlv.data.dto.request.ResendOtpRequest;
import com.example.prm392mnlv.data.dto.request.ResetPasswordRequest;
import com.example.prm392mnlv.data.dto.response.MessageResponse;
import com.example.prm392mnlv.retrofit.repositories.AuthManager;

import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPasswordActivity extends AppCompatActivity {

    private AuthManager authManager = new AuthManager();
    private EditText mEmail;
    private EditText mPassword;
    private EditText mConfirmPassword;
    private EditText mOtp;
    private Button mResendOtp;
    private Button mSubmit;
    private TextView mStatus;
    private ImageView mBack;

    private final Pattern emailPattern = Pattern.compile("^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@"
            + "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$");

    private final Pattern passwordPattern = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forgot_password);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        configureView();
    }

    private void configureView(){
        mEmail = findViewById(R.id.editText_Email);
        mPassword = findViewById(R.id.editText_Password);
        mConfirmPassword = findViewById(R.id.editText_ConfirmPassword);
        mOtp = findViewById(R.id.editText_Otp);
        mResendOtp = findViewById(R.id.button_ResendOtp);
        mResendOtp.setOnClickListener(v -> onOtpSendRequested());
        mSubmit = findViewById(R.id.button_Submit);
        mSubmit.setOnClickListener(v -> onSubmit());
        mStatus = findViewById(R.id.textView_Status);
        mBack = findViewById(R.id.imageView_Back);
        mBack.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
    }

    private void onOtpSendRequested(){
        ResendOtpRequest request = new ResendOtpRequest(mEmail.getText().toString());
        Callback<MessageResponse> callback = new Callback<>() {
            @Override
            public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {
                if (!response.isSuccessful()){
                    mStatus.setText("Otp Send Error:" + (response.body() == null ? response.message() : response.body().message));
                    System.out.println(response.toString());
                    return;
                }
                Toast.makeText(getApplicationContext(), R.string.OTP_SENT, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<MessageResponse> call, Throwable throwable) {
                mStatus.setText(throwable.getMessage());
            }
        };
        authManager.forgotPassword(request, callback);
    }

    private boolean validate(){
        if (mEmail.getText().toString().trim().isEmpty()){
            mStatus.setText(R.string.ERR_EMAIL_EMPTY);
            return false;
        }
        if (mPassword.getText().toString().trim().isEmpty()){
            mStatus.setText(R.string.ERR_PASSWORD_EMPTY);
            return false;
        }
        if (!emailPattern.matcher(mEmail.getText().toString().trim()).matches()){
            mStatus.setText(R.string.ERR_EMAIL_INVALID);
            return false;
        }
        if (!passwordPattern.matcher(mPassword.getText().toString().trim()).matches()){
            mStatus.setText(R.string.ERR_INVALID_PASSWORD);
            return false;
        }
        if (!mPassword.getText().toString().trim().equals(mConfirmPassword.getText().toString().trim())){
            mStatus.setText(R.string.ERR_PASSWORD_NOT_MATCH);
            return false;
        }

        if (mOtp.getText().toString().length() != 6){
            mStatus.setText(R.string.ERR_OTP_EMPTY);
            return false;
        }
        mStatus.setText("");
        return true;
    }

    private void onSubmit(){
        if (!validate()) return;
        ConfirmOtpResetPasswordRequest request1 = new ConfirmOtpResetPasswordRequest(mEmail.getText().toString(), mOtp.getText().toString());
        Callback<MessageResponse> callback1 = new Callback<>() {
            @Override
            public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {
                if (!response.isSuccessful()){
//                    mStatus.setText(response.body() == null ? response.message() : response.body().message);
//                    System.out.println(response.toString());
                    mStatus.setText(R.string.INCORRECT_OTP);
                    return;
                }
                onConfirmOtpSuccess();
            }
            @Override
            public void onFailure(Call<MessageResponse> call, Throwable throwable) {
                mStatus.setText(throwable.getMessage());
            }
        };
        authManager.confirmOtpResetPassword(request1, callback1);
    }

    private void onConfirmOtpSuccess(){
        ResetPasswordRequest request2 = new ResetPasswordRequest(mEmail.getText().toString(), mPassword.getText().toString());
        Callback<MessageResponse> callback2 = new Callback<>() {
            @Override
            public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {
                if (!response.isSuccessful()) {
                    mStatus.setText("Reset Password Error:" + (response.body() == null ? response.message() : response.body().message));
                    System.out.println(response.toString());
                    return;
                }
                Toast.makeText(getApplicationContext(), R.string.FORGOT_PASSWORD_SUCCESS, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                intent.putExtra("Email", mEmail.getText().toString());
                intent.putExtra("Password", mPassword.getText().toString());
                setResult(RESULT_OK, intent);
                finish();
            }
            @Override
            public void onFailure(Call<MessageResponse> call, Throwable throwable) {
                mStatus.setText(throwable.getMessage());
            }
        };
        authManager.resetPassword(request2, callback2);
    }
}