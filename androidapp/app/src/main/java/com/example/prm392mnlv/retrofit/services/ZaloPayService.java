package com.example.prm392mnlv.retrofit.services;

import com.example.prm392mnlv.data.dto.response.CreateZaloPayOrderResponse;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Url;

public interface ZaloPayService {

    @FormUrlEncoded
    @POST
    Call<CreateZaloPayOrderResponse> createOrder(@Url String url,
                                                 @Field("app_id") String appId,
                                                 @Field("app_user") String appUser,
                                                 @Field("app_time") String appTime,
                                                 @Field("amount") String amount,
                                                 @Field("app_trans_id") String appTransId,
                                                 @Field("embed_data") String embedData,
                                                 @Field("item") String item,
                                                 @Field("bank_code") String bankCode,
                                                 @Field("description") String description,
                                                 @Field("mac") String mack);
}
