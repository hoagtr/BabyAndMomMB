package com.example.prm392mnlv.ui.activities;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.prm392mnlv.R;
import com.example.prm392mnlv.data.dto.request.RegisterConfirmationRequest;
import com.example.prm392mnlv.data.dto.request.ResendOtpRequest;
import com.example.prm392mnlv.data.dto.response.MessageResponse;
import com.example.prm392mnlv.retrofit.repositories.AuthManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterConfirmationActivity extends AppCompatActivity {

    private EditText mEmail;
    private EditText mOtp;
    private TextView mStatus;

    private AuthManager authManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register_confirmation);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        configureView();
    }

    private void configureView() {
        mEmail = findViewById(R.id.editText_Email);
        mEmail.setText(getIntent().getStringExtra("Email"));
        mOtp = findViewById(R.id.editText_Otp);
        mStatus = findViewById(R.id.textView_Status);
        authManager = new AuthManager();
        findViewById(R.id.button_Submit).setOnClickListener(v -> onRegisterConfirm());
        findViewById(R.id.button_ResendOtp).setOnClickListener(v -> onResendConfirmationEmail());
        findViewById(R.id.imageView_Back).setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
    }


    private void onRegisterConfirm(){
        if (!validate()) return;
        RegisterConfirmationRequest request = new RegisterConfirmationRequest(
                    mEmail.getText().toString(), mOtp.getText().toString());

        Callback<MessageResponse> callback = new Callback<MessageResponse>() {
            @Override
            public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {
                if (!response.isSuccessful()){
                    mStatus.setText(response.body() == null ? response.message() : response.body().message);
                    return;
                }
                Toast.makeText(getApplicationContext(), R.string.EMAIL_CONFIRMATION_SUCCESS, Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
            }

            @Override
            public void onFailure(Call<MessageResponse> call, Throwable throwable) {
                mStatus.setText(throwable.getMessage());
                return;
            }
        };
        authManager.confirmEmail(request, callback);
    }

    private void onResendConfirmationEmail(){
        ResendOtpRequest request = new ResendOtpRequest(mEmail.getText().toString());
        Callback<MessageResponse> callback = new Callback<>() {
            @Override
            public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {
                if (!response.isSuccessful()){
                    mStatus.setText(response.body() == null ? response.message() : response.body().message);
                    System.out.println(response.toString());
                    return;
                }
                Toast.makeText(getApplicationContext(), R.string.REGISTER_CONFIRMATION_MAIL_SENT, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<MessageResponse> call, Throwable throwable) {
                mStatus.setText(throwable.getMessage());
            }
        };
        authManager.resendConfirmationEmail(request, callback);
    }

    private boolean validate(){
        if (mEmail.getText().toString().trim().isEmpty()){
            mStatus.setText(R.string.ERR_EMAIL_EMPTY);
            return false;
        }
        if (mOtp.getText().toString().length() != 6){
            mStatus.setText(R.string.ERR_OTP_EMPTY);
            return false;
        }
        mStatus.setText("");
        return true;
    }
}