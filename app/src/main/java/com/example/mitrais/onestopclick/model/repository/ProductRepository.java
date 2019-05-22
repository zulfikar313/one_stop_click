package com.example.mitrais.onestopclick.model.repository;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.net.Uri;
import android.util.Log;

import com.example.mitrais.onestopclick.dagger.component.DaggerRepositoryComponent;
import com.example.mitrais.onestopclick.dagger.component.RepositoryComponent;
import com.example.mitrais.onestopclick.model.Product;
import com.example.mitrais.onestopclick.model.firestore.ProductService;
import com.example.mitrais.onestopclick.model.room.ProductDao;
import com.google.android.gms.tasks.Task;
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
    private static ListenerRegistration productListenerRegistration;

    @Inject
    ProductDao productDao;

    @Inject
    ProductService productService;

    public ProductRepository(Application application) {
        initDagger(application);
        if (productListenerRegistration == null)
            setProductListener();
    }

    // region room
    private void insertProduct(Product product) {
        Completable.fromAction(() -> productDao.insertProduct(product))
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
        Completable.fromAction(() -> productDao.deleteProduct(product))
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

    /**
     * @return product list live data
     */
    public LiveData<List<Product>> getAllProducts() {
        return productDao.getAllProducts();
    }

    /**
     * @return product list live data
     */
    public LiveData<List<Product>> getProductsByGenre(String genre) {
        return productDao.getProductsByGenre(genre);
    }

    /**
     * @return product list live data
     */
    public LiveData<List<Product>> getProductsByTypeAndGenre(String type, String genre) {
        return productDao.getProductsByTypeAndGenre(type, genre);
    }

    /**
     * @param search search query
     * @return product list live data
     */
    public LiveData<List<Product>> searchProducts(String search) {
        return productDao.searchProducts("%" + search + "%");
    }

    /**
     * @param type product type
     * @return product list live data
     */
    public LiveData<List<Product>> getProductsByType(String type) {
        return productDao.getProductsByType(type);
    }

    /**
     * @param id product id
     * @return product live data
     */
    public LiveData<Product> getProductById(String id) {
        return productDao.getProductById(id);
    }
    //endregion

    // region firestore

    /**
     * sync local product data
     *
     * @return task
     */
    public Task<QuerySnapshot> syncProducts() {
        return productService.syncProducts().addOnSuccessListener(queryDocumentSnapshots -> {
            for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                Product product = queryDocumentSnapshot.toObject(Product.class);
                product.setId(queryDocumentSnapshot.getId());
                insertProduct(product);
            }
        });
    }

    /**
     * @param id product id
     * @return task
     */
    public Task<Void> addLike(String id) {
        return productService.addLike(id);
    }

    /**
     * @param id product id
     * @return task
     */
    public Task<Void> addDislike(String id) {
        return productService.addDislike(id);
    }

    /**
     * @param product product object
     * @return task
     */
    public Task<Void> saveProduct(Product product) {
        return productService.saveProduct(product);
    }

    /**
     * @param id  product id
     * @param uri thumbnail uri
     * @return task
     */
    public Task<Void> saveThumbnailUri(String id, Uri uri) {
        return productService.saveProductThumbnailUri(id, uri);
    }

    /**
     * @param id  product id
     * @param uri book uri
     * @return task
     */
    public Task<Void> saveBookUri(String id, Uri uri) {
        return productService.saveBookUri(id, uri);
    }

    /**
     * @param id  product id
     * @param uri music uri
     * @return task
     */
    public Task<Void> saveMusicUri(String id, Uri uri) {
        return productService.saveMusicUri(id, uri);
    }

    /**
     * @param id  product id
     * @param uri trailer uri
     * @return task
     */
    public Task<Void> saveProductTrailerUri(String id, Uri uri) {
        return productService.saveProductTrailerUri(id, uri);
    }

    /**
     * @param product product object
     * @return task
     */
    public Task<DocumentReference> addProduct(Product product) {
        return productService.addProduct(product);
    }

    /**
     * listen to product changes
     */
    private void setProductListener() {
        productListenerRegistration = ProductService.getProductRef().addSnapshotListener((queryDocumentSnapshots, e) -> {
            if (queryDocumentSnapshots != null) {
                for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()) {
                    DocumentSnapshot documentSnapshot = dc.getDocument();
                    Product product = documentSnapshot.toObject(Product.class);
                    product.setId(documentSnapshot.getId());
                    switch (dc.getType()) {
                        case ADDED: {
                            insertProduct(product);
                            break;
                        }
                        case MODIFIED: {
                            insertProduct(product);
                            break;
                        }
                        case REMOVED: {
                            deleteProduct(product);
                            break;
                        }
                    }
                }
            }
        });
    }
    // endregion

    /**
     * initialize dagger injection
     *
     * @param application for dao injection
     */
    private void initDagger(Application application) {
        RepositoryComponent component = DaggerRepositoryComponent.builder()
                .application(application)
                .build();
        component.inject(this);
    }
}
