package com.example.mitrais.onestopclick.model.repository;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.net.Uri;
import android.os.AsyncTask;

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

public class ProductRepository {
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

    /**
     * @param product product object
     */
    private void insertProduct(Product product) {
        new InsertProductAsyncTask(productDao).execute(product);
    }

    /**
     * @param product product object
     */
    public void updateProduct(Product product) {
        new UpdateProductAsyncTask(productDao).execute(product);
    }

    /**
     * @param product product object
     */
    public void deleteProduct(Product product) {
        new DeleteProductAsyncTask(productDao).execute(product);
    }

    /**
     * @return products live data
     */
    public LiveData<List<Product>> getAllProducts() {
        return productDao.getAllProducts();
    }

    /**
     * @param search title, author, artist or director
     * @return product search results live data
     */
    public LiveData<List<Product>> searchProducts(String search) {
        return productDao.searchProducts("%" + search + "%");
    }

    /**
     * @param type   product type
     * @param search title, author, artist or director
     * @return product search results live data
     */
    public LiveData<List<Product>> searchProductsByType(String type, String search) {
        return productDao.searchProductsByType(type, "%" + search + "%");
    }

    /**
     * @return filtered products live data
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

    /**
     * insert product to local db in background
     */
    static class InsertProductAsyncTask extends AsyncTask<Product, Void, Void> {
        private ProductDao productDao;

        InsertProductAsyncTask(ProductDao productDao) {
            this.productDao = productDao;
        }

        @Override
        protected Void doInBackground(Product... products) {
            productDao.insertProduct(products[0]);
            return null;
        }
    }

    /**
     * update product in local db in background
     */
    static class UpdateProductAsyncTask extends AsyncTask<Product, Void, Void> {
        private ProductDao productDao;

        UpdateProductAsyncTask(ProductDao productDao) {
            this.productDao = productDao;
        }

        @Override
        protected Void doInBackground(Product... products) {
            productDao.updateProduct(products[0]);
            return null;
        }
    }

    /**
     * delete product in local db in background
     */
    static class DeleteProductAsyncTask extends AsyncTask<Product, Void, Void> {
        private ProductDao productDao;

        DeleteProductAsyncTask(ProductDao productDao) {
            this.productDao = productDao;
        }

        @Override
        protected Void doInBackground(Product... products) {
            productDao.deleteProduct(products[0]);
            return null;
        }
    }
    //endregion

    // region firestore

    /**
     * @return sync products task
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
     * @return add like task
     */
    public Task<Void> addLike(String id) {
        return productService.addLike(id);
    }

    /**
     * @param id product id
     * @return add dislike task
     */
    public Task<Void> addDislike(String id) {
        return productService.addDislike(id);
    }

    /**
     * @param product product object
     * @return save product task
     */
    public Task<Void> saveProduct(Product product) {
        return productService.saveProduct(product);
    }

    /**
     * @param productId product id
     * @param uri       trailer uri
     * @return save product task
     */
    public Task<Void> saveProductTrailerUri(String productId, Uri uri) {
        return productService.saveProductTrailerUri(productId, uri);
    }

    /**
     * @param productId product id
     * @param uri       music uri
     * @return save product task
     */
    public Task<Void> saveProductMusicUri(String productId, Uri uri) {
        return productService.saveProductMusicUri(productId, uri);
    }

    /**
     * @param product product object
     * @return add product details task
     */
    public Task<DocumentReference> addProduct(Product product) {
        return productService.addProduct(product);
    }

    /**
     * add listener to product data
     * changes in product data will be reflected in local database
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
     * @param application application to inject ProductDao
     */
    private void initDagger(Application application) {
        ProductRepositoryComponent component = DaggerProductRepositoryComponent.builder()
                .application(application)
                .build();
        component.inject(this);
    }
}
