package com.example.mitrais.onestopclick;

public class Constant {
    public static final int PROGRESS_DELAY = 1000;

    // preferences
    public static final String PREF_LAST_LOGGED_IN_EMAIL = "PREF_LAST_LOGGED_IN_EMAIL";
    public static final String PREF_IS_REMEMBER_ME_ENABLED = "PREF_IS_REMEMBER_ME_ENABLED";

    // intent animations
    // more animation: https://github.com/hajiyevelnur92/intentanimation
    public static final String ANIMATION_FADEIN_TO_FADEOUT = "fadein-to-fadeout";

    // product types
    public static final String PRODUCT_TYPE_ALL = "all";
    public static final String PRODUCT_TYPE_BOOK = "book";
    public static final String PRODUCT_TYPE_MUSIC = "music";
    public static final String PRODUCT_TYPE_MOVIE = "movie";

    // extras
    public static final String EXTRA_PRODUCT_ID = "EXTRA_PRODUCT_ID";
    public static final String EXTRA_BOOK_URI = "EXTRA_BOOK_URI";
    public static final String EXTRA_MOVIE_URI = "EXTRA_MOVIE_URI";

    // filename extensions
    public static final String NAME_EXT_THUMBNAIL = "_thmb_1."; // TODO: support multiple thumbnails
    public static final String NAME_EXT_BOOK_PDF = "_book_1.pdf"; // TODO: support multiple books
    public static final String NAME_EXT_MUSIC = "_music.";
    public static final String NAME_EXT_TRAILER = "_trailer.";
    public static final String NAME_EXT_MOVIE = "_movie.";

    public static final String WEB_CLIENT_ID = "665475513149-6tbkh6j6u5jeg0g2e6kc3g4fvdfdsic7.apps.googleusercontent.com";
}
