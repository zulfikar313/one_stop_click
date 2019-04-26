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
    private String imageFilename;
    private String address;

    @Ignore
    public Profile() {
    }

    /**
     * @param email         profile email address
     * @param imageUri      profile image uri
     * @param imageFilename profile image filename
     * @param address       profile address
     */
    public Profile(@NonNull String email, String imageUri, String imageFilename, String address) {
        this.email = email;
        this.imageUri = imageUri == null ? "" : imageUri;
        this.imageFilename = imageFilename == null ? "" : imageFilename;
        this.address = address == null ? "" : address;
    }

    @Exclude
    @NonNull
    public String getEmail() {
        return email;
    }

    public String getImageUri() {
        return imageUri;
    }

    public String getImageFilename() {
        return imageFilename;
    }

    public String getAddress() {
        return address;
    }

    @Exclude
    public void setEmail(@NonNull String email) {
        this.email = email;
    }


    public void setAddress(String address) {
        this.address = address;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public void setImageFilename(String imageFilename) {
        this.imageFilename = imageFilename;
    }
}
