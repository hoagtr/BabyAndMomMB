package com.example.prm392mnlv.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.prm392mnlv.R;
import com.example.prm392mnlv.data.dto.response.MessageResponse;
import com.example.prm392mnlv.data.models.CartItem;
import com.example.prm392mnlv.retrofit.repositories.CartItemRepository;
import com.example.prm392mnlv.retrofit.repositories.CategoryRepository;
import com.example.prm392mnlv.util.LogHelper;
import com.example.prm392mnlv.util.TextUtils;
import com.example.prm392mnlv.util.ViewHelper;

import java.math.BigDecimal;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductDetailsActivity extends AppCompatActivity {
    private static final String TAG = "ProductDetails";

    private CategoryRepository mCategoryRepo;
    private CartItemRepository mCartRepo;

    private ImageView ivProductImage;
    private TextView tvProductName, tvDescription, tvPrice, tvStock, tvCategory, tvQuantity;
    private ImageButton btnDecrease, btnIncrease;
    private Button btnAddToCart;

    private int mQuantity = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        mCategoryRepo = new CategoryRepository();
        mCartRepo = new CartItemRepository();

        ivProductImage = findViewById(R.id.ivProductImage);
        tvProductName = findViewById(R.id.tvProductName);
        tvDescription = findViewById(R.id.tvDescription);
        tvPrice = findViewById(R.id.tvPrice);
        tvStock = findViewById(R.id.tvStock);
        tvCategory = findViewById(R.id.tvCategory);
        tvQuantity = findViewById(R.id.tvQuantity);
        btnDecrease = findViewById(R.id.btnDecrease);
        btnIncrease = findViewById(R.id.btnIncrease);
        btnAddToCart = findViewById(R.id.btnAddToCart);

        // Lấy dữ liệu từ Intent
        if (getIntent() != null) {
            String productId = getIntent().getStringExtra("productId");
            String productName = getIntent().getStringExtra("productName");
            String description = getIntent().getStringExtra("description");
            BigDecimal price = (BigDecimal) getIntent().getSerializableExtra("price");
            int quantityInStock = getIntent().getIntExtra("quantityInStock", 0);
            String imageUrl = getIntent().getStringExtra("imageUrl");
            String categoryName = getIntent().getStringExtra("categoryName");

            assert price != null;
            tvProductName.setText(productName);
            tvDescription.setText(description);
            tvPrice.setText(TextUtils.formatPrice(price));
            tvStock.setText(String.format(Locale.getDefault(), "Stock: %d", quantityInStock));
            tvCategory.setText(String.format(Locale.getDefault(), "Category: %s", categoryName));

            Glide.with(this)
                    .load(imageUrl)
                    .placeholder(R.drawable.placeholder_product)
                    .into(ivProductImage);

            updateControls(quantityInStock);

            btnDecrease.setOnClickListener(v -> {
                --mQuantity;
                updateControls(quantityInStock);
            });
            btnIncrease.setOnClickListener(v -> {
                ++mQuantity;
                updateControls(quantityInStock);
            });

            btnAddToCart.setOnClickListener(v -> {
                assert productId != null;
                CartItem cartItem = new CartItem();
                cartItem.setProductId(productId);
                cartItem.setQuantity(mQuantity);
                addToCart(cartItem);
            });
        }
    }

    private void updateControls(int quantityInStock) {
        tvQuantity.setText(String.valueOf(mQuantity));

        if (mQuantity > 1) {
            ViewHelper.enableClipArtButton(btnDecrease);
        } else {
            ViewHelper.disableClipArtButton(btnDecrease);
        }
        if (mQuantity < quantityInStock) {
            ViewHelper.enableClipArtButton(btnIncrease);
        } else {
            ViewHelper.disableClipArtButton(btnIncrease);
        }
    }

    private void addToCart(CartItem cartItem) {
        mCartRepo.createCartItem(cartItem, new Callback<>() {
            private static final String TAG = "AddToCart";

            @Override
            public void onResponse(@NonNull Call<MessageResponse> call, @NonNull Response<MessageResponse> response) {
                if (!response.isSuccessful()) {
                    LogHelper.logErrorResponse(TAG, response);
                    ViewHelper.showAlert(ProductDetailsActivity.this, R.string.err_product_details_add_to_cart_failed);
                    return;
                }

                new AlertDialog.Builder(ProductDetailsActivity.this)
                        .setMessage(R.string.success_product_details_add_to_cart)
                        .setPositiveButton(R.string.btn_buy_now, (dialog, which) -> startActivity(new Intent(ProductDetailsActivity.this, CartActivity.class)))
                        .setNeutralButton(android.R.string.ok, (dialog, which) -> {})
                        .show();
            }

            @Override
            public void onFailure(@NonNull Call<MessageResponse> call, @NonNull Throwable throwable) {
                Log.e(TAG, "onFailure: ", throwable);
                ViewHelper.showAlert(ProductDetailsActivity.this, R.string.err_product_details_add_to_cart_failed);
            }
        });
    }
}
