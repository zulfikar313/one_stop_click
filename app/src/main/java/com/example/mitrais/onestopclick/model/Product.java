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
    private String id = "";
    private String title;
    private String description;
    private String type;
    private String artist;
    private String author;
    private String director;
    private String thumbnailUri;
    private String bookUri;
    private String musicUri;
    private String trailerUri;
    private int like;
    private int dislike;

    @Ignore
    public Product() {
    }

    public Product(@NonNull String id, String title, String description, String type, String artist, String author, String director, String thumbnailUri, String bookUri, String musicUri, String trailerUri, int like, int dislike) {
        this.id = id;
        this.title = title != null ? title : "";
        this.description = description != null ? description : "";
        this.type = type != null ? type : "";
        this.artist = artist != null ? artist : "";
        this.author = author != null ? author : "";
        this.director = director != null ? director : "";
        this.thumbnailUri = thumbnailUri != null ? thumbnailUri : "";
        this.bookUri = bookUri != null ? bookUri : "";
        this.musicUri = musicUri != null ? musicUri : "";
        this.trailerUri = trailerUri != null ? trailerUri : "";
        this.like = like;
        this.dislike = dislike;
    }

    @Exclude
    @NonNull
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

    public String getThumbnailUri() {
        return thumbnailUri;
    }

    public int getLike() {
        return like;
    }

    public int getDislike() {
        return dislike;
    }

    public void setThumbnailUri(String thumbnailUri) {
        this.thumbnailUri = thumbnailUri;
    }

    public String getBookUri() {
        return bookUri;
    }

    public String getMusicUri() {
        return musicUri;
    }

    public String getTrailerUri() {
        return trailerUri;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setDirector(String director) {
        this.director = director;
    }
}
