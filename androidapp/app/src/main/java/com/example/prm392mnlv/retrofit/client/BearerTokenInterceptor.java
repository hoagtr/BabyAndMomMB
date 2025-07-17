package com.example.prm392mnlv.retrofit.client;

import androidx.annotation.NonNull;

import com.example.prm392mnlv.stores.TokenManager;

import java.io.IOException;
import java.util.function.Predicate;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class BearerTokenInterceptor implements Interceptor {
    private final Predicate<Request> mCondition;

    public BearerTokenInterceptor() {mCondition = null;}

    public BearerTokenInterceptor(Predicate<Request> condition) {mCondition = condition;}

    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        if (mCondition != null && !mCondition.test(chain.request())) {
            return chain.proceed(chain.request());
        }

        String accessToken = TokenManager.INSTANCE.getTokenBlocking(TokenManager.ACCESS_TOKEN);
        if (accessToken.isEmpty()) {
            return chain.proceed(chain.request());
        }

        Request request = chain.request().newBuilder()
                .addHeader("Authorization", "Bearer " + accessToken)
                .build();

        return chain.proceed(request);
    }
}
