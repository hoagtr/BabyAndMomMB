package com.example.prm392mnlv.util;

import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public final class ImageUtils {
    private static final String TAG = "ImageUtils";

    private ImageUtils() {}

    @Nullable
    public static Drawable fetchDrawable(@NonNull Uri uri) {
        try {
            URL url = new URL(uri.toString());
            InputStream inputStream = (InputStream) url.getContent();
            return Drawable.createFromStream(inputStream, null);
        } catch (IOException e) {
            Log.e(TAG, "fetchDrawable: ", e);
            return null;
        }
    }
}
