package com.example.prm392mnlv.retrofit.client;

import android.annotation.SuppressLint;

import com.example.prm392mnlv.retrofit.json.PathedJsonAdapterFactory;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter;

import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;

public class ApiClient {
    private static final String MOCK_URL = "https://6713c00e690bf212c75fa0c3.mockapi.io/";
    private static final String DEV_URL = "http://10.0.2.2:5055/api/";
    private static final String PROD_URL = "http://14.225.253.234:4000/api/";

    private static final Retrofit retrofit;

    public static Retrofit getClient() {
        return retrofit;
    }

    static {
        // Create a trust manager that does not validate certificate chains
        @SuppressLint("CustomX509TrustManager") final TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    @SuppressLint("TrustAllX509TrustManager")
                    @Override
                    public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                    }

                    @SuppressLint("TrustAllX509TrustManager")
                    @Override
                    public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                    }

                    @Override
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return new java.security.cert.X509Certificate[]{};
                    }
                }
        };

        // Install the all-trusting trust manager
        final SSLContext sslContext;
        try {
            sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // Create an ssl socket factory with our all-trusting manager
        final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

        final HostnameVerifier trustEveryone = (hostname, session) -> true;

        String baseUrl = DEV_URL;
        Predicate<Request> isInternalApi = request -> request.url().toString().startsWith(baseUrl);

        OkHttpClient client = new OkHttpClient.Builder()
                .sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0])
                .hostnameVerifier(trustEveryone)
                .addInterceptor(new BearerTokenInterceptor(isInternalApi))
                .authenticator(new BearerTokenAuthenticator(isInternalApi))
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .build();

        retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(client)
                .addConverterFactory(MoshiConverterFactory.create(
                        new Moshi.Builder()
                                .add(new PathedJsonAdapterFactory())
                                .add(Date.class, new Rfc3339DateJsonAdapter())
                                .build()))
                .build();
    }
}
