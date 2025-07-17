package com.example.prm392mnlv.data.models;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Objects;

public abstract class ModelBase {
    protected String id;

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj) return true;
        if (null == obj) return false;
        if (this.getClass() == obj.getClass()) {
            ModelBase modelObj = (ModelBase) obj;
            return this.getId().equals(modelObj.getId());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.getId());
    }
}
