package com.example.mitrais.onestopclick.view.library;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.example.mitrais.onestopclick.dagger.component.DaggerViewModelComponent;
import com.example.mitrais.onestopclick.dagger.component.ViewModelComponent;
import com.example.mitrais.onestopclick.model.Product;
import com.example.mitrais.onestopclick.model.repository.ProductRepository;
import java.util.List;

import javax.inject.Inject;

public class LibraryViewModel extends AndroidViewModel {
    @Inject
    ProductRepository productRepo;

    public LibraryViewModel(@NonNull Application application) {
        super(application);
        initDagger(application);
        products = productRepo.getOwnedProducts();
    }

    LiveData<List<Product>> products;

    LiveData<List<Product>> getOwnedProducts() {
        return products;
    }

    private void initDagger(Application application) {
        ViewModelComponent component = DaggerViewModelComponent.builder()
                .application(application)
                .build();
        component.inject(this);
    }
}
