package com.example.mitrais.onestopclick.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.google.firebase.firestore.Exclude;

import java.util.ArrayList;

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
    private ArrayList<String> likedBy;
    private ArrayList<String> dislikedBy;
    private int like;
    private int dislike;
    private boolean isLiked;
    private boolean isDisliked;

    @Ignore
    public Product() {
    }

    public Product(@NonNull String id, String title, String description, String type, String genre, String artist, String author, String director, String thumbnailUri, String bookUri, String musicUri, String trailerUri, String movieUri, ArrayList<String> likedBy, ArrayList<String> dislikedBy, int like, int dislike, boolean isLiked, boolean isDisliked) {
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
        this.likedBy = likedBy != null ? likedBy : new ArrayList<>();
        this.dislikedBy = dislikedBy != null ? dislikedBy : new ArrayList<>();
        this.like = like;
        this.dislike = dislike;
        this.isLiked = isLiked;
        this.isDisliked = isDisliked;
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

    public ArrayList<String> getLikedBy() {
        return likedBy;
    }

    public void setLikedBy(ArrayList<String> likedBy) {
        this.likedBy = likedBy;
    }

    public ArrayList<String> getDislikedBy() {
        return dislikedBy;
    }

    public void setDislikedBy(ArrayList<String> dislikedBy) {
        this.dislikedBy = dislikedBy;
    }

    public int getLike() {
        return like;
    }

    public void setLike(int like) {
        this.like = like;
    }

    public int getDislike() {
        return dislike;
    }

    public void setDislike(int dislike) {
        this.dislike = dislike;
    }

    @Exclude
    public boolean isLiked() {
        return isLiked;
    }

    @Exclude
    public void setLiked(boolean liked) {
        isLiked = liked;
    }

    @Exclude
    public boolean isDisliked() {
        return isDisliked;
    }

    @Exclude
    public void setDisliked(boolean disliked) {
        isDisliked = disliked;
    }
}


