package com.example.prm392mnlv.retrofit.repositories;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.prm392mnlv.data.dto.response.CreateZaloPayOrderResponse;
import com.example.prm392mnlv.retrofit.client.ApiClient;
import com.example.prm392mnlv.retrofit.services.ZaloPayService;

import org.jetbrains.annotations.Contract;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import retrofit2.Callback;

public class ZaloPayRepository {
    private static final String TAG = "ZaloPayRepo";

    public static final int APP_ID = 2553;
    private static final String MAC_KEY = "PcY4iZIKFCIdgZvA6ueMcMHHUbRLYjPL";
    private static final String CREATE_ORDER_URL = "https://sb-openapi.zalopay.vn/v2/create";
    public static final String CALLBACK_URI = "zpdk://mnlv";

    private final ZaloPayService mService;

    public ZaloPayRepository() {
        mService = ApiClient.getClient().create(ZaloPayService.class);
    }

    public void createOrder(String userIdentifier, long transactionAmount, Callback<CreateZaloPayOrderResponse> callback) {
        String appId = String.valueOf(APP_ID);
        String appUser = "Android_Demo";
        String appTime = String.valueOf(ZonedDateTime.now().toInstant().toEpochMilli());
        String amount = String.valueOf(transactionAmount);
        String appTransId = getAppTransId();
        String embedData = "{}";
        String item = "[]";
        String bankCode = "zalopayapp";
        String description = "Payment for order #" + appTransId;

        String macText = String.format(Locale.getDefault(), "%s|%s|%s|%s|%s|%s|%s", appId, appTransId, appUser, amount, appTime, embedData, item);
        String mac = computeHmac(macText);

        mService.createOrder(CREATE_ORDER_URL, appId, appUser, appTime, amount, appTransId, embedData, item, bankCode, description, mac)
                .enqueue(callback);
    }

    private static final DateTimeFormatter DATE_TIME_FORMATTER = new DateTimeFormatterBuilder()
            .appendValueReduced(ChronoField.YEAR, 2, 2, LocalDate.of(1960, 1, 1))
            .appendValue(ChronoField.MONTH_OF_YEAR, 2)
            .appendValue(ChronoField.DAY_OF_MONTH, 2)
            .appendLiteral("_")
            .appendValue(ChronoField.HOUR_OF_DAY, 2)
            .appendValue(ChronoField.MINUTE_OF_HOUR, 2)
            .appendValue(ChronoField.SECOND_OF_MINUTE, 2)
            .toFormatter(Locale.forLanguageTag("vi-VN"));

    private final AtomicInteger appTransSeed = new AtomicInteger(0);

    @NonNull
    private String getAppTransId() {
        appTransSeed.compareAndSet(100_000, 0);
        int seed = appTransSeed.incrementAndGet();
        return String.format(Locale.getDefault(), "%s%06d", LocalDateTime.now().format(DATE_TIME_FORMATTER), seed);
    }

    private static final String HMAC_ALGORITHM = "HmacSHA256";

    private static String computeHmac(String input) {
        try {
            Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            SecretKeySpec signingKey = new SecretKeySpec(MAC_KEY.getBytes(StandardCharsets.UTF_8), HMAC_ALGORITHM);
            mac.init(signingKey);
            byte[] hmacBytes = mac.doFinal(input.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(hmacBytes);
        } catch (Exception e) {
            Log.e(TAG, "computeHmac: ", e);
        }
        return input;
    }

    private static final char[] CHARSET = "0123456789abcdef".toCharArray();

    @NonNull
    @Contract("_ -> new")
    private static String bytesToHex(@NonNull byte[] input) {
        int idx = 0, v;
        char[] output = new char[input.length * 2];
        for (byte b : input) {
            v = b & 0xFF;
            output[idx++] = CHARSET[v >>> 4];
            output[idx++] = CHARSET[v & 0x0F];
        }
        return new String(output);
    }
}
