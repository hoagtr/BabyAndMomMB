package com.example.prm392mnlv.ui.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.prm392mnlv.R;
import com.example.prm392mnlv.data.dto.response.LoginResponse;
import com.example.prm392mnlv.retrofit.repositories.AuthManager;
import com.example.prm392mnlv.stores.TokenManager;
import com.example.prm392mnlv.util.TokenHelper;

import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class LoginActivity extends AppCompatActivity {

    private EditText mEmail;
    private EditText mPassword;
    private AuthManager authManager;
    private TextView mStatus;

    private final Pattern emailPattern = Pattern.compile("^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@"
            + "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$");

    private final Pattern passwordPattern = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        String accessToken = TokenManager.INSTANCE.getTokenBlocking(TokenManager.ACCESS_TOKEN);
        if (!accessToken.isEmpty()) {
            Intent homeIntent = new Intent();
            homeIntent.setClass(this, ProductListActivity.class);
            startActivity(homeIntent);
            finish();
        } else {
            configureView();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (authManager == null) authManager = new AuthManager();
    }

    private void configureView() {
        mEmail = findViewById(R.id.editText_Email);
        mPassword = findViewById(R.id.editText_Password);
        mStatus = findViewById(R.id.textView_Status);
        findViewById(R.id.button_Login).setOnClickListener(v -> onLogin());
        findViewById(R.id.textView_ToLogin).setOnClickListener(v -> toRegister());
        findViewById(R.id.textView_ToEmailConfirmation).setOnClickListener(v -> toEmailConfirmation());
        findViewById(R.id.textView_ToForgotPassword).setOnClickListener(v -> toForgotPassword());
    }

    private void onLogin() {
//        if (!validate()) return;

        String email = mEmail.getText().toString().trim();
        String password = mPassword.getText().toString().trim();
        Callback<LoginResponse> callback = new Callback<>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<LoginResponse> call, @NonNull Response<LoginResponse> response) {
                if (!response.isSuccessful()) {
                    switch (response.code()) {
                        case 400:
                            mStatus.setText(R.string.INVALID_EMAIL_OR_NOT_CONFIRMED);
                            return;
                        case 401:
                            mStatus.setText(R.string.INCORRECT_EMAIL_OR_PASSWORD);
                            return;
                        case 404:
                            mStatus.setText(R.string.ERR_EMAIL_NOT_FOUND);
                            return;
                        default:
                            mStatus.setText(response.message());
                            return;
                    }
                }
                assert response.body() != null;
                onLoginSuccess(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<LoginResponse> call, @NonNull Throwable throwable) {
                mStatus.setText(throwable.getMessage());
            }
        };
        authManager.login(email, password, callback);
    }

    private void onLoginSuccess(@NonNull LoginResponse response) {
        mStatus.setText(R.string.success_login);
        TokenManager.INSTANCE.setTokenBlocking(TokenManager.ACCESS_TOKEN, response.accessToken);
        TokenManager.INSTANCE.setTokenBlocking(TokenManager.REFRESH_TOKEN, response.refreshToken);
        TokenHelper.setToken(response.accessToken);

        String role = TokenHelper.getRole();

        Intent intent;
        if ("Staff".equalsIgnoreCase(role)) {
            intent = new Intent(this, MemberListActivity.class);
        } else {
            intent = new Intent(this, ProductListActivity.class);
        }

        startActivity(intent);
        finish();
    }

    //TODO
    private boolean validate() {
        if (mEmail.getText().toString().trim().isEmpty()) {
            mStatus.setText(R.string.ERR_EMAIL_EMPTY);
            return false;
        }

        if (!emailPattern.matcher(mEmail.getText().toString().trim()).matches()) {
            mStatus.setText(R.string.ERR_EMAIL_INVALID);
            return false;
        }

        if (mPassword.getText().toString().trim().isEmpty()) {
            mStatus.setText(R.string.ERR_PASSWORD_EMPTY);
            return false;
        }

        if (!passwordPattern.matcher(mPassword.getText().toString().trim()).matches()) {
            mStatus.setText(R.string.ERR_INVALID_PASSWORD);
            return false;
        }

        mStatus.setText("");
        return true;
    }

    private void toRegister() {
        Intent intent = new Intent();
        intent.setClass(this, RegisterActivity.class);
        getContent.launch(intent);
    }

    private final ActivityResultLauncher<Intent> getContent = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            result -> {
                try {
                    assert result.getData() != null;
                    String email = result.getData().getStringExtra("Email");
                    String password = result.getData().getStringExtra("Password");
                    if (result.getResultCode() == LoginActivity.RESULT_OK) {
                        mEmail.setText(email == null ? "" : email);
                        mPassword.setText(password == null ? "" : password);
                        onLogin();
                    }
                } catch (Exception ignored) {
                }
            });

    private void toEmailConfirmation() {
        Intent intent = new Intent();
        intent.setClass(this, RegisterConfirmationActivity.class);
        intent.putExtra("Email", mEmail.getText().toString().trim());
        startActivity(intent);
    }


    private void toForgotPassword() {
        Intent intent = new Intent();
        intent.setClass(this, ForgotPasswordActivity.class);
        getForgotPasswordResult.launch(intent);
    }

    private ActivityResultLauncher<Intent> getForgotPasswordResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            result -> {
                try {
                    if (result.getResultCode() == LoginActivity.RESULT_OK) {
                        String email = result.getData().getStringExtra("Email");
                        String password = result.getData().getStringExtra("Password");
                        mEmail.setText(email == null ? "" : email);
                        mPassword.setText(password == null ? "" : password);
                        onLogin();
                    }
                } catch (Exception ex) {
                }
            });


}
