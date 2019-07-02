package com.example.mitrais.onestopclick.model.firestore;

import com.example.mitrais.onestopclick.model.Profile;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class ProfileService {
    private static final String KEY_IMAGE_URI = "imageUri";
    private static final String KEY_IS_ADMIN = "admin";
    private static final String REF_PROFILE = "profile";
    private static ProfileService instance;
    private static CollectionReference profileRef;

    public static ProfileService getInstance() {
        if (instance == null) {
            instance = new ProfileService();
            FirebaseFirestore firestore = FirebaseFirestore.getInstance();
            profileRef = firestore.collection(REF_PROFILE);
        }

        return instance;
    }

    public Task<Void> save(Profile profile) {
        DocumentReference reference = profileRef.document(profile.getEmail());
        return reference.set(profile);
    }

    public Task<Void> saveImageUri(Profile profile) {
        DocumentReference docRef = profileRef.document(profile.getEmail());
        Map<String, Object> map = new HashMap<>();
        map.put(KEY_IMAGE_URI, profile.getImageUri());
        return docRef.update(map);
    }

    public Task<Void> saveProfileAdminAccess(String email, boolean isAdmin) {
        DocumentReference docRef = profileRef.document(email);
        Map<String, Object> map = new HashMap<>();
        map.put(KEY_IS_ADMIN, isAdmin);
        return docRef.update(map);
    }

    public Task<QuerySnapshot> syncAll() {
        return profileRef.get();
    }

    public Task<DocumentSnapshot> sync(String email) {
        DocumentReference reference = profileRef.document(email);
        return reference.get();
    }

    public DocumentReference getProfileRef(String email) {
        return profileRef.document(email);
    }
}
