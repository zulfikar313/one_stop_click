package com.example.mitrais.onestopclick;

public class Constant {
    public static final int PROGRESS_DELAY = 1000;

    // preferences
    public static final String PREF_LAST_LOGGED_IN_EMAIL = "PREF_LAST_LOGGED_IN_EMAIL";
    public static final String PREF_IS_REMEMBER_ME_ENABLED = "PREF_IS_REMEMBER_ME_ENABLED";

    /**
     * intent animation
     * more animation: https://github.com/hajiyevelnur92/intentanimation
     */
    public static final String ANIMATION_FADEIN_TO_FADEOUT = "fadein-to-fadeout";

    // product types
    public static final String PRODUCT_TYPE_ALL = "all";
    public static final String PRODUCT_TYPE_BOOK = "book";
    public static final String PRODUCT_TYPE_MUSIC = "music";
    public static final String PRODUCT_TYPE_MOVIE = "movie";

    // extras
    public static final String EXTRA_PRODUCT_ID = "EXTRA_PRODUCT_ID";
    public static final String EXTRA_BOOK_URI = "EXTRA_BOOK_URI";

    // filename name extension
    public static final String NAME_EXT_THUMBNAIL = "_thmb_1."; // TODO: support multiple thumbnails
    public static final String NAME_EXT_BOOK_PDF = "_book_1.pdf"; // TODO: support multiple books
    public static final String NAME_EXT_MUSIC = "_music.";
    public static final String NAME_EXT_TRAILER = "_trailer.";

    // book & movie genre
    public static final String GENRE_FANTASY = "Fantasy";
    public static final String GENRE_HORROR = "Horror";
    public static final String GENRE_MISTERY = "Mistery";
    public static final String GENRE_SCIENCE_FICTION = "Science fiction";
    public static final String GENRE_BIOGRAPHY = "Biography";
    public static final String GENRE_CHILDREN = "Children";
    public static final String GENRE_ACTION = "Action";

    // music genre
    public static final String GENRE_ROCK = "Rock";
    public static final String GENRE_POP = "Pop";
    public static final String GENRE_JAZZ = "Jazz";
    public static final String GENRE_COUNTRY = "Country";
    public static final String GENRE_REGGAE = "Reggae";
    public static final String GENRE_CLASSIC = "Classic";
    public static final String GENRE_RAP = "Rap";
    public static final String GENRE_METAL = "Metal";

    public static final String WEB_CLIENT_ID = "665475513149-6tbkh6j6u5jeg0g2e6kc3g4fvdfdsic7.apps.googleusercontent.com";
}
