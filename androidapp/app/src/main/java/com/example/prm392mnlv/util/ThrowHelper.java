package com.example.prm392mnlv.util;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class ThrowHelper {

    private ThrowHelper() {}

    public static void throwNullOrBlank(@NonNull Object object, @Nullable String argName) {
        throw new IllegalStateException("Model object of type "
                + object.getClass().getCanonicalName()
                + " cannot be initialized with null, empty, or blank [ "
                + argName
                + " ].");
    }

    public static void throwMustBeNonNegative(@NonNull Object object, @Nullable String argName) {
        throw new IllegalStateException("Model object of type "
                + object.getClass().getCanonicalName()
                + " cannot be initialized with negative [ "
                + argName
                + " ].");
    }
}
