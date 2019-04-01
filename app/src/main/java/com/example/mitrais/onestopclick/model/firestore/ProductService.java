package com.example.mitrais.onestopclick.model.firestore;

import com.example.mitrais.onestopclick.model.Product;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class ProductService {
    private static final String REF_PRODUCT = "product";
    private static final String KEY_LIKE = "like";
    private static final String KEY_DISLIKE = "dislike";
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

    // increase product like count
    public Task<Void> addLike(String productId) {
        return firestore.runTransaction(transaction -> {
            DocumentReference productDocRef = productRef.document(productId);
            Product product = transaction.get(productDocRef).toObject(Product.class);
            if (product != null) {
                long newLikeCount = product.getLike() + 1;
                transaction.update(productDocRef, KEY_LIKE, newLikeCount);
            }
            return null;
        });
    }

    // increase product dislike count
    public Task<Void> addDislike(String productId) {
        return firestore.runTransaction(transaction -> {
            DocumentReference productDocRef = productRef.document(productId);
            Product product = transaction.get(productDocRef).toObject(Product.class);
            if (product != null) {
                long newLikeCount = product.getDislike() + 1;
                transaction.update(productDocRef, KEY_DISLIKE, newLikeCount);
            }
            return null;
        });
    }

    // return product reference
    public static CollectionReference getProductRef() {
        return productRef;
    }
}
