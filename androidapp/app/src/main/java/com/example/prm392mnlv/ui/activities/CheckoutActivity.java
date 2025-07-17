package com.example.prm392mnlv.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.example.prm392mnlv.R;
import com.example.prm392mnlv.data.dto.response.CreateZaloPayOrderResponse;
import com.example.prm392mnlv.data.dto.response.MessageResponse;
import com.example.prm392mnlv.data.dto.response.UserProfileResponse;
import com.example.prm392mnlv.data.mappings.PaymentMapper;
import com.example.prm392mnlv.data.mappings.ShippingMapper;
import com.example.prm392mnlv.data.mappings.UserMapper;
import com.example.prm392mnlv.data.models.CartItem;
import com.example.prm392mnlv.data.models.PaymentMethod;
import com.example.prm392mnlv.data.models.PaymentMethods;
import com.example.prm392mnlv.data.models.Product;
import com.example.prm392mnlv.data.models.ShippingMethod;
import com.example.prm392mnlv.data.models.ShippingMethods;
import com.example.prm392mnlv.data.models.User;
import com.example.prm392mnlv.retrofit.repositories.OrderRepository;
import com.example.prm392mnlv.retrofit.repositories.UserRepository;
import com.example.prm392mnlv.retrofit.repositories.ZaloPayRepository;
import com.example.prm392mnlv.util.ImageUtils;
import com.example.prm392mnlv.util.LogHelper;
import com.example.prm392mnlv.util.TextUtils;
import com.example.prm392mnlv.util.ViewHelper;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.zalopay.sdk.ZaloPayError;
import vn.zalopay.sdk.ZaloPaySDK;
import vn.zalopay.sdk.listeners.PayOrderListener;

public class CheckoutActivity extends AppCompatActivity {
    public static final String CART_ITEMS_KEY = "CartItems";

    private static final String TAG = "CheckoutActivity";

    private UserRepository mUserRepo;
    private OrderRepository mOrderRepo;
    private ZaloPayRepository mZaloPayRepo;

    private TextView mTvUserName;
    private TextView mTvPhoneNumber;
    private TextView mTvShippingAddress;

    private LinearLayout mLlCheckoutItems;

    private TextView mTvShippingMethod;
    private TextView mTvShippingFee;
    private TextView mTvEstimatedDelivery;

    private TextView mTvItemCount;
    private TextView mTvSubtotal;

    private RadioGroup mRgPaymentMethods;

    private TextView mTvProductTotal;
    private TextView mTvShippingTotal;
    private TextView mTvDiscount;
    private TextView mTvGrandTotal;

    private TextView mTvTotalFooter;

    private ViewGroup mLoadingPanel;

    private User mUser;
    private List<CartItem> mCartItems;
    private BigDecimal mSubtotal;
    private ShippingMethod mShippingMethod;
    private PaymentMethod mPaymentMethod;
    private BigDecimal mGrandTotal;

    private final List<ShippingMethod> mShippingMethods = new ArrayList<>(ShippingMethods.getMethods());
    private final CompositeDisposable disposables = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        mUserRepo = new UserRepository();
        mOrderRepo = new OrderRepository();
        mZaloPayRepo = new ZaloPayRepository();

        mTvUserName = findViewById(R.id.tvUserName);
        mTvPhoneNumber = findViewById(R.id.tvPhoneNumber);
        mTvShippingAddress = findViewById(R.id.tvShippingAddress);
        mLlCheckoutItems = findViewById(R.id.llCheckoutItems);
        mTvShippingMethod = findViewById(R.id.tvShippingMethod);
        mTvShippingFee = findViewById(R.id.tvShippingFee);
        mTvEstimatedDelivery = findViewById(R.id.tvEstimatedDelivery);
        mTvItemCount = findViewById(R.id.tvItemCount);
        mTvSubtotal = findViewById(R.id.tvSubtotal);
        mRgPaymentMethods = findViewById(R.id.rgPaymentMethods);
        mTvProductTotal = findViewById(R.id.tvProductTotal);
        mTvShippingTotal = findViewById(R.id.tvShippingTotal);
        mTvDiscount = findViewById(R.id.tvDiscount);
        mTvGrandTotal = findViewById(R.id.tvGrandTotal);
        mTvTotalFooter = findViewById(R.id.tvTotalFooter);
        mLoadingPanel = findViewById(R.id.loadingPanel);

