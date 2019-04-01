package com.example.mitrais.onestopclick.model.firestore;

import com.example.mitrais.onestopclick.model.Profile;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class ProfileService {
    private static final String REF_PROFILE = "profile";
    private static final String KEY_ADDRESS = "address";
    private static final String KEY_PROFILE_IMAGE_URI = "profileImageUri";
    private static final String KEY_PROFILE_IMAGE_FILE_NAME = "profileImageFileName";
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

    // set new profile
    public Task<Void> addProfile(Profile profile) {
        DocumentReference reference = profileRef.document(profile.getEmail());
        return reference.set(profile);
    }

    // save existing profile non image data
    public Task<Void> saveProfile(Profile profile) {
        DocumentReference reference = profileRef.document(profile.getEmail());

        Map<String, Object> profileMap = new HashMap<>();
        profileMap.put(KEY_ADDRESS, profile.getAddress());

        return reference.update(profileMap);
    }

    // save existing profile image data
    public Task<Void> saveProfileImageData(Profile profile) {
        DocumentReference reference = profileRef.document(profile.getEmail());

        Map<String, Object> profileMap = new HashMap<>();
        profileMap.put(KEY_PROFILE_IMAGE_URI, profile.getProfileImageUri());
        profileMap.put(KEY_PROFILE_IMAGE_FILE_NAME, profile.getProfileImageFileName());

        return reference.update(profileMap);
    }

    // get profile by email
    public Task<DocumentSnapshot> getProfileByEmail(String email) {
        DocumentReference reference = profileRef.document(email);
        return reference.get();
    }

    // get profile document reference
    public DocumentReference getProfileDocumentReference(String email) {
        return profileRef.document(email);
    }
}
