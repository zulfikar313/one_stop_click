package com.example.mitrais.onestopclick.dagger.component;

import android.app.Application;

import com.example.mitrais.onestopclick.dagger.module.ProductDaoModule;
import com.example.mitrais.onestopclick.dagger.module.ProductServiceModule;
import com.example.mitrais.onestopclick.model.firestore.ProductService;
import com.example.mitrais.onestopclick.model.repository.ProductRepository;

import dagger.BindsInstance;
import dagger.Component;

@Component(modules = {ProductDaoModule.class, ProductServiceModule.class})
public interface ProductRepositoryComponent {
    void inject(ProductRepository productRepository);

    @Component.Builder
    interface Builder {
        @BindsInstance
        Builder application(Application application);

        ProductRepositoryComponent build();
    }
}
