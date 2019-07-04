package com.example.mitrais.onestopclick.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.support.annotation.NonNull;

import com.google.firebase.firestore.Exclude;

import java.util.Date;

@Entity(tableName = "comment", primaryKeys = {"productId", "email", "date"})
public class Comment {
    @Ignore
    private String id;
    @NonNull
    private String productId = "";
    @NonNull
    private String email = "";
    @NonNull
    private Date date = new Date();
    private String username;
    private String userImageUri;
    private float userRate;

    private String content;

    @Ignore
    public Comment() {
    }

    public Comment(String productId, String email, String username, String userImageUri, float userRate, Date date, String content) {
        this.productId = productId != null ? productId : "";
        this.email = email != null ? email : "";
        this.username = username != null ? username : "";
        this.userImageUri = userImageUri != null ? userImageUri : "";
        this.userRate = userRate;
        this.date = date != null ? date : new Date();
        this.content = content != null ? content : "";
    }

    @Exclude
    public String getId() {
        return id;
    }

    @Exclude
    public void setId(String id) {
        this.id = id;
    }

    @Exclude
    @NonNull
    public String getProductId() {
        return productId;
    }

    @Exclude
    public void setProductId(@NonNull String productId) {
        this.productId = productId;
    }

    @NonNull
    public String getEmail() {
        return email;
    }

    public void setEmail(@NonNull String email) {
        this.email = email;
    }

    @Exclude
    public String getUsername() {
        return username;
    }

    @Exclude
    public void setUsername(String username) {
        this.username = username;
    }

    @Exclude
    public String getUserImageUri() {
        return userImageUri;
    }

    @Exclude
    public void setUserImageUri(String userImageUri) {
        this.userImageUri = userImageUri;
    }

    @Exclude
    public float getUserRate() {
        return userRate;
    }

    @Exclude
    public void setUserRate(float userRate) {
        this.userRate = userRate;
    }

    @NonNull
    public Date getDate() {
        return date;
    }

    public void setDate(@NonNull Date date) {
        this.date = date;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
