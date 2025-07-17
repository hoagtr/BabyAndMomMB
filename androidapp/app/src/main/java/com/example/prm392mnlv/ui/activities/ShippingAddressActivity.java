package com.example.prm392mnlv.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.prm392mnlv.R;
import com.example.prm392mnlv.data.dto.response.MessageResponse;
import com.example.prm392mnlv.data.models.User;
import com.example.prm392mnlv.retrofit.repositories.UserRepository;
import com.example.prm392mnlv.util.LogHelper;
import com.example.prm392mnlv.util.ViewHelper;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShippingAddressActivity extends AppCompatActivity {
    private static final String TAG = "ChangeShippingAddress";

    public static final String USER_KEY = "user";
    public static final String RESULT_KEY = "updatedUser";

    private UserRepository mUserRepo;

    private TextInputLayout mEtAddress;

    private User mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shipping_address);

        mUserRepo = new UserRepository();

        mEtAddress = findViewById(R.id.etLayoutShippingAddress);

        mUser = getIntent().getParcelableExtra(USER_KEY);

        Objects.requireNonNull(mEtAddress.getEditText()).setText(mUser.getShippingAddress());
        mEtAddress.setEndIconMode(TextInputLayout.END_ICON_CLEAR_TEXT);

        mEtAddress.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                getAndValidateInput();
            }
        });

        findViewById(R.id.btnConfirm).setOnClickListener(this::updateShippingAddress);
    }

    private void updateShippingAddress(View v) {
        String address = getAndValidateInput();
        if (address == null) return;

        mUser.setShippingAddress(address);
        mUserRepo.updateUser(mUser, new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<MessageResponse> call, @NonNull Response<MessageResponse> response) {
                if (!response.isSuccessful()) {
                    LogHelper.logErrorResponse(TAG, response);
                    ViewHelper.showAlert(ShippingAddressActivity.this, R.string.err_shipping_addr_update_failed);
                    return;
                }
                returnResult();
            }

            @Override
            public void onFailure(@NonNull Call<MessageResponse> call, @NonNull Throwable throwable) {
                LogHelper.logFailure(TAG, throwable);
                ViewHelper.showAlert(ShippingAddressActivity.this, R.string.err_shipping_addr_update_failed);
            }
        });
    }

    private void returnResult() {
        Intent result = new Intent();
        result.putExtra(RESULT_KEY, mUser);
        setResult(Activity.RESULT_OK, result);
        finish();
    }

    @Nullable
    private String getAndValidateInput() {
        String address = Objects.requireNonNull(mEtAddress.getEditText()).getText().toString();
        if (address.isBlank()) {
            mEtAddress.setError("Please input an address.");
            return null;
        }
        if (address.length() < 5) {
            mEtAddress.setError("Address too short. Must be at least 5 characters long.");
            return null;
        }
        return address;
    }
}
