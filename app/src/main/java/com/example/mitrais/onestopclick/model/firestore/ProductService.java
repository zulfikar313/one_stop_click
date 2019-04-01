package com.example.mitrais.onestopclick.model.firestore;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class ProductService {
    private static final String REF_PRODUCT = "product";
    private static ProductService instance;
    private static FirebaseFirestore firestore;
    private static CollectionReference productRef;

    public static ProductService getInstance() {
        if (instance == null) {
            instance = new ProductService();
            firestore = FirebaseFirestore.getInstance();
            productRef = firestore.collection(REF_PRODUCT);
        }

        return instance;
    }

    // get all products
    public Task<QuerySnapshot> getAllProducts() {
        return productRef.get();
    }

    // return product reference
    public static CollectionReference getProductRef() {
        return productRef;
    }
}
