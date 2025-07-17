package com.example.prm392mnlv.ui.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392mnlv.R;
import com.example.prm392mnlv.data.dto.response.CartItemResponse;
import com.example.prm392mnlv.data.dto.response.CategoryResponse;
import com.example.prm392mnlv.data.dto.response.MessageResponse;
import com.example.prm392mnlv.data.dto.response.ProductResponse;
import com.example.prm392mnlv.data.mappings.CartItemMapper;
import com.example.prm392mnlv.data.mappings.CategoryMapper;
import com.example.prm392mnlv.data.mappings.ProductMapper;
import com.example.prm392mnlv.data.models.CartItem;
import com.example.prm392mnlv.data.models.Category;
import com.example.prm392mnlv.data.models.Product;
import com.example.prm392mnlv.retrofit.repositories.CartItemRepository;
import com.example.prm392mnlv.retrofit.repositories.CategoryRepository;
import com.example.prm392mnlv.retrofit.repositories.ProductRepository;
import com.example.prm392mnlv.ui.adapters.CartItemAdapter;
import com.example.prm392mnlv.ui.adapters.CartItemTouchCallback;
import com.example.prm392mnlv.util.LogHelper;
import com.example.prm392mnlv.util.NotificationHelper;
import com.example.prm392mnlv.util.TextUtils;
import com.example.prm392mnlv.util.ViewHelper;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartActivity
        extends AppCompatActivity
        implements CartItemAdapter.Listener {

    private static final String TAG = "CartActivity";

    private CartItemRepository mCartItemRepo;
    private ProductRepository mProductRepo;
    private CategoryRepository mCategoryRepo;

    private List<CartItem> mCartItems;
    private CartItemAdapter mCartItemAdapter;

    private TextView mTvCartTitle;
    private RecyclerView mRvCartItems;
    private ViewGroup mLayoutError;
    private ImageView mIvErrorIcon;
    private TextView mTvErrorMessage;

    private CheckBox mCbSelectAll;
    private TextView mTvTotalFooter;
    private Button mBtnCheckout;
    private Button mBtnDelete;

    private boolean deleteMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        100);
            }
        }


        
        mCartItemRepo = new CartItemRepository();
        mProductRepo = new ProductRepository();
        mCategoryRepo = new CategoryRepository();

        mTvCartTitle = findViewById(R.id.tvCartTitle);
        mRvCartItems = findViewById(R.id.rvCartItems);
        mLayoutError = findViewById(R.id.layoutError);
        mIvErrorIcon = findViewById(R.id.ivErrorIcon);
        mTvErrorMessage = findViewById(R.id.tvErrorMessage);
        mCbSelectAll = findViewById(R.id.cbSelectAll);
        mTvTotalFooter = findViewById(R.id.tvTotalFooter);
        mBtnCheckout = findViewById(R.id.btnCheckout);
        mBtnDelete = findViewById(R.id.btnDelete);

        mRvCartItems.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mRvCartItems.setVisibility(View.VISIBLE);
        mLayoutError.setVisibility(View.GONE);

        findViewById(R.id.ivEdit).setOnClickListener(this::toggleDeleteMode);
        mBtnDelete.setOnClickListener(this::onDelete);
        mCbSelectAll.setOnCheckedChangeListener(this::onSelectAll);
        mBtnCheckout.setOnClickListener(this::onCheckout);

        ViewHelper.disable(mBtnDelete);
        ViewHelper.disable(mCbSelectAll);
        ViewHelper.disable(mBtnCheckout);
    }

    @Override
    protected void onStart() {
        super.onStart();
        fetchCartItems();
        mCbSelectAll.setChecked(false);
    }

    private void fetchCartItems() {
        mCartItemRepo.getCartItems(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<CartItemResponse>> call, @NonNull Response<List<CartItemResponse>> response) {
                if (!response.isSuccessful()) {
                    LogHelper.logErrorResponse(TAG, response);
                    showLoadError();
                    return;
                }

                List<CartItemResponse> cartItemDTOs = response.body();
                assert cartItemDTOs != null;
                if (cartItemDTOs.isEmpty()) {
                    showEmptyCart();
                    return;
                }

                mCartItems = cartItemDTOs.parallelStream()
                        .map(CartItemMapper.INSTANCE::toModel)
                        .collect(Collectors.toList());

                fetchProductInfo();
            }

            @Override
            public void onFailure(@NonNull Call<List<CartItemResponse>> call, @NonNull Throwable throwable) {
                LogHelper.logFailure(TAG, throwable);
                showLoadError();
            }
        });
    }

    private void fetchProductInfo() {
        AtomicInteger count = new AtomicInteger();
        int size = mCartItems.size();
        mCartItems.parallelStream()
                .forEach(cartItem -> mProductRepo.getProducts(cartItem.getProductId(), null, null, new Callback<>() {
                    @Override
                    public void onResponse(@NonNull Call<List<ProductResponse>> call, @NonNull Response<List<ProductResponse>> response) {
                        if (!response.isSuccessful()) {
                            LogHelper.logErrorResponse(TAG, response);
                            return;
                        }

                        List<ProductResponse> productDTOs = response.body();
                        assert productDTOs != null;
                        assert productDTOs.size() == 1;
                        Product product = ProductMapper.INSTANCE.toModel(productDTOs.get(0));
                        cartItem.setProduct(product);
                        cartItem.setUnitPrice(product.getPrice());

                        checkProgress();
                    }

                    @Override
                    public void onFailure(@NonNull Call<List<ProductResponse>> call, @NonNull Throwable throwable) {
                        Log.e(TAG, "onFailure: Failed to retrieve product #_" + cartItem.getProductId(), throwable);
                        checkProgress();
                    }

                    private void checkProgress() {
                        if (count.incrementAndGet() < size) return;
                        fetchCategoryInfo();
                    }
                }));
    }

    private void fetchCategoryInfo() {
        Set<String> categoryIds = mCartItems.stream()
                .map(e -> e.getProduct() != null ? e.getProduct().getCategoryName() : null)
                .collect(Collectors.toSet());
        categoryIds.remove(null);

        AtomicInteger count = new AtomicInteger();
        int size = categoryIds.size();

        categoryIds.parallelStream().forEach(id -> mCategoryRepo.getCategories(id, new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<CategoryResponse>> call, @NonNull Response<List<CategoryResponse>> response) {
                if (!response.isSuccessful()) {
                    LogHelper.logErrorResponse(TAG, response);
                    checkProgress();
                    return;
                }

                List<CategoryResponse> categoryDTOs = response.body();
                assert categoryDTOs != null;
                assert categoryDTOs.size() == 1;

                Category category = CategoryMapper.INSTANCE.toModel(categoryDTOs.get(0));
                mCartItems.forEach(cartItem -> {
                    Product product = cartItem.getProduct();
                    if (product != null && category.getId().equals(product.getCategoryName())) {
                        product.setCategory(category);
                    }
                });

                checkProgress();
            }

            @Override
            public void onFailure(@NonNull Call<List<CategoryResponse>> call, @NonNull Throwable throwable) {
                Log.e(TAG, "onFailure: Failed to retrieve category #_" + id, throwable);
                checkProgress();
            }

            private void checkProgress() {
                if (count.incrementAndGet() < size) return;
                showCartItems();
            }
        }));
    }

    private void showEmptyCart() {
        mRvCartItems.setVisibility(View.GONE);
        mLayoutError.setVisibility(View.VISIBLE);
        mIvErrorIcon.setImageResource(R.drawable.cart_empty);
        mTvErrorMessage.setText(R.string.empty_cart);
    }

    private void showLoadError() {
        mRvCartItems.setVisibility(View.GONE);
        mLayoutError.setVisibility(View.VISIBLE);
        mIvErrorIcon.setImageResource(R.drawable.network_issue);
        mTvErrorMessage.setText(R.string.network_error);
    }

    private void showCartItems() {
        mRvCartItems.setVisibility(View.VISIBLE);
        mLayoutError.setVisibility(View.GONE);
        ViewHelper.enable(mBtnDelete);
        ViewHelper.enable(mCbSelectAll);
        ViewHelper.enable(mBtnCheckout);

        mCartItemAdapter = new CartItemAdapter(mCartItems);
        mRvCartItems.setAdapter(mCartItemAdapter);
        ItemTouchHelper.Callback callback = new CartItemTouchCallback(mCartItemAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(mRvCartItems);

        mTvCartTitle.setText(String.format(Locale.getDefault(), "Cart (%d)", mCartItems.size()));
        mTvTotalFooter.setText(TextUtils.formatPrice(BigDecimal.ZERO));
    }

    private void toggleDeleteMode(View v) {
        if (!deleteMode) {
            deleteMode = true;
            mTvTotalFooter.setVisibility(View.GONE);
            mBtnCheckout.setVisibility(View.GONE);
            mBtnDelete.setVisibility(View.VISIBLE);
        } else {
            deleteMode = false;
            mTvTotalFooter.setVisibility(View.VISIBLE);
            mBtnCheckout.setVisibility(View.VISIBLE);
            mBtnDelete.setVisibility(View.GONE);
        }
    }

    private void onCheckout(View view) {
        CartItem[] selectedItems = mCartItems.stream().filter(CartItem::isSelected).toArray(CartItem[]::new);
        if (selectedItems.length == 0) {
            new AlertDialog.Builder(CartActivity.this)
                    .setMessage(R.string.err_cart_checkout_no_item)
                    .setPositiveButton(android.R.string.ok, (dialog, which) -> {})
                    .show();
            return;
        }
        Intent intent = new Intent(this, CheckoutActivity.class);
        intent.putExtra(CheckoutActivity.CART_ITEMS_KEY, selectedItems);
        startActivity(intent);
    }

    private void onDelete(View v) {
        if (mCartItems.stream().noneMatch(CartItem::isSelected)) {
            new AlertDialog.Builder(CartActivity.this)
                    .setMessage(R.string.err_cart_delete_no_item)
                    .setPositiveButton(android.R.string.ok, (dialog, which) -> {})
                    .show();
            return;
        }
        new AlertDialog.Builder(CartActivity.this)
                .setMessage(R.string.confirm__cart_remove_selected_products)
                .setPositiveButton(android.R.string.ok, (dialog, which) -> removeSelectedCartItems())
                .setNegativeButton(android.R.string.cancel, (dialog, which) -> {})
                .show();
    }

    private void removeSelectedCartItems() {
        AtomicBoolean failed = new AtomicBoolean();
        for (int i = 0; i < mCartItems.size(); ++i) {
            if (mCartItems.get(i).isSelected()) {
                int idx = i;
                mCartItemRepo.deleteCartItem(mCartItems.get(i).getId(), new Callback<>() {
                    @Override
                    public void onResponse(@NonNull Call<MessageResponse> call, @NonNull Response<MessageResponse> response) {
                        if (!response.isSuccessful()) {
                            failed.set(true);
                            LogHelper.logErrorResponse(TAG, response);
                            return;
                        }
                        mCartItems.remove(idx);
                        mCartItemAdapter.notifyItemRemoved(idx);
                    }

                    @Override
                    public void onFailure(@NonNull Call<MessageResponse> call, @NonNull Throwable throwable) {
                        failed.set(true);
                        LogHelper.logFailure(TAG, throwable);
                    }
                });
            }
        }
        if (failed.get()) {
            Toast.makeText(CartActivity.this, R.string.err_product_removal_failed, Toast.LENGTH_SHORT).show();
        }
        updateTotalPrice();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void onSelectAll(@NonNull CompoundButton buttonView, boolean isChecked) {
        // Return if the button was checked programmatically
        if (!buttonView.isPressed()) return;
        mCartItems.forEach(cartItem -> cartItem.setSelected(isChecked));
        mCartItemAdapter.notifyDataSetChanged();
        updateTotalPrice();
    }

    @Override
    public void onCartItemCheckChanged(int position, boolean isChecked) {
        // No need to invalidate the View; the CheckBox should've already updated itself.
        mCartItems.get(position).setSelected(isChecked);
        mCbSelectAll.setChecked(mCartItems.stream().allMatch(CartItem::isSelected));
        updateTotalPrice();
    }

    @Override
    public void onCartItemQuantityChanged(int position, int delta) {
        CartItem cartItem = mCartItems.get(position);
        int newQuantity = cartItem.getQuantity() + delta;

        if (newQuantity <= 0) {
            onCartItemDeleted(position);
            return;
        }

        CartItem toBeCartItem = new CartItem();
        toBeCartItem.setProductId(cartItem.getProductId());
        toBeCartItem.setQuantity(newQuantity);

        mCartItemRepo.updateCartItem(cartItem.getId(), toBeCartItem, new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<MessageResponse> call, @NonNull Response<MessageResponse> response) {
                if (!response.isSuccessful()) {
                    LogHelper.logErrorResponse(TAG, response);
                    Toast.makeText(CartActivity.this, R.string.err_cart_update_failed, Toast.LENGTH_LONG).show();
                    return;
                }
                cartItem.setQuantity(newQuantity);
                mCartItems.set(position, cartItem);
                mCartItemAdapter.notifyItemChanged(position);
                updateTotalPrice();
            }

            @Override
            public void onFailure(@NonNull Call<MessageResponse> call, @NonNull Throwable throwable) {
                LogHelper.logFailure(TAG, throwable);
                Toast.makeText(CartActivity.this, R.string.err_cart_update_failed, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onCartItemDeleted(int position) {
        new AlertDialog.Builder(CartActivity.this)
                .setMessage(R.string.confirm_cart_remove_product)
                .setPositiveButton(android.R.string.ok, (dialog, which) -> removeCartItem(position))
                .setNegativeButton(android.R.string.cancel, (dialog, which) -> mCartItemAdapter.notifyItemChanged(position))
                .show();
    }

    private void removeCartItem(int position) {
        mCartItemRepo.deleteCartItem(mCartItems.get(position).getId(), new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<MessageResponse> call, @NonNull Response<MessageResponse> response) {
                if (!response.isSuccessful()) {
                    LogHelper.logErrorResponse(TAG, response);
                    Toast.makeText(CartActivity.this, R.string.err_cart_delete_failed, Toast.LENGTH_LONG).show();
                    return;
                }
                mCartItems.remove(position);
                mCartItemAdapter.notifyItemRemoved(position);
                updateTotalPrice();
            }

            @Override
            public void onFailure(@NonNull Call<MessageResponse> call, @NonNull Throwable throwable) {
                LogHelper.logFailure(TAG, throwable);
                Toast.makeText(CartActivity.this, R.string.err_cart_delete_failed, Toast.LENGTH_SHORT).show();
            }
        });
    }

    //FIXME: There should really be an API for this. This method makes an excessive number of API calls, just to get the latest prices.
    private void updateTotalPrice() {
        List<CartItem> selectedItems = mCartItems.parallelStream().filter(CartItem::isSelected).collect(Collectors.toList());

        if (selectedItems.isEmpty()) {
            mTvTotalFooter.setText(TextUtils.formatPrice(BigDecimal.ZERO));
            return;
        }

        int size = selectedItems.size();
        AtomicInteger count = new AtomicInteger();
        AtomicBoolean failed = new AtomicBoolean();
        AtomicReference<BigDecimal> total = new AtomicReference<>(BigDecimal.ZERO);

        selectedItems.parallelStream().forEach(cartItem -> mProductRepo.getProducts(cartItem.getProductId(), null, null, new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<ProductResponse>> call, @NonNull Response<List<ProductResponse>> response) {
                if (failed.get()) {
                    addToTotal(null);
                    return;
                }

                if (!response.isSuccessful()) {
                    failed.set(true);
                    LogHelper.logErrorResponse(TAG, response);
                    addToTotal(null);
                    return;
                }

                List<ProductResponse> productResponses = response.body();
                assert productResponses != null;
                assert productResponses.size() == 1;

                Product product = ProductMapper.INSTANCE.toModel(productResponses.get(0));
                if (cartItem.getProduct() != null) {
                    cartItem.getProduct().setPrice(product.getPrice());
                }
                addToTotal(product.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())));
            }

            @Override
            public void onFailure(@NonNull Call<List<ProductResponse>> call, @NonNull Throwable throwable) {
                failed.set(true);
                LogHelper.logFailure(TAG, throwable);
                addToTotal(null);
            }

            private void addToTotal(BigDecimal price) {
                if (!failed.get()) {
                    total.accumulateAndGet(price, BigDecimal::add);
                }

                if (count.incrementAndGet() < size) return;

                if (failed.get()) {
                    Toast.makeText(CartActivity.this, R.string.err_cart_calculate_total_failed, Toast.LENGTH_SHORT).show();
                } else {
                    String totalPrice = TextUtils.formatPrice(total.get());
                    mTvTotalFooter.setText(totalPrice);
                }
            }
        }));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCartItemAdapter != null) {
            mCartItemAdapter.onDestroy();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mCartItems != null && !mCartItems.isEmpty()) {
            NotificationHelper.showCartNotification(this, mCartItems.size());
        } else {
            NotificationHelper.clearBadge(this);
        }
    }

}
