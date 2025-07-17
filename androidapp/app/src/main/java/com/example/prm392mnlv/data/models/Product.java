package com.example.prm392mnlv.data.models;

import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.jetbrains.annotations.Contract;

import java.math.BigDecimal;

public class Product extends ModelBase implements Parcelable {
    private String productName;
    private String description;
    private BigDecimal price;
    private int quantityInStock;
    private Uri imageUrl;
    private String categoryName;

    private Category category;
    private Drawable imageDrawable;

    public Product() {}

    protected Product(@NonNull Parcel in) {
        id = in.readString();
        productName = in.readString();
        description = in.readString();
        quantityInStock = in.readInt();
        imageUrl = in.readParcelable(Uri.class.getClassLoader());
        categoryName = in.readString();
        category = in.readParcelable(Category.class.getClassLoader());
    }

    public static final Creator<Product> CREATOR = new Creator<Product>() {
        @NonNull
        @Contract("_ -> new")
        @Override
        public Product createFromParcel(Parcel in) {
            return new Product(in);
        }

        @NonNull
        @Contract(value = "_ -> new", pure = true)
        @Override
        public Product[] newArray(int size) {
            return new Product[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(productName);
        dest.writeString(description);
        dest.writeInt(quantityInStock);
        dest.writeParcelable(imageUrl, flags);
        dest.writeString(categoryName);
        dest.writeParcelable(category, flags);
    }

    @NonNull
    public String getProductName() {
        return productName;
    }

    public void setProductName(@NonNull String productName) {
        this.productName = productName;
    }

    @NonNull
    public String getDescription() {
        return description;
    }

    public void setDescription(@NonNull String description) {
        this.description = description;
    }

    @NonNull
    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(@NonNull BigDecimal price) {
        this.price = price;
    }

    public int getQuantityInStock() {
        return quantityInStock;
    }

    public void setQuantityInStock(int quantityInStock) {
        this.quantityInStock = quantityInStock;
    }

    @NonNull
    public Uri getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(@NonNull Uri imageUrl) {
        this.imageUrl = imageUrl;
    }

    @NonNull
    public String getCategoryName() {
        return categoryName;
    }


    public void setCategoryName(@NonNull String categoryName) {
        this.categoryName = categoryName;
    }

    @Nullable
    public Category getCategory() {
        return category;
    }

    public void setCategory(@NonNull Category category) {
        this.category = category;
    }

    @Nullable
    public Drawable getImageDrawable() {
        return imageDrawable;
    }

    public void setImageDrawable(@Nullable Drawable imageDrawable) {
        this.imageDrawable = imageDrawable;
    }
}
