package com.example.mitrais.onestopclick.model.firestore;

import android.net.Uri;

import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class StorageService {
    private static final String REF_PROFILE_IMG = "profile_image";
    private static StorageService instance;
    private static StorageReference profileImRef;

    public static StorageService getInstance() {
        if (instance == null) {
            instance = new StorageService();
            FirebaseStorage storage = FirebaseStorage.getInstance();
            profileImRef = storage.getReference(REF_PROFILE_IMG);
        }

        return instance;
    }

    // save image profile
    public Task<Uri> saveProfileImage(Uri uri, String fileName) {
        StorageReference reference = profileImRef.child(fileName);
        return reference.putFile(uri).continueWithTask(task -> reference.getDownloadUrl());
    }
}
