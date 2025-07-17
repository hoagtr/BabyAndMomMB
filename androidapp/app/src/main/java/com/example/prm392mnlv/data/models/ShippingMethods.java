package com.example.prm392mnlv.data.models;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public final class ShippingMethods {

    private static final Map<Integer, ShippingMethod> SHIPPING_METHODS = new HashMap<>() {{
        put(1, new ShippingMethod(1, "In-store pickup", 0, 0));
        put(2, new ShippingMethod(2, "Delivery", 1, 1));
    }};

    private ShippingMethods() {}

    @Nullable
    public static ShippingMethod get(int id) {
        return SHIPPING_METHODS.getOrDefault(id, null);
    }

    @NonNull
    public static Collection<Integer> getIds() {
        return SHIPPING_METHODS.keySet();
    }

    @NonNull
    public static Collection<ShippingMethod> getMethods() {
        return SHIPPING_METHODS.values();
    }
}
