package com.example.mitrais.onestopclick.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.google.firebase.firestore.Exclude;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Entity(tableName = "product")
public class Product {
    @PrimaryKey
    @NonNull
    private String id = "";
    private String title;
    private String description;
    private String type;
    private String genre;
    private String artist;
    private String author;
    private String director;
    private String thumbnailUri;
    private String bookUri;
    private String musicUri;
    private String trailerUri;
    private String movieUri;
    private ArrayList<String> viewedBy;
    private ArrayList<String> ownedBy;
    private ArrayList<String> putInCartBy;
    private HashMap<String, Float> rating;
    private boolean isOwned;
    private boolean isInCart;
    private float price;

    @Ignore
    private List<Comment> comments;

    @Ignore
    public Product() {
    }

    public Product(@NonNull String id, String title, String description, String type, String genre, String artist, String author,
                   String director, String thumbnailUri, String bookUri, String musicUri, String trailerUri, String movieUri, ArrayList<String> viewedBy, ArrayList<String> ownedBy,
                   HashMap<String, Float> rating, ArrayList<String> putInCartBy, boolean isOwned, boolean isInCart, float price) {
        this.id = id;
        this.title = title != null ? title : "";
        this.description = description != null ? description : "";
        this.type = type != null ? type : "";
        this.genre = genre != null ? genre : "";
        this.artist = artist != null ? artist : "";
        this.author = author != null ? author : "";
        this.director = director != null ? director : "";
        this.thumbnailUri = thumbnailUri != null ? thumbnailUri : "";
        this.bookUri = bookUri != null ? bookUri : "";
        this.musicUri = musicUri != null ? musicUri : "";
        this.trailerUri = trailerUri != null ? trailerUri : "";
        this.movieUri = movieUri != null ? movieUri : "";
        this.viewedBy = viewedBy != null ? viewedBy : new ArrayList<>();
        this.ownedBy = ownedBy != null ? ownedBy : new ArrayList<>();
        this.putInCartBy = putInCartBy != null ? putInCartBy : new ArrayList<>();
        this.rating = rating != null ? rating : new HashMap<>();
        this.isOwned = isOwned;
        this.isInCart = isInCart;
        this.price = price;
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

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getThumbnailUri() {
        return thumbnailUri;
    }

    public void setThumbnailUri(String thumbnailUri) {
        this.thumbnailUri = thumbnailUri;
    }

    public String getBookUri() {
        return bookUri;
    }

    public void setBookUri(String bookUri) {
        this.bookUri = bookUri;
    }

    public String getMusicUri() {
        return musicUri;
    }

    public void setMusicUri(String musicUri) {
        this.musicUri = musicUri;
    }

    public String getTrailerUri() {
        return trailerUri;
    }

    public void setTrailerUri(String trailerUri) {
        this.trailerUri = trailerUri;
    }

    public String getMovieUri() {
        return movieUri;
    }

    public void setMovieUri(String movieUri) {
        this.movieUri = movieUri;
    }

    public ArrayList<String> getViewedBy() {
        return viewedBy;
    }

    public void setViewedBy(ArrayList<String> viewedBy) {
        this.viewedBy = viewedBy;
    }

    public ArrayList<String> getOwnedBy() {
        return ownedBy;
    }

    public void setOwnedBy(ArrayList<String> ownedBy) {
        this.ownedBy = ownedBy;
    }

    public ArrayList<String> getPutInCartBy() {
        return putInCartBy;
    }

    public void setPutInCartBy(ArrayList<String> putInCartBy) {
        this.putInCartBy = putInCartBy;
    }

    public HashMap<String, Float> getRating() {
        return rating;
    }

    public void setRating(HashMap<String, Float> rating) {
        this.rating = rating;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    @Exclude
    public List<Comment> getComments() {
        return comments;
    }

    @Exclude
    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }


    @Exclude
    public boolean isOwned() {
        return isOwned;
    }

    @Exclude
    public void setOwned(boolean owned) {
        isOwned = owned;
    }

    @Exclude
    public boolean isInCart() {
        return isInCart;
    }

    @Exclude
    public void setInCart(boolean inCart) {
        isInCart = inCart;
    }
}


