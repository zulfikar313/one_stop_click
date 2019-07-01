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
    private static CollectionReference productRef;

    public static ProductService getInstance() {
        if (instance == null) {
            instance = new ProductService();
            FirebaseFirestore firestore = FirebaseFirestore.getInstance();
            productRef = firestore.collection(REF_PRODUCT);
        }

        return instance;
    }

    public Task<QuerySnapshot> sync() {
        return productRef.get();
    }

    public Task<QuerySnapshot> syncComments(String productId) {
        CollectionReference reference = productRef.document(productId).collection(REF_COMMENT);
        return reference.get();
    }

    public Task<DocumentSnapshot> sync(String productId) {
        return productRef.document(productId).get();
    }

    public Task<DocumentReference> add(Product product) {
        return productRef.add(product);
    }

    public Task<Void> save(Product product) {
        DocumentReference docRef = productRef.document(product.getId());
        return docRef.set(product);
    }

    public Task<Void> saveThumbnailUri(String id, Uri uri) {
        DocumentReference docRef = productRef.document(id);
        Map<String, Object> map = new HashMap<>();
        map.put(KEY_THUMBNAIL_URI, uri.toString());
        return docRef.update(map);
    }

    public Task<Void> saveBookUri(String id, Uri uri) {
        DocumentReference docRef = productRef.document(id);
        Map<String, Object> map = new HashMap<>();
        map.put(KEY_BOOK_URI, uri.toString());
        return docRef.update(map);
    }

    public Task<Void> saveMusicUri(String id, Uri uri) {
        DocumentReference docRef = productRef.document(id);
        Map<String, Object> map = new HashMap<>();
        map.put(KEY_MUSIC_URI, uri.toString());
        return docRef.update(map);
    }

    public Task<Void> saveTrailerUri(String id, Uri uri) {
        DocumentReference docRef = productRef.document(id);
        Map<String, Object> map = new HashMap<>();
        map.put(KEY_TRAILER_URI, uri.toString());
        return docRef.update(map);
    }

    public Task<Void> saveMovieUri(String id, Uri uri) {
        DocumentReference docRef = productRef.document(id);
        Map<String, Object> map = new HashMap<>();
        map.put(KEY_MOVIE_URI, uri.toString());
        return docRef.update(map);
    }

    public Task<Void> saveRating(String id, HashMap<String, Float> ratings) {
        DocumentReference docRef = productRef.document(id);
        Map<String, Object> map = new HashMap<>();
        map.put(KEY_RATING, ratings);
        return docRef.update(map);
    }

    public Task<DocumentReference> addComment(String productId, Comment comment) {
        return productRef.document(productId).collection(REF_COMMENT)
                .add(comment);
    }

    public Task<Void> saveComment(String productId, Comment comment) {
        return productRef.document(productId).collection(REF_COMMENT)
                .document(comment.getId()).set(comment);
    }

    public static CollectionReference getReference() {
        return productRef;
    }
}
