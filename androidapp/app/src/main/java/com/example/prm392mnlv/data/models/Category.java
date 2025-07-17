package com.example.prm392mnlv.data.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.Contract;

public class Category extends ModelBase implements Parcelable {
    private String categoryName;

    public Category() {}

    protected Category(@NonNull Parcel in) {
        id = in.readString();
        categoryName = in.readString();
    }

    public static final Creator<Category> CREATOR = new Creator<>() {
        @NonNull
        @Contract("_ -> new")
        @Override
        public Category createFromParcel(Parcel in) {
            return new Category(in);
        }

        @NonNull
        @Contract(value = "_ -> new", pure = true)
        @Override
        public Category[] newArray(int size) {
            return new Category[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(categoryName);
    }

    @NonNull
    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(@NonNull String categoryName) {
        this.categoryName = categoryName;
    }
}
