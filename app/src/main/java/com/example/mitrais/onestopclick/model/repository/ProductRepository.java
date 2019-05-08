package com.example.mitrais.onestopclick.model.repository;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.net.Uri;
import android.util.Log;

import com.example.mitrais.onestopclick.dagger.component.DaggerProductRepositoryComponent;
import com.example.mitrais.onestopclick.dagger.component.ProductRepositoryComponent;
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
                        Log.i(TAG, "Insert product complete");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, e.getMessage());
                    }
                });
    }

    public void updateProduct(Product product) {
        Completable.fromAction(() -> productDao.updateProduct(product))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onComplete() {
                        Log.i(TAG, "Update product complete");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, e.getMessage());
                    }
                });
    }

    public void deleteProduct(Product product) {
        Completable.fromAction(() -> productDao.deleteProduct(product))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onComplete() {
                        Log.i(TAG, "Delete product complete");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, e.getMessage());
                    }
                });
    }

    public LiveData<List<Product>> getAllProducts() {
        return productDao.getAllProducts();
    }

    public LiveData<List<Product>> searchProducts(String search) {
        return productDao.searchProducts("%" + search + "%");
    }

    public LiveData<List<Product>> searchProductsByType(String type, String search) {
        return productDao.searchProductsByType(type, "%" + search + "%");
    }

    public LiveData<List<Product>> getProductsByType(String type) {
        return productDao.getProductsByType(type);
    }

    public LiveData<Product> getProductById(String id) {
        return productDao.getProductById(id);
    }
    //endregion

    // region firestore
    public Task<QuerySnapshot> syncProducts() {
        return productService.syncProducts().addOnSuccessListener(queryDocumentSnapshots -> {
            for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                Product product = queryDocumentSnapshot.toObject(Product.class);
                product.setId(queryDocumentSnapshot.getId());
                insertProduct(product);
            }
        });
    }

    public Task<Void> addLike(String id) {
        return productService.addLike(id);
    }

    public Task<Void> addDislike(String id) {
        return productService.addDislike(id);
    }

    public Task<Void> saveProduct(Product product) {
        return productService.saveProduct(product);
    }

    public Task<Void> saveThumbnailUri(String productId, Uri uri) {
        return productService.saveProductThumbnailUri(productId, uri);
    }

    public Task<Void> saveProductTrailerUri(String productId, Uri uri) {
        return productService.saveProductTrailerUri(productId, uri);
    }

    public Task<Void> saveProductMusicUri(String productId, Uri uri) {
        return productService.saveProductMusicUri(productId, uri);
    }

    public Task<DocumentReference> addProduct(Product product) {
        return productService.addProduct(product);
    }

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

    private void initDagger(Application application) {
        ProductRepositoryComponent component = DaggerProductRepositoryComponent.builder()
                .application(application)
                .build();
        component.inject(this);
    }
}
