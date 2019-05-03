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
    private String contentUri;
    private String contentFilename;
    private String thumbnailUri;
    private String thumbnailFilename;
    private String musicUri;
    private String trailerUri;
    private int like;
    private int dislike;

    @Ignore
    public Product() {
    }

    /**
     * @param id                product id
     * @param title             product title
     * @param description       product description
     * @param type              product type either book, music or movie
     * @param artist            used if product type is music
     * @param author            used if product type is book
     * @param director          used if product type is movie
     * @param contentUri        content uri for product file
     * @param contentFilename   content filename in storage
     * @param thumbnailUri      thumbnail uri depicting product
     * @param thumbnailFilename thumbnail filename in storage
     * @param musicUri          music uri
     * @param trailerUri        trailer uri
     * @param like              product like count
     * @param dislike           product dislike count
     */
    public Product(@NonNull String id, String title, String description, String type, String artist, String author, String director, String contentUri, String contentFilename, String thumbnailUri, String thumbnailFilename, String musicUri, String trailerUri, int like, int dislike) {
        this.id = id;
        this.title = title == null ? " " : title;
        this.description = description == null ? "" : description;
        this.type = type == null ? "" : type;
        this.artist = artist == null ? "" : artist;
        this.author = author == null ? "" : author;
        this.director = director == null ? "" : director;
        this.contentUri = contentUri == null ? "" : contentUri;
        this.contentFilename = contentFilename == null ? "" : contentFilename;
        this.thumbnailUri = thumbnailUri == null ? "" : thumbnailUri;
        this.thumbnailFilename = thumbnailFilename == null ? "" : thumbnailFilename;
        this.trailerUri = trailerUri == null ? "" : trailerUri;
        this.musicUri = musicUri == null ? "" : musicUri;
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

    public String getContentUri() {
        return contentUri;
    }

    public String getContentFilename() {
        return contentFilename;
    }

    public String getThumbnailUri() {
        return thumbnailUri;
    }

    public String getThumbnailFilename() {
        return thumbnailFilename;
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

    public void setThumbnailFilename(String thumbnailFilename) {
        this.thumbnailFilename = thumbnailFilename;
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
