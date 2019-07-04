package com.example.mitrais.onestopclick.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.support.annotation.NonNull;

@Entity(tableName = "ownership", primaryKeys = {"email", "productId"})
public class Ownership {
    @NonNull
    private String email;
    @NonNull
    private String productId;
    private float rating;

    @Ignore
    public Ownership() {
    }

    public Ownership(@NonNull String email) {
        this.email = email;
    }

    @NonNull
    public String getEmail() {
        return email;
    }

    public void setEmail(@NonNull String email) {
        this.email = email;
    }

    @NonNull
    public String getProductId() {
        return productId;
    }

    public void setProductId(@NonNull String productId) {
        this.productId = productId;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }
}
