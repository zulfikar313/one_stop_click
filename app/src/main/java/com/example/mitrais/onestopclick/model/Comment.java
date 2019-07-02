package com.example.mitrais.onestopclick.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.google.firebase.firestore.Exclude;

import java.util.Date;

@Entity(tableName = "comment")
public class Comment {
    @PrimaryKey
    @NonNull
    private String id = "";
    private String productId;
    private String email;
    private String username;
    private String userImageUri;
    @Ignore
    private float userRate;
    private Date date;
    private String content;

    @Ignore
    public Comment() {
    }

    public Comment(@NonNull String id, String productId, String email, String username, String userImageUri, Date date, String content) {
        this.id = id;
        this.productId = productId != null ? productId : "";
        this.email = email != null ? email : "";
        this.username = username != null ? username : "";
        this.userImageUri = userImageUri != null ? userImageUri : "";
        this.date = date != null ? date : new Date();
        this.content = content != null ? content : "";
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    @Exclude
    public String getProductId() {
        return productId;
    }

    @Exclude
    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
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
    @Ignore
    public float getUserRate() {
        return userRate;
    }

    @Exclude
    @Ignore
    public void setUserRate(float userRate) {
        this.userRate = userRate;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
