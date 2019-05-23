package com.example.mitrais.onestopclick.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.support.annotation.NonNull;

@Entity(tableName = "profile_product", primaryKeys = {"email", "productId"})
public class ProfileProduct {
    @NonNull
    private String email = "";
    @NonNull
    private String productId = "";
    private boolean isLiked;
    private boolean isDisliked;

    public ProfileProduct(@NonNull String email, @NonNull String productId, boolean isLiked, boolean isDisliked) {
        this.email = email;
        this.productId = productId;
        this.isLiked = isLiked;
        this.isDisliked = isDisliked;
    }

    @Ignore
    public ProfileProduct() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public boolean isLiked() {
        return isLiked;
    }

    public void setLiked(boolean liked) {
        isLiked = liked;
    }

    public boolean isDisliked() {
        return isDisliked;
    }

    public void setDisliked(boolean disliked) {
        isDisliked = disliked;
    }
}
