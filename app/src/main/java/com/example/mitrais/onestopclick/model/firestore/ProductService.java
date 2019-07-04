package com.example.mitrais.onestopclick.model.firestore;

import android.net.Uri;

import com.example.mitrais.onestopclick.model.Comment;
import com.example.mitrais.onestopclick.model.Product;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class ProductService {
    private static final String REF_PRODUCT = "product";
    private static final String REF_COMMENT = "comment";
    private static final String KEY_THUMBNAIL_URI = "thumbnailUri";
    private static final String KEY_BOOK_URI = "bookUri";
    private static final String KEY_MUSIC_URI = "musicUri";
    private static final String KEY_TRAILER_URI = "trailerUri";
    private static final String KEY_MOVIE_URI = "movieUri";
    private static final String KEY_RATING = "rating";
    private static ProductService instance;
    private static CollectionReference productReference;

    public static ProductService getInstance() {
        if (instance == null) {
            instance = new ProductService();
            FirebaseFirestore firestore = FirebaseFirestore.getInstance();
            productReference = firestore.collection(REF_PRODUCT);
        }

        return instance;
    }

    // region product services
    public Task<QuerySnapshot> syncAllProducts() {
        return productReference.get();
    }

    public Task<DocumentSnapshot> syncProductById(String productId) {
        return productReference.document(productId).get();
    }

    public Task<DocumentReference> addProduct(Product product) {
        return productReference.add(product);
    }

    public Task<Void> saveProduct(Product product) {
        DocumentReference docRef = productReference.document(product.getId());
        return docRef.set(product);
    }

    public Task<Void> saveProductThumbnailUri(String id, Uri uri) {
        DocumentReference docRef = productReference.document(id);
        Map<String, Object> map = new HashMap<>();
        map.put(KEY_THUMBNAIL_URI, uri.toString());
        return docRef.update(map);
    }

    public Task<Void> saveBookFileUri(String id, Uri uri) {
        DocumentReference docRef = productReference.document(id);
        Map<String, Object> map = new HashMap<>();
        map.put(KEY_BOOK_URI, uri.toString());
        return docRef.update(map);
    }

    public Task<Void> saveMusicFileUri(String id, Uri uri) {
        DocumentReference docRef = productReference.document(id);
        Map<String, Object> map = new HashMap<>();
        map.put(KEY_MUSIC_URI, uri.toString());
        return docRef.update(map);
    }

    public Task<Void> saveTrailerFileUri(String id, Uri uri) {
        DocumentReference docRef = productReference.document(id);
        Map<String, Object> map = new HashMap<>();
        map.put(KEY_TRAILER_URI, uri.toString());
        return docRef.update(map);
    }

    public Task<Void> saveMovieFileUri(String id, Uri uri) {
        DocumentReference docRef = productReference.document(id);
        Map<String, Object> map = new HashMap<>();
        map.put(KEY_MOVIE_URI, uri.toString());
        return docRef.update(map);
    }

    public Task<Void> saveProductRating(String id, HashMap<String, Float> ratings) {
        DocumentReference docRef = productReference.document(id);
        Map<String, Object> map = new HashMap<>();
        map.put(KEY_RATING, ratings);
        return docRef.update(map);
    }

    public static CollectionReference getProductReference() {
        return productReference;
    }
    // endregion

    // region comment services
    public Task<QuerySnapshot> syncCommentByProductId(String productId) {
        CollectionReference reference = productReference.document(productId).collection(REF_COMMENT);
        return reference.get();
    }

    public Task<DocumentReference> addComment(String productId, Comment comment) {
        return productReference.document(productId).collection(REF_COMMENT)
                .add(comment);
    }

    public CollectionReference getCommentReference(String productId) {
        return productReference.document(productId).collection(REF_COMMENT);
    }
    // endregion
}
