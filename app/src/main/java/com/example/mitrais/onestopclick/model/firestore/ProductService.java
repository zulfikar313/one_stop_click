package com.example.mitrais.onestopclick.model.firestore;

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
    private static final String KEY_THUMBNAIL_FILENAME = "thumbnailFileName";
    private static final String KEY_TITLE = "title";
    private static final String KEY_AUTHOR = "author";
    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_DIRECTOR = "director";
    private static final String KEY_ARTIST = "artist";
    private static final String KEY_TYPE = "type";
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

    // save existing product image data
    public Task<Void> saveProductImageData(Product product) {
        DocumentReference reference = productRef.document(product.getId());

        Map<String, Object> productMap = new HashMap<>();
        productMap.put(KEY_THUMBNAIL_URI, product.getThumbnailUri());
        productMap.put(KEY_THUMBNAIL_FILENAME, product.getThumbnailFileName());

        return reference.update(productMap);
    }

    // save existing product non image data
    public Task<Void> saveProduct(Product product) {
        DocumentReference reference = productRef.document(product.getId());

        Map<String, Object> productMap = new HashMap<>();
        productMap.put(KEY_TITLE, product.getTitle());
        productMap.put(KEY_AUTHOR, product.getAuthor());
        productMap.put(KEY_ARTIST, product.getArtist());
        productMap.put(KEY_DIRECTOR, product.getDirector());
        productMap.put(KEY_DESCRIPTION, product.getDescription());
        productMap.put(KEY_TYPE, product.getType());

        return reference.update(productMap);
    }

    // return product reference
    public static CollectionReference getProductRef() {
        return productRef;
    }
}
