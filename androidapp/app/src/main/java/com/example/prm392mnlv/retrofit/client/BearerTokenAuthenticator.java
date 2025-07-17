package com.example.prm392mnlv.retrofit.client;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.example.prm392mnlv.ApplicationExt;
import com.example.prm392mnlv.R;
import com.example.prm392mnlv.data.dto.request.BearerTokenRefreshRequest;
import com.example.prm392mnlv.data.dto.response.RefreshTokenResponse;
import com.example.prm392mnlv.retrofit.services.AuthService;
import com.example.prm392mnlv.stores.TokenManager;
import com.example.prm392mnlv.ui.activities.LoginActivity;
import com.example.prm392mnlv.util.LogHelper;

import java.io.IOException;
import java.util.List;
import java.util.function.Predicate;

import okhttp3.Authenticator;
import okhttp3.Challenge;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;

public class BearerTokenAuthenticator implements Authenticator {
    private static final String TAG = "BearerAuth";

    private static final String TOKEN_REFRESH_ENDPOINT = "/refreshtoken";
    private static final int MAX_RETRIES = 0;

    private final Predicate<Request> mCondition;
    private AuthService mAuthService;

    public BearerTokenAuthenticator() {mCondition = null;}

    public BearerTokenAuthenticator(Predicate<Request> condition) {mCondition = condition;}

    @Nullable
    @Override
    public Request authenticate(Route route, @NonNull Response response) {
        return toLogin();
//        if (mCondition != null && !mCondition.test(response.request())) {
//            return null;
//        }
//
//        if (response.request().url().encodedPath().endsWith(TOKEN_REFRESH_ENDPOINT)) {
//            // We're on the secondary request chain, attempting to refresh our access token.
//            // The server somehow returned a 401. Bail now rather than loop to retry limit.
//            return null;
//        }
//
//        // Check whether our access token expired, or we simply haven't signed in.
//        if (response.request().header("Authorization") == null) {
//            // We haven't signed in.
//            return toLogin();
//        }
//
//        // We'd successfully refreshed the token, but somehow still failed to authenticate.
//        if (retryCount(response) > MAX_RETRIES) {
//            return toLogin();
//        }
//
//        List<Challenge> challenges = response.challenges();
//        if (challenges.isEmpty() || !hasBearerChallenge(challenges)) {
//            return toLogin();
//        }
//
//        String refreshToken = TokenManager.INSTANCE.getTokenBlocking(TokenManager.REFRESH_TOKEN);
//        if (refreshToken.isEmpty()) {
//            return toLogin();
//        }
//
//        String newAccessToken = refreshToken(refreshToken);
//        if (newAccessToken == null) {
//            return toLogin();
//        }
//
//        return response.request().newBuilder()
//                .header("Authorization", "Bearer " + newAccessToken)
//                .build();
    }

    private int retryCount(@NonNull Response response) {
        int count = 0;
        while ((response = response.priorResponse()) != null) {
            ++count;
        }
        return count;
    }

    private boolean hasBearerChallenge(@NonNull List<Challenge> challenges) {
        return challenges.stream().anyMatch(challenge -> "Bearer".equalsIgnoreCase(challenge.scheme()));
    }

    @Nullable
    private Request toLogin() {
        boolean tokensCleared = TokenManager.INSTANCE.clearTokensBlocking();
        if (!tokensCleared) {
            Log.wtf(TAG, "Failed to clear Bearer tokens!");
        }

        new Handler(Looper.getMainLooper()).post(() -> {
            Context context = ApplicationExt.getCurrentActivity();
            if (context != null) {
                new AlertDialog.Builder(context)
                        .setMessage(R.string.err_auth_return_home)
                        .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                            Intent intent = new Intent(context, LoginActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            context.startActivity(intent);
                        })
                        .show();
            }
        });

        return null;
    }

    @Nullable
    private String refreshToken(String refreshToken) {
        if (mAuthService == null) {
            mAuthService = ApiClient.getClient().create(AuthService.class);
        }

        try {
            BearerTokenRefreshRequest req = new BearerTokenRefreshRequest(refreshToken);
            retrofit2.Response<RefreshTokenResponse> response = mAuthService.refreshToken(req).execute();
            if (!response.isSuccessful()) {
                LogHelper.logErrorResponse(TAG, response);
                return null;
            }

            RefreshTokenResponse refTknResponse = response.body();
            assert refTknResponse != null;
            Log.i(TAG, "Successfully refreshed Bearer token\nAccess token: " + refTknResponse.accessToken + "\nRefresh token: " + refTknResponse.refreshToken);

            boolean accTknSaved = TokenManager.INSTANCE.setTokenBlocking(TokenManager.ACCESS_TOKEN, refTknResponse.accessToken);
            boolean refTknSaved = TokenManager.INSTANCE.setTokenBlocking(TokenManager.REFRESH_TOKEN, refTknResponse.refreshToken);
            if (!accTknSaved) {
                // Save the refresh token anyway. We'll need it quite soon.
                Log.wtf(TAG, "Failed to save access token!");
                return null;
            }
            if (!refTknSaved) {
                // Save the access token anyway. Maybe the user will log off in a few minutes...
                Log.wtf(TAG, "Failed to save refresh token!");
            }

            return refTknResponse.accessToken;
        } catch (IOException e) {
            LogHelper.logFailure(TAG, e);
            return null;
        }
    }
}
