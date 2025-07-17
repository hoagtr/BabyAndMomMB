package com.example.prm392mnlv.ui.activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.prm392mnlv.R;
import com.example.prm392mnlv.data.models.PaymentMethod;
import com.example.prm392mnlv.data.models.ShippingMethod;
import com.example.prm392mnlv.util.TextUtils;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;

public class OrderSummaryActivity extends AppCompatActivity {
    private static final String TAG = "OrderSummary";

    public static final String ORDER_TOTAL_KEY = "orderTotal";
    public static final String PAYMENT_METHOD_KEY = "paymentMethod";
    public static final String SHIPPING_METHOD_KEY = "shippingMethod";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_confirmation);

        Intent intent = getIntent();
        BigDecimal orderTotal = (BigDecimal) intent.getSerializableExtra(ORDER_TOTAL_KEY);
        PaymentMethod paymentMethod = intent.getParcelableExtra(PAYMENT_METHOD_KEY);
        ShippingMethod shippingMethod = intent.getParcelableExtra(SHIPPING_METHOD_KEY);

        try {
            assert orderTotal != null;
            assert paymentMethod != null;
            assert shippingMethod != null;
        } catch (AssertionError e) {
            Log.e(TAG, "Order summary was not provided with proper order data.", e);
            throw new RuntimeException(e);
        }

        TextView mTvOrderTotal = findViewById(R.id.tvOrderTotal);
        TextView mTvPaymentMethod = findViewById(R.id.tvPaymentMethod);
        TextView mTvShippingMethod = findViewById(R.id.tvShippingMethod);
        TextView mTvEstimatedDelivery = findViewById(R.id.tvEstimatedDelivery);

        mTvOrderTotal.setText(TextUtils.formatPrice(orderTotal));
        mTvPaymentMethod.setText(paymentMethod.getName());
        mTvShippingMethod.setText(shippingMethod.getName());
        if (shippingMethod.getDeliveryTime() != null) {
            mTvEstimatedDelivery.setText(shippingMethod.getDeliveryTime().format(DateTimeFormatter.ofPattern("dd MMM yyyy")));
        } else {
            mTvEstimatedDelivery.setText(R.string.not_available);
            mTvEstimatedDelivery.setTypeface(null, Typeface.ITALIC);
        }

        findViewById(R.id.btnHome).setOnClickListener(v -> {
            Intent homeIntent = new Intent(OrderSummaryActivity.this, ProductListActivity.class);
            startActivity(homeIntent);
            finish();
        });
    }
}
