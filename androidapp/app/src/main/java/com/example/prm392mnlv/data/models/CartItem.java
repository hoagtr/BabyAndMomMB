package com.example.prm392mnlv.data.models;

import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.jetbrains.annotations.Contract;

import java.math.BigDecimal;

public class CartItem extends ModelBase implements Parcelable {
    private String productId;
    private BigDecimal unitPrice;
    private int quantity;

    private Product product;
    private boolean selected;

    public CartItem() {}

    protected CartItem(@NonNull Parcel in) {
        id = in.readString();
        productId = in.readString();
        unitPrice = (BigDecimal) in.readSerializable();
        quantity = in.readInt();
        product = in.readParcelable(Product.class.getClassLoader());
        selected = in.readByte() != 0;
    }

    public static final Creator<CartItem> CREATOR = new Creator<>() {
        @NonNull
        @Contract("_ -> new")
        @Override
        public CartItem createFromParcel(Parcel in) {
            return new CartItem(in);
        }

        @NonNull
        @Contract(value = "_ -> new", pure = true)
        @Override
        public CartItem[] newArray(int size) {
            return new CartItem[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(productId);
        dest.writeSerializable(unitPrice);
        dest.writeInt(quantity);
        dest.writeParcelable(product, flags);
        dest.writeByte((byte) (selected ? 1 : 0));
    }

    @NonNull
    public String getProductId() {
        return productId;
    }

    public void setProductId(@NonNull String productId) {
        this.productId = productId;
    }

    @NonNull
    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(@NonNull BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Nullable
    public Product getProduct() {
        return product;
    }

    public void setProduct(@NonNull Product product) {
        this.product = product;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
