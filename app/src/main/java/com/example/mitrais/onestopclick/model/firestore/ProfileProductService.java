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

    public Task<Void> save(ProfileProduct profileProduct) {
        DocumentReference reference = profileProductRef.document(generateId(profileProduct));
        return reference.set(profileProduct);
    }

    public Task<QuerySnapshot> sync() {
        return profileProductRef.get();
    }

    public Query getReference() {
        return profileProductRef;
    }

    /**
     * @param profileProduct object
     * @return combination of email and product id with underscore inbetween
     */
    private String generateId(ProfileProduct profileProduct) {
        return profileProduct.getEmail() + "_" + profileProduct.getProductId();
    }
}
