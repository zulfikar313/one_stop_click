package com.example.mitrais.onestopclick.model.firestore;

import android.net.Uri;

import com.example.mitrais.onestopclick.model.Product;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class ProductService {
    private static final String REF_PRODUCT = "product";
    private static final String KEY_LIKE = "like";
    private static final String KEY_DISLIKE = "dislike";
    private static final String KEY_TRAILER_URI = "trailerUri";
    private static ProductService instance;
    private static FirebaseFirestore firestore;
    private static CollectionReference productRef;

    /**
     * @return ProductService instance
     */
    public static ProductService getInstance() {
        if (instance == null) {
            instance = new ProductService();
            firestore = FirebaseFirestore.getInstance();
            productRef = firestore.collection(REF_PRODUCT);
        }

        return instance;
    }

    /**
     * @return sync products task
     */
    public Task<QuerySnapshot> syncProducts() {
        return productRef.get();
    }


    /**
     * increase like count
     *
     * @param id product id
     * @return add like task
     */
    public Task<Void> addLike(String id) {
        return firestore.runTransaction(transaction -> {
            DocumentReference docRef = productRef.document(id);
            Product product = transaction.get(docRef).toObject(Product.class);
            if (product != null) {
                long newLikeCount = product.getLike() + 1;
                transaction.update(docRef, KEY_LIKE, newLikeCount);
            }
            return null;
        });
    }

    /**
     * increase dislike count
     *
     * @param id product id
     * @return add dislike task
     */
    public Task<Void> addDislike(String id) {
        return firestore.runTransaction(transaction -> {
            DocumentReference docRef = productRef.document(id);
            Product product = transaction.get(docRef).toObject(Product.class);
            if (product != null) {
                long newLikeCount = product.getDislike() + 1;
                transaction.update(docRef, KEY_DISLIKE, newLikeCount);
            }
            return null;
        });
    }

    /**
     * save product
     *
     * @param product existing product
     * @return save product task
     */
    public Task<Void> saveProduct(Product product) {
        DocumentReference docRef = productRef.document(product.getId());
        return docRef.set(product);
    }

    /**
     * @param productId  product id
     * @param trailerUri trailer uri
     * @return
     */
    public Task<Void> saveProductTrailerUri(String productId, Uri trailerUri) {
        DocumentReference docRef = productRef.document(productId);

        Map<String, Object> map = new HashMap<>();
        map.put(KEY_TRAILER_URI, trailerUri.toString());

        return docRef.update(map);
    }


    /**
     * add new product
     *
     * @param product product
     * @return add product task
     */
    public Task<DocumentReference> addProduct(Product product) {
        return productRef.add(product);
    }

    /**
     * @return product collection reference
     */
    public static CollectionReference getProductRef() {
        return productRef;
    }
}
