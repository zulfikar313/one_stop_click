package com.example.mitrais.onestopclick.model.firestore;

import com.example.mitrais.onestopclick.model.Product;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

/**
 * ProductService provide access to product data in firestore
 */
public class ProductService {
    private static final String REF_PRODUCT = "product";
    private static final String KEY_LIKE = "like";
    private static final String KEY_DISLIKE = "dislike";
    private static final String KEY_THUMBNAIL_URI = "thumbnailUri";
    private static final String KEY_THUMBNAIL_FILENAME = "thumbnailFilename";
    private static final String KEY_TITLE = "title";
    private static final String KEY_AUTHOR = "author";
    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_DIRECTOR = "director";
    private static final String KEY_ARTIST = "artist";
    private static final String KEY_TYPE = "type";
    private static ProductService instance;
    private static FirebaseFirestore firestore;
    private static CollectionReference productRef;

    /**
     * returns ProductService singleton instance
     *
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
     * returns retrieve all products task
     *
     * @return retrieve all products task
     */
    public Task<QuerySnapshot> retrieveAllProducts() {
        return productRef.get();
    }


    /**
     * increase like count and returns add like task
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
     * increase dislike count and returns add dislike task
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
     * save product and returns save product task
     *
     * @param product existing product
     * @return save product task
     */
    public Task<Void> saveProduct(Product product) {
        DocumentReference docRef = productRef.document(product.getId());
        return docRef.set(product);
    }

    /**
     * add product and returns add product task
     *
     * @param product product
     * @return add product task
     */
    public Task<DocumentReference> addProduct(Product product) {
        return productRef.add(product);
    }

    /**
     * returns product collection reference
     * collection reference can be listened for changes
     *
     * @return product collection reference
     */
    public static CollectionReference getProductRef() {
        return productRef;
    }
}
