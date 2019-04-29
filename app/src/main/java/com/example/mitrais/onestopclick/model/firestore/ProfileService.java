package com.example.mitrais.onestopclick.model.firestore;

import com.example.mitrais.onestopclick.model.Profile;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileService {
    private static final String REF_PROFILE = "profile";
    private static ProfileService instance;
    private static CollectionReference profileRef;

    /**
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
     * @param profile user profile
     * @return save profile task
     */
    public Task<Void> saveProfile(Profile profile) {
        DocumentReference reference = profileRef.document(profile.getEmail());
        return reference.set(profile);
    }

    /**
     * @param email user email address
     * @return sync profile task
     */
    public Task<DocumentSnapshot> syncProfile(String email) {
        DocumentReference reference = profileRef.document(email);
        return reference.get();
    }

    /**
     * @param email user email address
     * @return profile document reference
     */
    public DocumentReference getProfileRef(String email) {
        return profileRef.document(email);
    }
}
