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
        // initialize dagger injection
        ProductRepositoryComponent component = DaggerProductRepositoryComponent.builder()
                .application(application)
                .build();
        component.inject(this);

        if (productListenerRegistration == null)
            setProductsListener();
    }

    // region room
    // insert local product
    public void insertLocalProduct(Product product) {
        new InsertLocalProductAsyncTask(productDao).execute(product);
    }

    // update local product
    public void updateLocalProduct(Product product) {
        new UpdateLocalProductAsyncTask(productDao).execute(product);
    }

    // delete local product
    public void deleteLocalProduct(Product product) {
        new DeleteLocalProductAsyncTask(productDao).execute(product);
    }

    // get all local products
    public LiveData<List<Product>> getAllLocalProducts() {
        return productDao.getAllProducts();
    }

    // insert local product in background
    static class InsertLocalProductAsyncTask extends AsyncTask<Product, Void, Void> {
        private ProductDao productDao;

        public InsertLocalProductAsyncTask(ProductDao productDao) {
            this.productDao = productDao;
        }

        @Override
        protected Void doInBackground(Product... products) {
            productDao.insertProduct(products[0]);
            return null;
        }
    }

    // update local product in background
    static class UpdateLocalProductAsyncTask extends AsyncTask<Product, Void, Void> {
        private ProductDao productDao;

        public UpdateLocalProductAsyncTask(ProductDao productDao) {
            this.productDao = productDao;
        }

        @Override
        protected Void doInBackground(Product... products) {
            productDao.updateProduct(products[0]);
            return null;
        }
    }

    // delete local product in background
    static class DeleteLocalProductAsyncTask extends AsyncTask<Product, Void, Void> {
        private ProductDao productDao;

        public DeleteLocalProductAsyncTask(ProductDao productDao) {
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
    public Task<QuerySnapshot> getAllProducts() {
        return productService.getAllProducts().addOnSuccessListener(queryDocumentSnapshots -> {
            for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                Product product = queryDocumentSnapshot.toObject(Product.class);
                product.setId(queryDocumentSnapshot.getId());
                insertLocalProduct(product);
            }
        });
    }

    // add products listener
    private void setProductsListener() {
        productListenerRegistration = ProductService.getProductRef().addSnapshotListener((queryDocumentSnapshots, e) -> {
            for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()) {
                DocumentSnapshot documentSnapshot = dc.getDocument();
                Product product = documentSnapshot.toObject(Product.class);
                product.setId(documentSnapshot.getId());
                switch (dc.getType()) {
                    case ADDED: {
                        insertLocalProduct(product);
                        break;
                    }
                    case MODIFIED: {
                        insertLocalProduct(product);
                        break;
                    }
                    case REMOVED: {
                        deleteLocalProduct(product);
                        break;
                    }
                }
            }
        });
    }
    // endregion

}
