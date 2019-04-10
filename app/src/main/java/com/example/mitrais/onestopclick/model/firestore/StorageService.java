package com.example.mitrais.onestopclick.model.firestore;

import android.net.Uri;

import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class StorageService {
    private static final String REF_PROFILE_IMG = "profile_image";
    private static final String REF_PRODUCT_IMG = "product_image";
    private static StorageService instance;
    private static StorageReference profileImgRef;
    private static StorageReference productImgRef;

    public static StorageService getInstance() {
        if (instance == null) {
            instance = new StorageService();
            FirebaseStorage storage = FirebaseStorage.getInstance();
            profileImgRef = storage.getReference(REF_PROFILE_IMG);
            productImgRef = storage.getReference(REF_PRODUCT_IMG);
        }

        return instance;
    }

    // save profile image
    public Task<Uri> saveProfileImage(Uri uri, String fileName) {
        StorageReference reference = profileImgRef.child(fileName);
        return reference.putFile(uri).continueWithTask(task -> reference.getDownloadUrl());
    }

    // save product image
    public Task<Uri> saveProductImage(Uri uri, String filename) {
        StorageReference reference = productImgRef.child(filename);
        return reference.putFile(uri).continueWithTask(task -> reference.getDownloadUrl());
    }
}
