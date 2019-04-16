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
     * set product image and returns set product image task
     *
     * @param product existing product
     * @return set product image task
     */
    public Task<Void> setProductImage(Product product) {
        DocumentReference docRef = productRef.document(product.getId());

        Map<String, Object> map = new HashMap<>();
        map.put(KEY_THUMBNAIL_URI, product.getThumbnailUri());
        map.put(KEY_THUMBNAIL_FILENAME, product.getThumbnailFilename());

        return docRef.update(map);
    }

    /**
     * set product details and returns set product details task
     *
     * @param product existing product
     * @return set product details task
     */
    public Task<Void> setProductDetails(Product product) {
        DocumentReference docRef = productRef.document(product.getId());

        Map<String, Object> map = new HashMap<>();
        map.put(KEY_TITLE, product.getTitle());
        map.put(KEY_AUTHOR, product.getAuthor());
        map.put(KEY_ARTIST, product.getArtist());
        map.put(KEY_DIRECTOR, product.getDirector());
        map.put(KEY_DESCRIPTION, product.getDescription());
        map.put(KEY_TYPE, product.getType());

        return docRef.update(map);
    }

    /**
     * add new product with image data only
     * and returns add new product task
     *
     * @param product new product
     * @return add product image task
     */
    public Task<DocumentReference> addProductImage(Product product) {
        Map<String, Object> map = new HashMap<>();
        map.put(KEY_THUMBNAIL_URI, product.getThumbnailUri());
        map.put(KEY_THUMBNAIL_FILENAME, product.getThumbnailFilename());

        return productRef.add(map);
    }

    /**
     * add new product with details only
     * and returns add new product task
     *
     * @param product new product
     * @return add product details task
     */
    public Task<DocumentReference> addProductDetails(Product product) {
        Map<String, Object> map = new HashMap<>();
        map.put(KEY_TITLE, product.getTitle());
        map.put(KEY_AUTHOR, product.getAuthor());
        map.put(KEY_ARTIST, product.getArtist());
        map.put(KEY_DIRECTOR, product.getDirector());
        map.put(KEY_DESCRIPTION, product.getDescription());
        map.put(KEY_TYPE, product.getType());

        return productRef.add(map);
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
