package com.example.mitrais.onestopclick.model.firestore;

import com.example.mitrais.onestopclick.model.ProfileProduct;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

public class ProfileProductService {
    private static ProfileProductService instance;
    private static final String REF_PROFILE_PRODUCT = "profile_product";
    private static final String KEY_EMAIL = "email";
    private static CollectionReference profileProductRef;

    public static ProfileProductService getInstance() {
        if (instance == null) {
            instance = new ProfileProductService();
            FirebaseFirestore firestore = FirebaseFirestore.getInstance();
            profileProductRef = firestore.collection(REF_PROFILE_PRODUCT);
        }

        return instance;
    }

    /**
     * @param profileProduct ProfileProduct object
     * @return task
     */
    public Task<Void> saveProfileProduct(ProfileProduct profileProduct) {
        DocumentReference reference = profileProductRef.document(generateFirestoreId(profileProduct));
        return reference.set(profileProduct);
    }

    /**
     * @param email email address
     * @return task
     */
    public Task<QuerySnapshot> syncProfileProduct(String email) {
        return profileProductRef.whereEqualTo(KEY_EMAIL, email).get();
    }

    /**
     * @param email email address
     * @return profile product collection reference
     */
    public Query getProfileProductRef(String email) {
        return profileProductRef.whereEqualTo(KEY_EMAIL, email);
    }

    private String generateFirestoreId(ProfileProduct profileProduct) {
        return profileProduct.getEmail() + "_" + profileProduct.getProductId();
    }
}
