package com.example.mitrais.onestopclick.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.support.annotation.NonNull;

import com.example.mitrais.onestopclick.model.Product;
import com.example.mitrais.onestopclick.model.repository.ProductRepository;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;

public class AddProductViewModel extends AndroidViewModel {
    private ProductRepository productRepository;

    public AddProductViewModel(@NonNull Application application) {
        super(application);
        productRepository = new ProductRepository(application);
    }

    public Task<DocumentReference> addProduct(Product product) {
        return productRepository.addProduct(product);
    }
}
