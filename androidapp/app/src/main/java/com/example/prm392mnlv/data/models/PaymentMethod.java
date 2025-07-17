package com.example.prm392mnlv.data.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;

import org.jetbrains.annotations.Contract;

public class PaymentMethod implements Parcelable {
    private int id;
    private String name;
    private @DrawableRes int icon;

    public PaymentMethod() {}

    public PaymentMethod(int id, @NonNull String name, @DrawableRes int icon) {
        this.id = id;
        this.name = name;
        this.icon = icon;
    }

    protected PaymentMethod(@NonNull Parcel in) {
        id = in.readInt();
        name = in.readString();
        icon = in.readInt();
    }

    public static final Creator<PaymentMethod> CREATOR = new Creator<PaymentMethod>() {
        @NonNull
        @Contract("_ -> new")
        @Override
        public PaymentMethod createFromParcel(Parcel in) {
            return new PaymentMethod(in);
        }

        @NonNull
        @Contract(value = "_ -> new", pure = true)
        @Override
        public PaymentMethod[] newArray(int size) {
            return new PaymentMethod[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeInt(icon);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @NonNull
    public String getName() {
        return name;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }
}
