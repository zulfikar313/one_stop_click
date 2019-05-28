package com.example.mitrais.onestopclick.view.add_product;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.support.annotation.NonNull;

import com.example.mitrais.onestopclick.dagger.component.DaggerViewModelComponent;
import com.example.mitrais.onestopclick.dagger.component.ViewModelComponent;
import com.example.mitrais.onestopclick.model.Product;
import com.example.mitrais.onestopclick.model.repository.ProductRepository;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;

import javax.inject.Inject;

public class AddProductViewModel extends AndroidViewModel {
    @Inject
    ProductRepository productRepo;

    public AddProductViewModel(@NonNull Application application) {
        super(application);
        initDagger(application);
    }

    Task<DocumentReference> addProduct(Product product) {
        return productRepo.add(product);
    }

    private void initDagger(Application application) {
        ViewModelComponent component = DaggerViewModelComponent.builder()
                .application(application)
                .build();
        component.inject(this);
    }
}
