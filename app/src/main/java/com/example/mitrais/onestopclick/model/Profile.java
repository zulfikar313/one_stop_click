package com.example.mitrais.onestopclick.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.google.firebase.firestore.Exclude;

@Entity(tableName = "profile")
public class Profile {
    @PrimaryKey
    @NonNull
    private String email = "";
    private String imageUri;
    private String address;
    private boolean isAdmin;

    @Ignore
    public Profile() {
    }

    public Profile(@NonNull String email, String imageUri, String address, boolean isAdmin) {
        this.email = email;
        this.imageUri = imageUri == null ? "" : imageUri;
        this.address = address == null ? "" : address;
        this.isAdmin = isAdmin;
    }

    @Exclude
    @NonNull
    public String getEmail() {
        return email;
    }

    @Exclude
    public void setEmail(@NonNull String email) {
        this.email = email;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }
}
