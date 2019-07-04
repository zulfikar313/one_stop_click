package com.example.mitrais.onestopclick.model.firestore;

import android.net.Uri;

import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class StorageService {
    private static final String REF_PROFILE_IMG = "profile_image";
    private static final String REF_PRODUCT_IMG = "product_image";
    private static final String REF_BOOK = "book";
    private static final String REF_MUSIC = "music";
    private static final String REF_TRAILER = "trailer";
    private static final String REF_MOVIE = "movie";
    private static StorageService instance;
    private static StorageReference profileImageReference;
    private static StorageReference productImageReference;
    private static StorageReference bookReference;
    private static StorageReference musicReference;
    private static StorageReference trailerReference;
    private static StorageReference movieReference;

    public static StorageService getInstance() {
        if (instance == null) {
            instance = new StorageService();
            FirebaseStorage storage = FirebaseStorage.getInstance();
            profileImageReference = storage.getReference(REF_PROFILE_IMG);
            productImageReference = storage.getReference(REF_PRODUCT_IMG);
            bookReference = storage.getReference(REF_BOOK);
            musicReference = storage.getReference(REF_MUSIC);
            trailerReference = storage.getReference(REF_TRAILER);
            movieReference = storage.getReference(REF_MOVIE);
        }

        return instance;
    }

    public FileDownloadTask downloadBook(Uri localUri, String filename) {
        StorageReference reference = bookReference.child(filename);
        return reference.getFile(localUri);
    }

    public Task<Uri> uploadProfileImage(Uri uri, String filename) {
        StorageReference reference = profileImageReference.child(filename);
        return reference.putFile(uri).continueWithTask(task -> reference.getDownloadUrl());
    }

    public Task<Uri> uploadProductThumbnail(Uri uri, String filename) {
        StorageReference reference = productImageReference.child(filename);
        return reference.putFile(uri).continueWithTask(task -> reference.getDownloadUrl());
    }

    public Task<Uri> uploadBookFile(Uri uri, String filename) {
        StorageReference reference = bookReference.child(filename);
        return reference.putFile(uri).continueWithTask(task -> reference.getDownloadUrl());
    }

    public Task<Uri> uploadMusicFile(Uri uri, String filename) {
        StorageReference reference = musicReference.child(filename);
        return reference.putFile(uri).continueWithTask(task -> reference.getDownloadUrl());
    }

    public Task<Uri> uploadTrailerFile(Uri uri, String filename) {
        StorageReference reference = trailerReference.child(filename);
        return reference.putFile(uri).continueWithTask(task -> reference.getDownloadUrl());
    }

    public Task<Uri> uploadMovieFile(Uri uri, String filename) {
        StorageReference reference = movieReference.child(filename);
        return reference.putFile(uri).continueWithTask(task -> reference.getDownloadUrl());
    }
}
