package com.example.prm392mnlv.data.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.jetbrains.annotations.Contract;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;

public class ShippingMethod implements Parcelable {
    private int id;
    private String name;
    private int feeClass;
    private int speedClass;

    private BigDecimal fee;
    private LocalDate deliveryTime;

    public ShippingMethod() {}

    public ShippingMethod(int id, String name, int feeClass, int speedClass) {
        this.id = id;
        this.name = name;
        this.feeClass = feeClass;
        this.speedClass = speedClass;
    }

    protected ShippingMethod(@NonNull Parcel in) {
        id = in.readInt();
        name = in.readString();
        feeClass = in.readInt();
        speedClass = in.readInt();
        fee = (BigDecimal) in.readSerializable();
        deliveryTime = (LocalDate) in.readSerializable();
    }

    public static final Creator<ShippingMethod> CREATOR = new Creator<>() {
        @NonNull
        @Contract("_ -> new")
        @Override
        public ShippingMethod createFromParcel(Parcel in) {
            return new ShippingMethod(in);
        }

        @NonNull
        @Contract(value = "_ -> new", pure = true)
        @Override
        public ShippingMethod[] newArray(int size) {
            return new ShippingMethod[size];
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
        dest.writeInt(feeClass);
        dest.writeInt(speedClass);
        dest.writeSerializable(fee);
        dest.writeSerializable(deliveryTime);
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

    public int getFeeClass() {
        return feeClass;
    }

    public void setFeeClass(int feeClass) {
        this.feeClass = feeClass;
    }

    public int getSpeedClass() {
        return speedClass;
    }

    public void setSpeedClass(int speedClass) {
        this.speedClass = speedClass;
    }

    @Nullable
    public BigDecimal getFee() {
        return fee;
    }

    public void setFee(@Nullable BigDecimal fee) {
        this.fee = fee;
    }

    @Nullable
    public LocalDate getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(@Nullable LocalDate deliveryTime) {
        this.deliveryTime = deliveryTime;
    }
}
