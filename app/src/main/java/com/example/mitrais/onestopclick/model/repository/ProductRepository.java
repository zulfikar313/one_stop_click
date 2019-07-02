package com.example.mitrais.onestopclick.model.repository;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.net.Uri;
import android.util.Log;

import com.example.mitrais.onestopclick.dagger.component.DaggerRepositoryComponent;
import com.example.mitrais.onestopclick.dagger.component.RepositoryComponent;
import com.example.mitrais.onestopclick.model.Comment;
import com.example.mitrais.onestopclick.model.Product;
import com.example.mitrais.onestopclick.model.firestore.AuthService;
import com.example.mitrais.onestopclick.model.firestore.ProductService;
import com.example.mitrais.onestopclick.model.room.CommentDao;
import com.example.mitrais.onestopclick.model.room.ProductDao;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.List;

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
    CommentDao commentDao;

    @Inject
    ProductService productService;

    @Inject
    AuthService authService;

    public ProductRepository(Application application) {
        initDagger(application);
        addListener();
    }

    // region room
    private void insert(Product product) {
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

    private void delete(Product product) {
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

    public LiveData<List<Product>> getAll() {
        return productDao.getAll();
    }

    public LiveData<Product> getById(String id) {
        return productDao.getById(id);
    }

    public LiveData<List<Product>> getByType(String type) {
        return productDao.getByType(type);
    }

    public LiveData<List<Product>> getByGenre(String genre) {
        return productDao.getByGenre(genre);
    }

    public LiveData<List<Product>> getByTypeAndGenre(String type, String genre) {
        return productDao.getByTypeAndGenre(type, genre);
    }

    public LiveData<List<Product>> search(String search) {
        return productDao.search("%" + search + "%");
    }

    public LiveData<List<Product>> getInCart() {
        return productDao.getInCart();
    }

    public LiveData<List<Product>> getOwned() {
        return productDao.getOwned();
    }

    private void insertComment(Comment comment) {
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

    private void deleteComment(Comment comment) {
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

    // region firestore
    public Task<QuerySnapshot> sync() {
        return productService.sync().addOnSuccessListener(queryDocumentSnapshots -> {
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

                insert(product);

                if (product.getComments() != null && product.getComments().size() > 0) {
                    for (Comment comment : product.getComments()) {
                        comment.setProductId(product.getId());
                        insertComment(comment);
                    }
                }
            }
        });
    }

    public Task<DocumentSnapshot> sync(String productId) {
        return productService.sync(productId)
                .addOnSuccessListener(documentSnapshot -> {
                    FirebaseUser user = authService.getUser();
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

                    insert(product);

                    if (product.getComments() != null && product.getComments().size() > 0) {
                        for (Comment comment : product.getComments()) {
                            comment.setProductId(product.getId());
                            insertComment(comment);
                        }
                    }
                });
    }

    public Task<QuerySnapshot> syncComments(String productId) {
        return productService.syncComments(productId)
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                        Comment comment = queryDocumentSnapshot.toObject(Comment.class);
                        comment.setId(queryDocumentSnapshot.getId());
                        comment.setProductId(productId);
                        insertComment(comment);
                    }
                });
    }

    public Task<DocumentReference> add(Product product) {
        return productService.add(product);
    }

    public Task<Void> save(Product product) {
        return productService.save(product);
    }

    public Task<Void> saveThumbnailUri(String id, Uri uri) {
        return productService.saveThumbnailUri(id, uri);
    }

    public Task<Void> saveBookUri(String id, Uri uri) {
        return productService.saveBookUri(id, uri);
    }

    public Task<Void> saveMusicUri(String id, Uri uri) {
        return productService.saveMusicUri(id, uri);
    }

    public Task<Void> saveTrailerUri(String id, Uri uri) {
        return productService.saveTrailerUri(id, uri);
    }

    public Task<Void> saveMovieUri(String id, Uri uri) {
        return productService.saveMovieUri(id, uri);
    }

    public Task<Void> saveRating(String id, HashMap<String, Float> rating) {
        return productService.saveRating(id, rating);
    }

    public Task<DocumentReference> addComment(String productId, Comment comment) {
        return productService.addComment(productId, comment);
    }

    public Task<Void> saveComment(String productId, Comment comment) {
        return productService.saveComment(productId, comment);
    }

    private void addListener() {
        listenerRegistration = ProductService.getReference().addSnapshotListener((queryDocumentSnapshots, e) -> {
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
                            insert(product);

                            if (product.getComments() != null && product.getComments().size() > 0) {
                                for (Comment comment : product.getComments()) {
                                    comment.setProductId(product.getId());
                                    insertComment(comment);
                                }
                            }
                            break;
                        }
                        case MODIFIED: {
                            insert(product);

                            if (product.getComments() != null && product.getComments().size() > 0) {
                                for (Comment comment : product.getComments()) {
                                    comment.setProductId(product.getId());
                                    insertComment(comment);
                                }
                            }
                            break;
                        }
                        case REMOVED: {
                            delete(product);

                            deleteCommentByProductId(product.getId());
                            break;
                        }
                    }
                }
            }
        });
    }
    // endregion

    private void initDagger(Application application) {
        RepositoryComponent component = DaggerRepositoryComponent.builder()
                .application(application)
                .build();
        component.inject(this);
    }
}
