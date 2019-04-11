package com.example.mitrais.onestopclick.model.firestore;

import android.net.Uri;

import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/**
 * StorageService class provide access to FirebaseStorage
 */
public class StorageService {
    private static final String REF_PROFILE_IMG = "profile_image";
    private static final String REF_PRODUCT_IMG = "product_image";
    private static StorageService instance;
    private static StorageReference profileImgRef;
    private static StorageReference productImgRef;

    /**
     * returns StorageService singleton instance
     *
     * @return StorageService instance
     */
    public static StorageService getInstance() {
        if (instance == null) {
            instance = new StorageService();
            FirebaseStorage storage = FirebaseStorage.getInstance();
            profileImgRef = storage.getReference(REF_PROFILE_IMG);
            productImgRef = storage.getReference(REF_PRODUCT_IMG);
        }

        return instance;
    }

    /**
     * set profile image and returns set profile image task
     *
     * @param uri      profile image uri
     * @param filename profile image filename
     * @return set profile image task
     */
    public Task<Uri> setProfileImage(Uri uri, String filename) {
        StorageReference reference = profileImgRef.child(filename);
        return reference.putFile(uri).continueWithTask(task -> reference.getDownloadUrl());
    }

    /**
     * set product image and returns set product image task
     *
     * @param uri      product image uri
     * @param filename product image filename
     * @return set product image task
     */
    public Task<Uri> setProductImage(Uri uri, String filename) {
        StorageReference reference = productImgRef.child(filename);
        return reference.putFile(uri).continueWithTask(task -> reference.getDownloadUrl());
    }
}
