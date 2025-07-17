package com.example.prm392mnlv;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.prm392mnlv.retrofit.repositories.ZaloPayRepository;
import com.example.prm392mnlv.stores.TokenManager;

import java.lang.ref.WeakReference;

import vn.zalopay.sdk.Environment;
import vn.zalopay.sdk.ZaloPaySDK;

public class ApplicationExt extends Application {
    private static ApplicationExt instance;
    private static WeakReference<Activity> mCurrentActivity;

    public ApplicationExt() {
        instance = this;
    }

    public static Context getAppContext() {
        return instance.getApplicationContext();
    }

    @Nullable
    public static Activity getCurrentActivity() {
        if (mCurrentActivity != null) {
            return mCurrentActivity.get();
        }
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        TokenManager.init(getApplicationContext());
        ZaloPaySDK.init(ZaloPayRepository.APP_ID, Environment.SANDBOX);

        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {

            }

            @Override
            public void onActivityStarted(@NonNull Activity activity) {

            }

            @Override
            public void onActivityResumed(@NonNull Activity activity) {
                mCurrentActivity = new WeakReference<>(activity);
            }

            @Override
            public void onActivityPaused(@NonNull Activity activity) {
                if (activity.equals(mCurrentActivity.get())) {
                    mCurrentActivity = null;
                }
            }

            @Override
            public void onActivityStopped(@NonNull Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(@NonNull Activity activity) {

            }
        });
    }
}
