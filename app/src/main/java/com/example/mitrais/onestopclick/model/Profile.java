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
    private String profileImageUri;
    private String profileImageFilename;
    private String address;

    @Ignore
    public Profile() {
    }

    /**
     * @param email profile email address
     * @param profileImageUri profile image uri
     * @param profileImageFilename profile image filename
     * @param address profile address
     */
    public Profile(@NonNull String email, String profileImageUri, String profileImageFilename, String address) {
        this.email = email;
        this.profileImageUri = profileImageUri;
        this.profileImageFilename = profileImageFilename;
        this.address = address;
    }

    @Exclude
    public void setEmail(@NonNull String email) {
        this.email = email;
    }

    @Exclude
    @NonNull
    public String getEmail() {
        return email;
    }

    /**
     * @return profile image uri
     */
    public String getProfileImageUri() {
        return profileImageUri;
    }

    /**
     * @return profile image filename
     */
    public String getProfileImageFilename() {
        return profileImageFilename;
    }

    /**
     * @return user address
     */
    public String getAddress() {
        return address;
    }


}
