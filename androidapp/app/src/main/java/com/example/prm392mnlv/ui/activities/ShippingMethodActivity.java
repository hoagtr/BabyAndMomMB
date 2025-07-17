package com.example.prm392mnlv.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.prm392mnlv.R;
import com.example.prm392mnlv.data.models.ShippingMethod;
import com.example.prm392mnlv.util.TextUtils;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ShippingMethodActivity extends AppCompatActivity {

    public static final String SELECTED_METHOD_ID_KEY = "selectedMethod";
    public static final String SHIPPING_METHODS_KEY = "shippingMethods";
    public static final String RESULT_KEY = "SelectedId";

    private RadioGroup mRgShippingMethods;

    private int mSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shipping_method);

        Intent intent = getIntent();
        mSelected = intent.getIntExtra(SELECTED_METHOD_ID_KEY, 1);
        Parcelable[] parcels = intent.getParcelableArrayExtra(SHIPPING_METHODS_KEY);
        if (parcels == null) {
            throw new IllegalStateException("Shipping method activity must be started with a list of shipping methods.");
        }
        List<ShippingMethod> shippingMethods = Arrays.stream(parcels)
                .map(e -> (ShippingMethod) e)
                .collect(Collectors.toList());

        mRgShippingMethods = findViewById(R.id.rgShippingMethods);

        LayoutInflater inflater = getLayoutInflater();
        for (ShippingMethod method : shippingMethods) {
            View view = inflater.inflate(R.layout.item_shipping_method, mRgShippingMethods, false);

            TextView tvShippingMethod = view.findViewById(R.id.tvShippingMethod);
            TextView tvShippingFee = view.findViewById(R.id.tvShippingFee);
            TextView tvEstimatedDelivery = view.findViewById(R.id.tvEstimatedDelivery);
            RadioButton rbSelect = view.findViewById(R.id.rbSelect);

            tvShippingMethod.setText(method.getName());
            if (method.getFeeClass() == 0) {
                tvShippingFee.setText(TextUtils.formatPrice(BigDecimal.ZERO));
            } else if (method.getFee() == null) {
                tvShippingFee.setText(R.string.undetermined);
                tvShippingFee.setTypeface(null, Typeface.ITALIC);
            } else {
                tvShippingFee.setText(TextUtils.formatPrice(method.getFee()));
                tvShippingFee.setTypeface(null, Typeface.NORMAL);
            }
            if (method.getSpeedClass() == 0) {
                tvEstimatedDelivery.setText(R.string.not_available);
                tvEstimatedDelivery.setTypeface(null, Typeface.ITALIC);
            } else if (method.getDeliveryTime() == null) {
                tvEstimatedDelivery.setText(R.string.undetermined);
                tvEstimatedDelivery.setTypeface(null, Typeface.ITALIC);
            } else {
                tvEstimatedDelivery.setText(method.getDeliveryTime().format(DateTimeFormatter.ISO_LOCAL_DATE));
                tvEstimatedDelivery.setTypeface(null, Typeface.NORMAL);
            }

            rbSelect.setId(method.getId());

            view.setOnClickListener(v -> {
                rbSelect.setChecked(true);
                mSelected = method.getId();
            });

            mRgShippingMethods.addView(view);
        }

        mRgShippingMethods.check(mSelected);

        findViewById(R.id.btnConfirm).setOnClickListener(v -> {
            Intent result = new Intent();
            result.putExtra(RESULT_KEY, mSelected);
            setResult(Activity.RESULT_OK, result);
            finish();
        });
    }
}
