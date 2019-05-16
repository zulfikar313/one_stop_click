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
    private static final String KEY_THUMBNAIL_URI = "thumbnailUri";
    private static final String KEY_BOOK_URI = "bookUri";
    private static final String KEY_MUSIC_URI = "musicUri";
    private static final String KEY_TRAILER_URI = "trailerUri";
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

    /**
     * sync local product data
     *
     * @return task
     */
    public Task<QuerySnapshot> syncProducts() {
        return productRef.get();
    }

    /**
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
     * @param product product object
     * @return task
     */
    public Task<Void> saveProduct(Product product) {
        DocumentReference docRef = productRef.document(product.getId());
        return docRef.set(product);
    }

    /**
     * @param id  product id
     * @param uri thumbnail uri
     * @return task
     */
    public Task<Void> saveProductThumbnailUri(String id, Uri uri) {
        DocumentReference docRef = productRef.document(id);

        Map<String, Object> map = new HashMap<>();
        map.put(KEY_THUMBNAIL_URI, uri.toString());

        return docRef.update(map);
    }

    /**
     * @param id  product id
     * @param uri book uri
     * @return task
     */
    public Task<Void> saveBookUri(String id, Uri uri) {
        DocumentReference docRef = productRef.document(id);

        Map<String, Object> map = new HashMap<>();
        map.put(KEY_BOOK_URI, uri.toString());

        return docRef.update(map);
    }

    /**
     * @param id  product id
     * @param uri music uri
     * @return task
     */
    public Task<Void> saveProductMusicUri(String id, Uri uri) {
        DocumentReference docRef = productRef.document(id);

        Map<String, Object> map = new HashMap<>();
        map.put(KEY_MUSIC_URI, uri.toString());

        return docRef.update(map);
    }

    /**
     * @param id  product id
     * @param uri trailer uri
     * @return task
     */
    public Task<Void> saveProductTrailerUri(String id, Uri uri) {
        DocumentReference docRef = productRef.document(id);

        Map<String, Object> map = new HashMap<>();
        map.put(KEY_TRAILER_URI, uri.toString());

        return docRef.update(map);
    }

    /**
     * @param product product object
     * @return task
     */
    public Task<DocumentReference> addProduct(Product product) {
        return productRef.add(product);
    }

    /**
     * @return product reference
     */
    public static CollectionReference getProductRef() {
        return productRef;
    }
}
