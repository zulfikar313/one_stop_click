package com.example.mitrais.onestopclick.model.repository;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.net.Uri;
import android.util.Log;

import com.example.mitrais.onestopclick.dagger.component.DaggerRepositoryComponent;
import com.example.mitrais.onestopclick.dagger.component.RepositoryComponent;
import com.example.mitrais.onestopclick.model.Comment;
import com.example.mitrais.onestopclick.model.Ownership;
import com.example.mitrais.onestopclick.model.Product;
import com.example.mitrais.onestopclick.model.firestore.AuthService;
import com.example.mitrais.onestopclick.model.firestore.ProductService;
import com.example.mitrais.onestopclick.model.room.CommentDao;
import com.example.mitrais.onestopclick.model.room.OwnershipDao;
import com.example.mitrais.onestopclick.model.room.ProductDao;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class ProductRepository {
    private static final String TAG = "ProductRepository";
    private static ListenerRegistration listenerRegistration;

    @Inject
    ProductDao productDao;

    @Inject
    OwnershipDao ownershipDao;

    @Inject
    CommentDao commentDao;

    @Inject
    AuthService authService;

    @Inject
    ProductService productService;

    public ProductRepository(Application application) {
        initDagger(application);
        addProductListener();
    }

    // region product operations
    private void insertProduct(Product product) {
        Completable.fromAction(() -> productDao.insert(product))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onComplete() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, e.getMessage());
                    }
                });
    }

    private void deleteProduct(Product product) {
        Completable.fromAction(() -> productDao.delete(product))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onComplete() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, e.getMessage());
                    }
                });
    }

    public LiveData<Product> getProductById(String productId) {
        return productDao.getProductById(productId);
    }

    public LiveData<List<Product>> getProductByType(String productType) {
        return productDao.getProductByType(productType);
    }

    public LiveData<List<Product>> getProductByGenre(String genre) {
        return productDao.getProductByGenre(genre);
    }

    public LiveData<List<Product>> getProductByTypeAndGenre(String productType, String genre) {
        return productDao.getProducyByTypeAndGenre(productType, genre);
    }

    public LiveData<List<Product>> searchProductByQuery(String searchQuery) {
        return productDao.searchProductByQuery("%" + searchQuery + "%");
    }

    // inCart attribute is cached data only so no need product id as input
    public LiveData<List<Product>> getProductInCart() {
        return productDao.getInCart();
    }

    // isOwned attribute is cached data only so no need product id as input
    public LiveData<List<Product>> getProductOwned() {
        return productDao.getOwned();
    }
    // endregion

    // region ownership operations
    private void insertOwnership(Ownership ownership) {
        Completable.fromAction(() -> ownershipDao.insert(ownership))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onComplete() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, e.getMessage());
                    }
                });
    }
    // endregion

    // region comment operations
    public void insertComment(Comment comment) {
        Completable.fromAction(() -> commentDao.insert(comment))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onComplete() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, e.getMessage());
                    }
                });
    }

    public void deleteComment(Comment comment) {
        Completable.fromAction(() -> commentDao.delete(comment))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onComplete() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, e.getMessage());
                    }
                });
    }

    private void deleteCommentByProductId(String productId) {
        Completable.fromAction(() -> commentDao.deleteByProductId(productId))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onComplete() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, e.getMessage());
                    }
                });
    }

    public LiveData<List<Comment>> getAllComments() {
        return commentDao.getAll();
    }

    public LiveData<List<Comment>> getCommentsByProductId(String productId) {
        return commentDao.getByProductId(productId);
    }
    //endregion

    // region product services
    public Task<QuerySnapshot> syncAllProducts() {
        return productService.syncAllProducts().addOnSuccessListener(queryDocumentSnapshots -> {
            for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                FirebaseUser user = authService.getUser();
                Product product = queryDocumentSnapshot.toObject(Product.class);
                product.setId(queryDocumentSnapshot.getId());

                // set excluded isOwned value
                if (product.getOwnedBy() != null) {
                    if (user != null)
                        product.setOwned(product.getOwnedBy().contains(user.getEmail()));
                }

                // set excluded isInCart value
                if (product.getPutInCartBy() != null) {
                    if (user != null) {
                        product.setInCart(product.getPutInCartBy().contains(user.getEmail()));
                    }
                }

                insertProduct(product);

                // insert ownership data
                if (product.getRating() != null && product.getRating().size() > 0) {
                    for (Map.Entry<String, Float> entry : product.getRating().entrySet()) {
                        Ownership ownership = new Ownership();
                        ownership.setEmail(entry.getKey());
                        ownership.setProductId(product.getId());
                        ownership.setRating(entry.getValue());
                        insertOwnership(ownership);
                    }
                }
            }
        });
    }

    public Task<DocumentSnapshot> syncProductById(String productId) {
        return productService.syncProductById(productId)
                .addOnSuccessListener(documentSnapshot -> {
                    Product product = documentSnapshot.toObject(Product.class);
                    if (product != null) {
                        FirebaseUser user = authService.getUser();
                        product.setId(documentSnapshot.getId());

                        // set excluded isOwned value
                        if (product.getOwnedBy() != null) {
                            if (user != null)
                                product.setOwned(product.getOwnedBy().contains(user.getEmail()));
                        }

                        // set excluded isInCart value
                        if (product.getPutInCartBy() != null) {
                            if (user != null) {
                                product.setInCart(product.getPutInCartBy().contains(user.getEmail()));
                            }
                        }

                        insertProduct(product);

                        // insert ownership data
                        if (product.getRating() != null && product.getRating().size() > 0) {
                            for (Map.Entry<String, Float> entry : product.getRating().entrySet()) {
                                Ownership ownership = new Ownership();
                                ownership.setEmail(entry.getKey());
                                ownership.setProductId(product.getId());
                                ownership.setRating(entry.getValue());
                                insertOwnership(ownership);
                            }
                        }
                    }
                });
    }

    public Task<QuerySnapshot> syncCommentByProductId(String productId) {
        return productService.syncCommentByProductId(productId)
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                        Comment comment = queryDocumentSnapshot.toObject(Comment.class);
                        comment.setProductId(productId);
                        insertComment(comment);
                    }
                });
    }

    public Task<DocumentReference> addProduct(Product product) {
        return productService.addProduct(product);
    }

    public Task<Void> saveProduct(Product product) {
        return productService.saveProduct(product);
    }

    public Task<Void> saveProductThumbnailUri(String id, Uri uri) {
        return productService.saveProductThumbnailUri(id, uri);
    }

    public Task<Void> saveBookFileUri(String id, Uri uri) {
        return productService.saveBookFileUri(id, uri);
    }

    public Task<Void> saveMusicFileUri(String id, Uri uri) {
        return productService.saveMusicFileUri(id, uri);
    }

    public Task<Void> saveTrailerFileUri(String id, Uri uri) {
        return productService.saveTrailerFileUri(id, uri);
    }

    public Task<Void> saveMovieFileUri(String id, Uri uri) {
        return productService.saveMovieFileUri(id, uri);
    }

    public Task<Void> saveProductRating(String id, HashMap<String, Float> rating) {
        return productService.saveProductRating(id, rating);
    }

    private void addProductListener() {
        listenerRegistration = ProductService.getProductReference().addSnapshotListener((queryDocumentSnapshots, e) -> {
            if (queryDocumentSnapshots != null) {
                FirebaseUser user = authService.getUser();
                for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()) {
                    DocumentSnapshot documentSnapshot = dc.getDocument();
                    Product product = documentSnapshot.toObject(Product.class);
                    product.setId(documentSnapshot.getId());

                    // set excluded isOwned value
                    if (product.getOwnedBy() != null) {
                        if (user != null)
                            product.setOwned(product.getOwnedBy().contains(user.getEmail()));
                    }

                    // set excluded isInCart value
                    if (product.getPutInCartBy() != null) {
                        if (user != null) {
                            product.setInCart(product.getPutInCartBy().contains(user.getEmail()));
                        }
                    }

                    switch (dc.getType()) {
                        case ADDED: {
                            insertProduct(product);

                            // insert product ownership
                            if (product.getRating() != null && product.getRating().size() > 0) {
                                for (Map.Entry<String, Float> entry : product.getRating().entrySet()) {
                                    Ownership ownership = new Ownership();
                                    ownership.setEmail(entry.getKey());
                                    ownership.setProductId(product.getId());
                                    ownership.setRating(entry.getValue());
                                    insertOwnership(ownership);
                                }
                            }
                            break;
                        }
                        case MODIFIED: {
                            insertProduct(product);

                            // insert product ownership
                            if (product.getRating() != null && product.getRating().size() > 0) {
                                for (Map.Entry<String, Float> entry : product.getRating().entrySet()) {
                                    Ownership ownership = new Ownership();
                                    ownership.setEmail(entry.getKey());
                                    ownership.setProductId(product.getId());
                                    ownership.setRating(entry.getValue());
                                    insertOwnership(ownership);
                                }
                            }
                            break;
                        }
                        case REMOVED: {
                            deleteProduct(product);
                            // TODO: add function to deleteProfile ownership by id here
                            deleteCommentByProductId(product.getId());
                            break;
                        }
                    }
                }
            }
        });
    }
    // endregion

    // region comment services
    public Task<DocumentReference> addComment(String productId, Comment comment) {
        return productService.addComment(productId, comment);
    }

    public CollectionReference getCommentReference(String productId) {
        return productService.getCommentReference(productId);
    }
    // endregion

    private void initDagger(Application application) {
        RepositoryComponent component = DaggerRepositoryComponent.builder()
                .application(application)
                .build();
        component.inject(this);
    }
}
