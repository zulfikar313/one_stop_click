package com.example.mitrais.onestopclick.model.repository;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.net.Uri;
import android.util.Log;

import com.example.mitrais.onestopclick.dagger.component.DaggerRepositoryComponent;
import com.example.mitrais.onestopclick.dagger.component.RepositoryComponent;
import com.example.mitrais.onestopclick.model.Product;
import com.example.mitrais.onestopclick.model.firestore.AuthService;
import com.example.mitrais.onestopclick.model.firestore.ProductService;
import com.example.mitrais.onestopclick.model.room.ProductDao;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

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

    public void deleteBoundData() {
        Completable.fromAction(() -> productDao.deleteBoundData())
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
    //endregion

    // region firestore
    public Task<QuerySnapshot> sync() {
        return productService.sync().addOnSuccessListener(queryDocumentSnapshots -> {
            for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                FirebaseUser user = authService.getUser();
                Product product = queryDocumentSnapshot.toObject(Product.class);
                product.setId(queryDocumentSnapshot.getId());
                if (product.getLikedBy() != null) {
                    if (user != null)
                        product.setLiked(product.getLikedBy().contains(user.getEmail()));
                    product.setLike(product.getLikedBy().size());
                }
                if (product.getDislikedBy() != null) {
                    if (user != null)
                        product.setDisliked(product.getDislikedBy().contains(user.getEmail()));
                    product.setDislike(product.getDislikedBy().size());
                }
                insert(product);
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

    private void addListener() {
        listenerRegistration = ProductService.getReference().addSnapshotListener((queryDocumentSnapshots, e) -> {
            if (queryDocumentSnapshots != null) {
                FirebaseUser user = authService.getUser();
                for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()) {
                    DocumentSnapshot documentSnapshot = dc.getDocument();
                    Product product = documentSnapshot.toObject(Product.class);
                    product.setId(documentSnapshot.getId());
                    if (product.getLikedBy() != null) {
                        if (user != null)
                            product.setLiked(product.getLikedBy().contains(user.getEmail()));
                        product.setLike(product.getLikedBy().size());
                    }
                    if (product.getDislikedBy() != null) {
                        if (user != null)
                            product.setDisliked(product.getDislikedBy().contains(user.getEmail()));
                        product.setDislike(product.getDislikedBy().size());
                    }
                    switch (dc.getType()) {
                        case ADDED: {
                            insert(product);
                            break;
                        }
                        case MODIFIED: {
                            insert(product);
                            break;
                        }
                        case REMOVED: {
                            delete(product);
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
