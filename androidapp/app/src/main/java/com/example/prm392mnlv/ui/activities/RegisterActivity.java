package com.example.prm392mnlv.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.prm392mnlv.R;
import com.example.prm392mnlv.data.dto.response.MessageResponse;
import com.example.prm392mnlv.retrofit.repositories.AuthManager;

import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private EditText mName;
    private EditText mEmail;
    private EditText mPhoneNumber;
    private EditText mPassword;
    private EditText mConfirmPassword;
    private TextView mStatus;
    private AuthManager authManager;

    private final Pattern emailPattern = Pattern.compile("^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@"
            + "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$");

    private final Pattern passwordPattern = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        configureView();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (authManager == null) authManager = new AuthManager();
    }

    private void configureView() {
        mName = findViewById(R.id.editText_Name);
        mEmail = findViewById(R.id.editText_Email);
        mPhoneNumber = findViewById(R.id.editText_PhoneNumber);
        mPassword = findViewById(R.id.editText_Password);
        mConfirmPassword = findViewById(R.id.editText_ConfirmPassword);
        mStatus = findViewById(R.id.textView_Status);
        findViewById(R.id.button_Submit).setOnClickListener(v -> onRegister());
        findViewById(R.id.textView_ToLogin).setOnClickListener(v -> {
            finish();
        });
    }

    private void onRegister() {
        if (!validate()) return;

        String name = mName.getText().toString().trim();
        String phoneNumber = mPhoneNumber.getText().toString().trim();
        String email = mEmail.getText().toString().trim();
        String password = mPassword.getText().toString().trim();
        Callback<MessageResponse> callback = new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<MessageResponse> call, @NonNull Response<MessageResponse> response) {
                if (!response.isSuccessful()) {
                    mStatus.setText(response.body() == null ? response.message() : response.body().message);
                    return;
                }
                Toast.makeText(getApplicationContext(), R.string.REGISTER_CONFIRMATION_MAIL_SENT, Toast.LENGTH_SHORT).show();
                toRegisterConfirmation();
            }

            @Override
            public void onFailure(@NonNull Call<MessageResponse> call, @NonNull Throwable throwable) {
                mStatus.setText(throwable.getMessage());
            }
        };
        authManager.register(name, email, phoneNumber, password, callback);
    }

    private boolean validate() {
        if (mName.getText().toString().trim().isEmpty()) {
            mStatus.setText(R.string.ERR_NAME_EMPTY);
            return false;
        }

        if (mEmail.getText().toString().trim().isEmpty()) {
            mStatus.setText(R.string.ERR_EMAIL_EMPTY);
            return false;
        }

        if (!emailPattern.matcher(mEmail.getText().toString().trim()).matches()) {
            mStatus.setText(R.string.ERR_EMAIL_INVALID);
            return false;
        }

        if (mPhoneNumber.getText().toString().trim().isEmpty()) {
            mStatus.setText(R.string.ERR_PHONE_NUMBER_EMPTY);
            return false;
        }

        if (mPhoneNumber.getText().toString().length() != 10){
            mStatus.setText(R.string.ERR_PHONE_NUMBER_INVALID);
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

        if (!mPassword.getText().toString().trim().equals(mConfirmPassword.getText().toString().trim())) {
            mStatus.setText(R.string.ERR_PASSWORD_NOT_MATCH);
            return false;
        }

        mStatus.setText("");
        return true;
    }
    private void toRegisterConfirmation(){
        Intent intent = new Intent();
        intent.setClass(this, RegisterConfirmationActivity.class);
        intent.putExtra("Email", mEmail.getText().toString().trim());
        getContent.launch(intent);
    }
    private ActivityResultLauncher<Intent> getContent = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RegisterActivity.RESULT_OK){
                    Intent intent1 = new Intent();
                    intent1.putExtra("Email", mEmail.getText().toString());
                    intent1.putExtra("Password", mPassword.getText().toString());
                    setResult(RegisterActivity.RESULT_OK, intent1);
                    finish();
                }
            }
    );


}
