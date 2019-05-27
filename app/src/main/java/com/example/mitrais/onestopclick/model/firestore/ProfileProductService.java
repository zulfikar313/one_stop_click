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
     * @return task
     */
    public Task<QuerySnapshot> syncProfileProduct() {
        return profileProductRef.get();
    }

    /**
     * @return profile product collection reference
     */
    public Query getProfileProductRef() {
        return profileProductRef;
    }

    private String generateFirestoreId(ProfileProduct profileProduct) {
        return profileProduct.getEmail() + "_" + profileProduct.getProductId();
    }
}