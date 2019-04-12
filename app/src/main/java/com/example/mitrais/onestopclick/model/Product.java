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
     * @param like              product like count
     * @param dislike           product dislike count
     */
    public Product(@NonNull String id, String title, String description, String type, String artist, String author, String director, String contentUri, String contentFilename, String thumbnailUri, String thumbnailFilename, int like, int dislike) {
        this.id = id;
        this.title = title = title == null ? " " : title;
        this.description = description == null ? "" : description;
        this.type = type == null ? "" : type;
        this.artist = artist == null ? "" : artist;
        this.author = author == null ? "" : author;
        this.director = director == null ? "" : director;
        this.contentUri = contentUri == null ? "" : contentUri;
        this.contentFilename = contentFilename == null ? "" : contentFilename;
        this.thumbnailUri = thumbnailUri == null ? "" : thumbnailUri;
        this.thumbnailFilename = thumbnailFilename == null ? "" : thumbnailFilename;
        this.like = like;
        this.dislike = dislike;
    }

    /**
     * @return product id
     */
    @Exclude
    @NonNull
    public String getId() {
        return id;
    }

    /**
     * @param id product id
     */
    @Exclude
    public void setId(@NonNull String id) {
        this.id = id;
    }

    /**
     * @return product title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @return product description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return product type
     */
    public String getType() {
        return type;
    }

    /**
     * @return artist
     */
    public String getArtist() {
        return artist;
    }

    /**
     * @return author
     */
    public String getAuthor() {
        return author;
    }

    /**
     * @return director
     */
    public String getDirector() {
        return director;
    }

    /**
     * @return content uri
     */
    public String getContentUri() {
        return contentUri;
    }

    /**
     * @return content filename
     */
    public String getContentFilename() {
        return contentFilename;
    }

    /**
     * @return thumbnail uri
     */
    public String getThumbnailUri() {
        return thumbnailUri;
    }

    /**
     * @return thumbnail filename
     */
    public String getThumbnailFilename() {
        return thumbnailFilename;
    }

    /**
     * @return like count
     */
    public int getLike() {
        return like;
    }

    /**
     * @return dislike count
     */
    public int getDislike() {
        return dislike;
    }

    /**
     * @param thumbnailUri thumbnail uri
     */
    public void setThumbnailUri(String thumbnailUri) {
        this.thumbnailUri = thumbnailUri;
    }

    /**
     * @param thumbnailFilename thumbnail filename
     */
    public void setThumbnailFilename(String thumbnailFilename) {
        this.thumbnailFilename = thumbnailFilename;
    }

    /**
     * @param title product title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @param description product description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @param type product type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @param artist music artist
     */
    public void setArtist(String artist) {
        this.artist = artist;
    }

    /**
     * @param author book author
     */
    public void setAuthor(String author) {
        this.author = author;
    }

    /**
     * @param director movie director
     */
    public void setDirector(String director) {
        this.director = director;
    }
}
