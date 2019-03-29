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
    private String email;
    private String profileImageUri;
    private String profileImageFileName;
    private String address;

    @Ignore
    public Profile() {
    }

    public Profile(@NonNull String email, String profileImageUri, String profileImageFileName, String address) {
        this.email = email;
        this.profileImageUri = profileImageUri;
        this.profileImageFileName = profileImageFileName;
        this.address = address;
    }

    @Exclude
    public void setEmail(String email) {
        this.email = email;
    }

    @Exclude
    public String getEmail() {
        return email;
    }

    public String getProfileImageUri() {
        return profileImageUri;
    }

    public String getProfileImageFileName() {
        return profileImageFileName;
    }

    public String getAddress() {
        return address;
    }


}
