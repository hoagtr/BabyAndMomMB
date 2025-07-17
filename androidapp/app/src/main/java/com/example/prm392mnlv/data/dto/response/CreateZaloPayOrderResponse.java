package com.example.prm392mnlv.data.dto.response;

import androidx.annotation.NonNull;

import com.squareup.moshi.Json;

public class CreateZaloPayOrderResponse {
    @Json(name = "return_code")
    public final int returnCode;

    @Json(name = "return_message")
    public final @NonNull String returnMessage;

    @Json(name = "sub_return_code")
    public final int subReturnCode;

    @Json(name = "sub_return_message")
    public final @NonNull String subReturnMessage;

    @Json(name = "order_url")
    public final @NonNull String orderUrl;

    @Json(name = "zp_trans_token")
    public final @NonNull String zpTransToken;

    @Json(name = "order_token")
    public final @NonNull String orderToken;

    @Json(name = "qr_code")
    public final @NonNull String qrCode;

    public CreateZaloPayOrderResponse(int returnCode, @NonNull String returnMessage, int subReturnCode, @NonNull String subReturnMessage, @NonNull String orderUrl, @NonNull String zpTransToken, @NonNull String orderToken, @NonNull String qrCode) {
        this.returnCode = returnCode;
        this.returnMessage = returnMessage;
        this.subReturnCode = subReturnCode;
        this.subReturnMessage = subReturnMessage;
        this.orderUrl = orderUrl;
        this.zpTransToken = zpTransToken;
        this.orderToken = orderToken;
        this.qrCode = qrCode;
    }
}
