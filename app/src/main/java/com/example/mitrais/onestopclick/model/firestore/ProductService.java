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
    private static final String KEY_THUMBNAIL_URI = "thumbnailUri";
    private static final String KEY_BOOK_URI = "bookUri";
    private static final String KEY_MUSIC_URI = "musicUri";
    private static final String KEY_TRAILER_URI = "trailerUri";
    private static final String KEY_MOVIE_URI = "movieUri";
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

    public static CollectionReference getReference() {
        return productRef;
    }
}
