package com.example.mitrais.onestopclick.model.firestore;

import com.example.mitrais.onestopclick.model.Profile;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

/**
 * ProfileService class provide access to profile data in firestore
 */
public class ProfileService {
    private static final String REF_PROFILE = "profile";
    private static final String KEY_ADDRESS = "address";
    private static final String KEY_PROFILE_IMAGE_URI = "profileImageUri";
    private static final String KEY_PROFILE_IMAGE_FILE_NAME = "profileImageFileName";
    private static ProfileService instance;
    private static CollectionReference profileRef;

    /**
     * returns ProfileService singleton instance
     *
     * @return ProfileService instance
     */
    public static ProfileService getInstance() {
        if (instance == null) {
            instance = new ProfileService();
            FirebaseFirestore firestore = FirebaseFirestore.getInstance();
            profileRef = firestore.collection(REF_PROFILE);
        }

        return instance;
    }

    /**
     * add new profile and returns add profile task
     *
     * @param profile
     * @return save profile task
     */
    public Task<Void> saveProfile(Profile profile) {
        DocumentReference reference = profileRef.document(profile.getEmail());
        return reference.set(profile);
    }

    /**
     * returns retrieve profile by email task
     *
     * @param email user email address
     * @return retrieve profile by email task
     */
    public Task<DocumentSnapshot> retrieveProfileByEmail(String email) {
        DocumentReference reference = profileRef.document(email);
        return reference.get();
    }

    /**
     * returns profile document reference by email
     *
     * @param email user email address
     * @return profile document reference
     */
    public DocumentReference retrieveProfileDocRef(String email) {
        return profileRef.document(email);
    }
}
