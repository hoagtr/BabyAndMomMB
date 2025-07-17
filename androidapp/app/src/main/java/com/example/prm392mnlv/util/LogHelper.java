package com.example.prm392mnlv.util;

import android.util.Log;

import androidx.annotation.NonNull;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Response;

public final class LogHelper {

    private LogHelper() {}

    public static void logErrorResponse(String tag, @NonNull Response<?> response) {
        try (ResponseBody body = response.errorBody()) {
            assert body != null;
            Log.e(tag, "onResponse error: " + response.message() + "\n" + body.string());
        } catch (IOException e) {
            Log.e(tag, "onResponse error: ", e);
        }
    }

    public static void logFailure(String tag, Throwable throwable) {
        Log.e(tag, "onFailure: ", throwable);
    }
}
