package com.example.mitrais.onestopclick.model.firestore;

import android.net.Uri;

import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class StorageService {
    private static final String REF_PROFILE_IMG = "profile_image";
    private static final String REF_PRODUCT_IMG = "product_image";
    private static final String REF_TRAILER = "trailer";
    private static final String REF_MUSIC = "music";
    private static final String REF_BOOK = "book";
    private static StorageService instance;
    private static StorageReference profileImgRef;
    private static StorageReference productImgRef;
    private static StorageReference bookRef;
    private static StorageReference musicRef;
    private static StorageReference trailerRef;

    public static StorageService getInstance() {
        if (instance == null) {
            instance = new StorageService();
            FirebaseStorage storage = FirebaseStorage.getInstance();
            profileImgRef = storage.getReference(REF_PROFILE_IMG);
            productImgRef = storage.getReference(REF_PRODUCT_IMG);
            bookRef = storage.getReference(REF_BOOK);
            musicRef = storage.getReference(REF_MUSIC);
            trailerRef = storage.getReference(REF_TRAILER);
        }

        return instance;
    }

    /**
     * @param uri      profile image uri
     * @param filename profile image filename
     * @return task
     */
    public Task<Uri> uploadProfileImage(Uri uri, String filename) {
        StorageReference reference = profileImgRef.child(filename);
        return reference.putFile(uri).continueWithTask(task -> reference.getDownloadUrl());
    }

    /**
     * @param uri      thumbnail uri
     * @param filename thumbnail filename
     * @return upload thumnail task
     */
    public Task<Uri> uploadThumbnail(Uri uri, String filename) {
        StorageReference reference = productImgRef.child(filename);
        return reference.putFile(uri).continueWithTask(task -> reference.getDownloadUrl());
    }

    /**
     * @param uri      book uri
     * @param filename book filename
     * @return task
     */
    public Task<Uri> uploadBook(Uri uri, String filename) {
        StorageReference reference = bookRef.child(filename);
        return reference.putFile(uri).continueWithTask(task -> reference.getDownloadUrl());
    }

    /**
     * @param localUri book local uri
     * @param filename book filename
     * @return task
     */
    public FileDownloadTask downloadBook(Uri localUri, String filename) {
        StorageReference reference = bookRef.child(filename);
        return reference.getFile(localUri);
    }

    /**
     * @param uri      music uri
     * @param filename music filename
     * @return upload music task
     */
    public Task<Uri> uploadMusic(Uri uri, String filename) {
        StorageReference reference = musicRef.child(filename);
        return reference.putFile(uri).continueWithTask(task -> reference.getDownloadUrl());
    }

    /**
     * @param uri      trailer uri
     * @param filename trailer filename
     * @return upload trailer task
     */
    public Task<Uri> uploadTrailer(Uri uri, String filename) {
        StorageReference reference = trailerRef.child(filename);
        return reference.putFile(uri).continueWithTask(task -> reference.getDownloadUrl());
    }
}