        startLoading();

        fetchUserInfo();
        initProductList();
        mShippingMethod = ShippingMethods.getMethods().stream().findFirst().orElseThrow();
        initPaymentMethods();
        mPaymentMethod = PaymentMethods.getMethods().stream().findFirst().orElseThrow();
        mRgPaymentMethods.check(mPaymentMethod.getId());

        ActivityResultLauncher<Intent> changeAddress = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                o -> {
                    if (o.getResultCode() != Activity.RESULT_OK) return;
                    Intent result = o.getData();
                    if (result != null) {
                        mUser = result.getParcelableExtra(ShippingAddressActivity.RESULT_KEY);
                        calculateShippingAndSubtotal();
                        updateUserInfo();
                        updateShippingMethodAndSubtotal();
                        updateSummary();
                    }
                });

        ActivityResultLauncher<Intent> changeShipping = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                o -> {
                    if (o.getResultCode() != Activity.RESULT_OK) return;
                    Intent result = o.getData();
                    if (result != null) {
                        int id = result.getIntExtra(ShippingMethodActivity.RESULT_KEY, 1);
                        mShippingMethod = mShippingMethods.stream().filter(method -> method.getId() == id).findFirst().orElseThrow();
                        updateShippingMethodAndSubtotal();
                        updateSummary();
                    }
                }
        );

        findViewById(R.id.ivChangeAddress).setOnClickListener(v -> {
            Intent intent = new Intent(CheckoutActivity.this, ShippingAddressActivity.class);
            intent.putExtra(ShippingAddressActivity.USER_KEY, mUser);
            changeAddress.launch(intent);
        });

        findViewById(R.id.ivChangeShipping).setOnClickListener(v -> {
            Intent intent = new Intent(CheckoutActivity.this, ShippingMethodActivity.class);
            intent.putExtra(ShippingMethodActivity.SELECTED_METHOD_ID_KEY, mShippingMethod.getId());
            intent.putExtra(ShippingMethodActivity.SHIPPING_METHODS_KEY, mShippingMethods.toArray(new ShippingMethod[0]));
            changeShipping.launch(intent);
        });

        mRgPaymentMethods.setOnCheckedChangeListener((group, checkedId) -> {
            mPaymentMethod = PaymentMethods.get(checkedId);
            updateSummary();
        });

        findViewById(R.id.btnPlaceOrder).setOnClickListener(this::placeOrder);
    }

    private void startLoading() {
        mLoadingPanel.setVisibility(View.VISIBLE);
    }

    private void stopLoading() {
        mLoadingPanel.setVisibility(View.GONE);
    }

    private void fetchUserInfo() {
        mUserRepo.getUserProfile(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<UserProfileResponse> call, @NonNull Response<UserProfileResponse> response) {
                if (!response.isSuccessful()) {
                    LogHelper.logErrorResponse(TAG, response);
                    ViewHelper.showAlert(CheckoutActivity.this, R.string.err_checkout_load_user_address_failed);
                    return;
                }

                UserProfileResponse userResponse = response.body();
                assert userResponse != null;
                mUser = UserMapper.INSTANCE.toModel(userResponse);

                calculateShippingAndSubtotal();
                updateUserInfo();
                updateShippingMethodAndSubtotal();
                updateSummary();

                stopLoading();
            }

            @Override
            public void onFailure(@NonNull Call<UserProfileResponse> call, @NonNull Throwable throwable) {
                LogHelper.logFailure(TAG, throwable);
                ViewHelper.showAlert(CheckoutActivity.this, R.string.err_checkout_load_user_address_failed);
            }
        });
    }

    private void initProductList() {
        Parcelable[] parcels = getIntent().getParcelableArrayExtra(CART_ITEMS_KEY);
        if (parcels == null) {
            throw new IllegalStateException("Checkout activity must be started with a list of cart items.");
        }

        mLlCheckoutItems.removeAllViews();
        mCartItems = Arrays.stream(parcels).map(e -> (CartItem) e).collect(Collectors.toList());
        LayoutInflater inflater = getLayoutInflater();
        mCartItems.forEach(item -> renderCheckoutItem(item, inflater));

        String itemCountTxt = mCartItems.size() + (mCartItems.size() == 1 ? " item" : " items");
        mTvItemCount.setText(itemCountTxt);
    }

    private void renderCheckoutItem(@NonNull CartItem item, @NonNull LayoutInflater inflater) {
        View view = inflater.inflate(R.layout.item_checkout, mLlCheckoutItems, false);

        ImageView ivProductImage = view.findViewById(R.id.ivProductImage);
        TextView tvProductName = view.findViewById(R.id.tvProductName);
        TextView tvCategory = view.findViewById(R.id.tvCategory);
        TextView tvOriginalPrice = view.findViewById(R.id.tvOriginalPrice);
        TextView tvPrice = view.findViewById(R.id.tvPrice);
        TextView tvQuantity = view.findViewById(R.id.tvQuantity);

        if (item.getProduct() != null) {
            Product product = item.getProduct();

            tvProductName.setText(product.getProductName());
            if (product.getCategory() != null) {
                tvCategory.setText(product.getCategory().getCategoryName());
            }

            Drawable loading = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_loading, getTheme());
            Drawable noImage = ResourcesCompat.getDrawable(getResources(), R.drawable.no_image, getTheme());
            assert noImage != null;

            ivProductImage.setImageDrawable(loading);
            Disposable fetchImage = Flowable.fromSupplier(() -> ImageUtils.fetchDrawable(product.getImageUrl()))
                    .single(noImage)
                    .onErrorReturnItem(noImage)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(ivProductImage::setImageDrawable);
            disposables.add(fetchImage);
        }

        String unitPrice = TextUtils.formatPrice(item.getUnitPrice());
        tvPrice.setText(unitPrice);

        BigDecimal fakeOriginalPrice = item.getUnitPrice().multiply(BigDecimal.valueOf(1.25));
        String originalPrice = TextUtils.formatPrice(fakeOriginalPrice);
        tvOriginalPrice.setText(originalPrice);
        tvOriginalPrice.setPaintFlags(tvOriginalPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        tvQuantity.setText(String.format(Locale.getDefault(), "x%d", item.getQuantity()));

        mLlCheckoutItems.addView(view);
    }

    private void calculateShippingAndSubtotal() {
        if (mUser != null && mUser.getShippingAddress() != null) {
            //TODO: Actual distance
            double distance = 1.337d * mUser.getShippingAddress().length();
            for (ShippingMethod method : mShippingMethods) {
                int feeClass = method.getFeeClass();
                BigDecimal shippingFee = BigDecimal.valueOf((Math.log10(Math.max(distance, 0.1d)) + 1) * feeClass * 9_800);
                method.setFee(shippingFee);

                int speedClass = method.getSpeedClass();
                if (speedClass == 0) {
                    method.setDeliveryTime(null);
                } else {
                    LocalDate eta = LocalDate.now().plusDays((long) Math.ceil(Math.sqrt(distance)) * speedClass * 7);
                    method.setDeliveryTime(eta);
                }
            }
        } else {
            mShippingMethods.forEach(method -> {
                if (method.getFeeClass() == 0) {
                    method.setFee(BigDecimal.ZERO);
                }
                if (method.getSpeedClass() == 0) {
                    method.setDeliveryTime(null);
                }
            });
        }

        mSubtotal = BigDecimal.ZERO;
        mCartItems.forEach(item -> mSubtotal = mSubtotal.add(item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity()))));
        mSubtotal = mSubtotal.add(mShippingMethod.getFee() != null ? mShippingMethod.getFee() : BigDecimal.ZERO);
    }

    private void initPaymentMethods() {
        mRgPaymentMethods.removeAllViews();
        List<PaymentMethod> paymentMethods = new ArrayList<>(PaymentMethods.getMethods());
        LayoutInflater inflater = getLayoutInflater();
        paymentMethods.forEach(method -> {
            View view = inflater.inflate(R.layout.item_payment_method, mRgPaymentMethods, false);

            ImageView ivPaymentMethodIcon = view.findViewById(R.id.ivPaymentMethodIcon);
            TextView tvPaymentMethodName = view.findViewById(R.id.tvPaymentMethodName);
            RadioButton rbPaymentMethod = view.findViewById(R.id.rbPaymentMethod);

            ivPaymentMethodIcon.setImageResource(method.getIcon());
            tvPaymentMethodName.setText(method.getName());
            rbPaymentMethod.setId(method.getId());

            mRgPaymentMethods.addView(view);
        });
    }

    private void updateUserInfo() {
        mTvUserName.setText(mUser.getName());
        mTvPhoneNumber.setText(TextUtils.formatPhoneNumber(mUser.getPhoneNumber()));

        if (TextUtils.isNullOrBlank(mUser.getShippingAddress())) {
            mTvShippingAddress.setText(R.string.prompt_select_shipping_address);
            mTvShippingAddress.setTypeface(null, Typeface.ITALIC);
        } else {
            mTvShippingAddress.setText(mUser.getShippingAddress());
            mTvShippingAddress.setTypeface(null, Typeface.NORMAL);
        }
    }

    private void updateShippingMethodAndSubtotal() {
        mTvShippingMethod.setText(mShippingMethod.getName());
        if ((mUser == null || mUser.getShippingAddress() == null)
                && mShippingMethod.getFeeClass() != 0) {
            mTvShippingFee.setText(R.string.undetermined);
            mTvShippingFee.setTypeface(null, Typeface.ITALIC);
            mTvEstimatedDelivery.setText(R.string.undetermined);
            mTvEstimatedDelivery.setTypeface(null, Typeface.ITALIC);
        } else {
            assert mShippingMethod.getFee() != null;
            mTvShippingFee.setText(TextUtils.formatPrice(mShippingMethod.getFee()));
            mTvShippingFee.setTypeface(null, Typeface.NORMAL);
            if (mShippingMethod.getDeliveryTime() == null) {
                mTvEstimatedDelivery.setText(R.string.not_available);
                mTvEstimatedDelivery.setTypeface(null, Typeface.ITALIC);
            } else {
                mTvEstimatedDelivery.setText(mShippingMethod.getDeliveryTime().format(DateTimeFormatter.ISO_LOCAL_DATE));
                mTvEstimatedDelivery.setTypeface(null, Typeface.NORMAL);
            }
        }
        mTvSubtotal.setText(TextUtils.formatPrice(mSubtotal));
    }

    private void updateSummary() {
        BigDecimal productTotal = BigDecimal.ZERO;
        for (CartItem item : mCartItems) {
            productTotal = productTotal.add(item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
        }

        BigDecimal discount = BigDecimal.ZERO;
        if (mPaymentMethod.getId() == 2) {
            discount = productTotal.multiply(BigDecimal.valueOf(-0.04d));
        }

        if ((mUser == null || mUser.getShippingAddress() == null)
                && mShippingMethod.getFeeClass() != 0) {
            mGrandTotal = productTotal.add(discount);
            mTvShippingTotal.setText(R.string.undetermined);
            mTvShippingTotal.setTypeface(null, Typeface.ITALIC);
        } else {
            BigDecimal shippingTotal = mShippingMethod.getFee();
            assert shippingTotal != null;
            mGrandTotal = productTotal.add(shippingTotal).add(discount);
            mTvShippingTotal.setText(TextUtils.formatPrice(shippingTotal));
            mTvShippingTotal.setTypeface(null, Typeface.NORMAL);
        }

        mTvProductTotal.setText(TextUtils.formatPrice(productTotal));
        mTvDiscount.setText(TextUtils.formatPrice(discount));
        mTvGrandTotal.setText(TextUtils.formatPrice(mGrandTotal));
        mTvTotalFooter.setText(TextUtils.formatPrice(mGrandTotal));
    }

    private void placeOrder(View v) {
        if (mUser.getShippingAddress() == null) {
            ViewHelper.showAlert(CheckoutActivity.this, R.string.err_checkout_place_order_no_address);
            return;
        }

        if (mPaymentMethod.getId() == 2) {
            createZaloPayTransaction();
        } else {
            createOrder();
        }
    }

    private void createOrder() {
        startLoading();
        String paymentMethod = PaymentMapper.INSTANCE.toDto(mPaymentMethod);
        String shippingType = ShippingMapper.INSTANCE.toDto(mShippingMethod);
        mOrderRepo.createOrder(paymentMethod, shippingType, new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<MessageResponse> call, @NonNull Response<MessageResponse> response) {
                if (!response.isSuccessful()) {
                    try (ResponseBody body = response.errorBody()) {
                        assert body != null;
                        Log.e(TAG, "onResponse error: " + response.message() + "\n" + body.string());
                        ViewHelper.showAlert(
                                CheckoutActivity.this,
                                getString(R.string.err_checkout_place_order_failed) + "\nReason: " + response.message());
                    } catch (IOException e) {
                        Log.e(TAG, "onResponse error: ", e);
                    }
                    return;
                }

                Intent intent = new Intent(CheckoutActivity.this, OrderSummaryActivity.class);
                intent.putExtra(OrderSummaryActivity.ORDER_TOTAL_KEY, mGrandTotal);
                intent.putExtra(OrderSummaryActivity.PAYMENT_METHOD_KEY, mPaymentMethod);
                intent.putExtra(OrderSummaryActivity.SHIPPING_METHOD_KEY, mShippingMethod);
                startActivity(intent);
                stopLoading();
                finish();
            }

            @Override
            public void onFailure(@NonNull Call<MessageResponse> call, @NonNull Throwable throwable) {
                LogHelper.logFailure(TAG, throwable);
                ViewHelper.showAlert(CheckoutActivity.this, R.string.err_checkout_place_order_failed);
            }
        });
    }

    private void createZaloPayTransaction() {
        mZaloPayRepo.createOrder(mUser.getEmail(), mGrandTotal.longValue(), new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<CreateZaloPayOrderResponse> call, @NonNull Response<CreateZaloPayOrderResponse> response) {
                if (!response.isSuccessful()) {
                    LogHelper.logErrorResponse(TAG, response);
                    ViewHelper.showAlert(CheckoutActivity.this, R.string.err_checkout_place_order_zalopay_connection_error);
                    return;
                }

                CreateZaloPayOrderResponse createZaloPayOrderResponse = response.body();
                assert createZaloPayOrderResponse != null;
                if (createZaloPayOrderResponse.returnCode != 1) {
                    Log.e(TAG, "onResponse: ZaloPay error" +
                            "\nCode: " + createZaloPayOrderResponse.subReturnCode +
                            "\nMessage: " + createZaloPayOrderResponse.subReturnMessage);
                    ViewHelper.showAlert(CheckoutActivity.this, R.string.err_checkout_place_order_zalopay_failed);
                    return;
                }

                executeTransactionViaZaloPayApp(createZaloPayOrderResponse.zpTransToken);
            }

            @Override
            public void onFailure(@NonNull Call<CreateZaloPayOrderResponse> call, @NonNull Throwable throwable) {
                LogHelper.logFailure(TAG, throwable);
                Toast.makeText(CheckoutActivity.this, R.string.err_checkout_place_order_zalopay_connection_error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void executeTransactionViaZaloPayApp(String token) {
        ZaloPaySDK.getInstance().payOrder(CheckoutActivity.this, token, ZaloPayRepository.CALLBACK_URI, new PayOrderListener() {
            @Override
            public void onPaymentSucceeded(String transactionId, String zpTransToken, String appTransId) {
                createOrder();
            }

            @Override
            public void onPaymentCanceled(String zpTransToken, String appTransId) {
                Log.w(TAG, "onPaymentCanceled:\n\tTransaction ID: [ " + appTransId + " ]\n\tTransaction token: [ " + zpTransToken + " ]");
            }

            @Override
            public void onPaymentError(ZaloPayError zaloPayError, String zpTransToken, String appTransId) {
                Log.e(TAG, "onPaymentError:\n\tError type: [ " + zaloPayError.toString() + " ]\n\tTransaction ID: [ " + appTransId + " ]\n\tTransaction token: [ " + zpTransToken + " ]");
                if (zaloPayError == ZaloPayError.PAYMENT_APP_NOT_FOUND) {
                    new AlertDialog.Builder(CheckoutActivity.this)
                            .setMessage("It appears you do not have ZaloPay installed. Would you like to get it from the AppStore?")
                            .setPositiveButton(android.R.string.ok, (dialog, which) -> ZaloPaySDK.getInstance().navigateToZaloPayOnStore(CheckoutActivity.this))
                            .setNegativeButton(android.R.string.cancel, (dialog, which) -> {})
                            .show();
                } else {
                    ViewHelper.showAlert(CheckoutActivity.this, R.string.err_checkout_place_order_zalopay_transaction_error);
                }
            }
        });
    }

    @Override
    protected void onNewIntent(@NonNull Intent intent) {
        super.onNewIntent(intent);
        ZaloPaySDK.getInstance().onResult(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposables.dispose();
    }
}
