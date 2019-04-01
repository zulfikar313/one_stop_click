package com.example.mitrais.onestopclick.model.repository;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import com.example.mitrais.onestopclick.dagger.component.DaggerProductRepositoryComponent;
import com.example.mitrais.onestopclick.dagger.component.ProductRepositoryComponent;
import com.example.mitrais.onestopclick.model.Product;
import com.example.mitrais.onestopclick.model.firestore.ProductService;
import com.example.mitrais.onestopclick.model.room.ProductDao;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentChange;
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
            setProductsListener();
    }

    // region room
    // insert product
    private void insertProduct(Product product) {
        new InsertProductAsyncTask(productDao).execute(product);
    }

    // update product
    public void updateProduct(Product product) {
        new UpdateProductAsyncTask(productDao).execute(product);
    }

    // delete product
    public void deleteProduct(Product product) {
        new DeleteProductAsyncTask(productDao).execute(product);
    }

    // get all local products
    public LiveData<List<Product>> getAllLocalProducts() {
        return productDao.getAllProducts();
    }

    // insert local product in background
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

    // update local product in background
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

    // delete local product in background
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
    // get all products
    public Task<QuerySnapshot> getAllProducts() {
        return productService.getAllProducts().addOnSuccessListener(queryDocumentSnapshots -> {
            for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                Product product = queryDocumentSnapshot.toObject(Product.class);
                product.setId(queryDocumentSnapshot.getId());
                insertProduct(product);
            }
        });
    }

    // add product like count
    public Task<Void> addLike(String productId) {
        return productService.addLike(productId);
    }

    // add product dislike count
    public Task<Void> addDislike(String productId) {
        return productService.addDislike(productId);
    }

    // add products listener
    private void setProductsListener() {
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

    // initialize dagger injection
    private void initDagger(Application application) {
        ProductRepositoryComponent component = DaggerProductRepositoryComponent.builder()
                .application(application)
                .build();
        component.inject(this);
    }
}
