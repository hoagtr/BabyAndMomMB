package com.example.prm392mnlv.stores;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.datastore.preferences.core.MutablePreferences;
import androidx.datastore.preferences.core.Preferences;
import androidx.datastore.preferences.core.PreferencesKeys;
import androidx.datastore.preferences.rxjava3.RxPreferenceDataStoreBuilder;
import androidx.datastore.rxjava3.RxDataStore;

import io.reactivex.rxjava3.core.Single;

public enum TokenManager {
    INSTANCE;

    private static final String STORE_NAME = "settings";
    private static boolean mInit;

    public static synchronized void init(Context context) {
        if (mInit) return;
        INSTANCE.mDataStore = new RxPreferenceDataStoreBuilder(context, STORE_NAME).build();
        mInit = true;
    }

    private RxDataStore<Preferences> mDataStore;

    // Empty Prefs for use as return value on errors.
    public static final Preferences PREFS_ERROR = new MutablePreferences();

    public static final Preferences.Key<String> ACCESS_TOKEN = PreferencesKeys.stringKey("access_token");
    public static final Preferences.Key<String> REFRESH_TOKEN = PreferencesKeys.stringKey("refresh_token");

    @NonNull
    public String getTokenBlocking(Preferences.Key<String> tokenType) {
        Single<String> token = mDataStore.data().firstOrError().map(prefs -> prefs.get(tokenType)).onErrorReturnItem("");
        return token.blockingGet();
    }

    @NonNull
    public Single<String> getTokenNonBlocking(Preferences.Key<String> tokenType) {
        return mDataStore.data().firstOrError().map(prefs -> prefs.get(tokenType)).onErrorReturnItem("");
    }

    public boolean setTokenBlocking(Preferences.Key<String> tokenType, String tokenValue) {
        Single<Preferences> updateResult = mDataStore.updateDataAsync(prefsIn -> {
            MutablePreferences mutPrefs = prefsIn.toMutablePreferences();
            mutPrefs.set(tokenType, tokenValue);
            return Single.just(mutPrefs);
        }).onErrorReturnItem(PREFS_ERROR);

        return updateResult.blockingGet() != PREFS_ERROR;
    }

    @NonNull
    public Single<Preferences> setTokenNonBlocking(Preferences.Key<String> tokenType, String tokenValue) {
        return mDataStore
                .updateDataAsync(prefsIn -> {
                    MutablePreferences mutPrefs = prefsIn.toMutablePreferences();
                    mutPrefs.set(tokenType, tokenValue);
                    return Single.just(mutPrefs);
                })
                .onErrorReturnItem(PREFS_ERROR);
    }

    public boolean clearTokensBlocking() {
        Single<Preferences> updateResult = mDataStore.updateDataAsync(prefsIn -> {
            MutablePreferences mutPrefs = prefsIn.toMutablePreferences();
            mutPrefs.remove(ACCESS_TOKEN);
            mutPrefs.remove(REFRESH_TOKEN);
            return Single.just(mutPrefs);
        }).onErrorReturnItem(PREFS_ERROR);

        return updateResult.blockingGet() != PREFS_ERROR;
    }

    @NonNull
    public Single<Preferences> clearTokensNonBlocking() {
        return mDataStore
                .updateDataAsync(prefsIn -> {
                    MutablePreferences mutPrefs = prefsIn.toMutablePreferences();
                    mutPrefs.remove(ACCESS_TOKEN);
                    mutPrefs.remove(REFRESH_TOKEN);
                    return Single.just(mutPrefs);
                })
                .onErrorReturnItem(PREFS_ERROR);
    }
}
