package com.example.prm392mnlv.util;

import androidx.annotation.NonNull;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

public final class TextUtils {
    private static final NumberFormat formatter;

    private TextUtils() {}

    static {
        formatter = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("vi-VN"));
        formatter.setMaximumFractionDigits(0);
    }

    public static boolean isNullOrEmpty(String str) {
        return (null == str || str.isEmpty());
    }

    public static boolean isNullOrBlank(String str) {
        return (null == str || str.isBlank());
    }

    public static String formatPrice(@NonNull BigDecimal price) {
        return formatter.format(price);
    }

    private static final String PHONE_NUMBER_PATTERN = "\\d{10}";

    @NonNull
    public static String formatPhoneNumber(@NonNull String phoneNumber) {
        phoneNumber = phoneNumber.strip().replaceAll("\\D", "");
        if (phoneNumber.length() != 10) {
            throw new IllegalArgumentException("Phone number must contain exactly 10 digits.");
        }
        return String.format("(+84) %s %s %s", phoneNumber.substring(1, 4), phoneNumber.substring(4, 7), phoneNumber.substring(7, 10));
    }
}
