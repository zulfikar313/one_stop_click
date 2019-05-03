package com.example.mitrais.onestopclick.dagger.component;

import android.app.Application;

import com.example.mitrais.onestopclick.dagger.module.ProductRepositoryModule;
import com.example.mitrais.onestopclick.viewmodel.ProductListViewModel;

import dagger.BindsInstance;
import dagger.Component;

@Component(modules = ProductRepositoryModule.class)
public interface ProductListViewModelComponent {
    void inject(ProductListViewModel productListViewModel);

    @Component.Builder
    interface Builder {
        @BindsInstance
        Builder application(Application application);

        ProductListViewModelComponent build();
    }
}
