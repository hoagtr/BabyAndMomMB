package com.example.prm392mnlv.data.models;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.prm392mnlv.R;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public final class PaymentMethods {
    private static final Map<Integer, PaymentMethod> PAYMENT_METHODS = new HashMap<>() {{
        put(1, new PaymentMethod(1, "Cash on delivery", R.drawable.ic_cash));
        put(2, new PaymentMethod(2, "ZaloPay", R.drawable.zalo));
    }};

    private PaymentMethods() {}

    @Nullable
    public static PaymentMethod get(int id) {
        return PAYMENT_METHODS.getOrDefault(id, null);
    }

    @NonNull
    public static Collection<Integer> getIds() {
        return PAYMENT_METHODS.keySet();
    }

    @NonNull
    public static Collection<PaymentMethod> getMethods() {
        return PAYMENT_METHODS.values();
    }
}
