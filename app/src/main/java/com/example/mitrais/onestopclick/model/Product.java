package com.example.mitrais.onestopclick.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.google.firebase.firestore.Exclude;

@Entity(tableName = "product")
public class Product {
    @PrimaryKey
    @NonNull
    private String id;
    private String title;
    private String description;
    private String type;
    private String artist;
    private String author;
    private String director;
    private String contentUri;
    private String contentFileName;
    private String thumbnailUri;
    private String thumbnailFileName;
    private int like;
    private int dislike;

    @Ignore
    public Product() {
    }

    public Product(@NonNull String id, String title, String description, String type, String artist, String author, String director, String contentUri, String contentFileName, String thumbnailUri, String thumbnailFileName, int like, int dislike) {
        this.id = id;
        this.title = title = title == null ? " " : title;
        this.description = description == null ? "" : description;
        this.type = type == null ? "" : title;
        this.artist = artist == null ? "" : artist;
        this.author = author == null ? "" : author;
        this.director = director == null ? "" : director;
        this.contentUri = contentUri == null ? "" : contentUri;
        this.contentFileName = contentFileName == null ? "" : contentFileName;
        this.thumbnailUri = thumbnailUri == null ? "" : thumbnailUri;
        this.thumbnailFileName = thumbnailFileName == null ? "" : thumbnailFileName;
        this.like = like;
        this.dislike = dislike;
    }

    @Exclude
    public String getId() {
        return id;
    }

    @Exclude
    public void setId(@NonNull String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getType() {
        return type;
    }

    public String getArtist() {
        return artist;
    }

    public String getAuthor() {
        return author;
    }

    public String getDirector() {
        return director;
    }

    public String getContentUri() {
        return contentUri;
    }

    public String getContentFileName() {
        return contentFileName;
    }

    public String getThumbnailUri() {
        return thumbnailUri;
    }

    public String getThumbnailFileName() {
        return thumbnailFileName;
    }

    public int getLike() {
        return like;
    }

    public int getDislike() {
        return dislike;
    }
}
