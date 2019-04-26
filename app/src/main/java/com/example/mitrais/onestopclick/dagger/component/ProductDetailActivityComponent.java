package com.example.mitrais.onestopclick.dagger.component;

import com.example.mitrais.onestopclick.dagger.module.ProductDetailViewModelModule;
import com.example.mitrais.onestopclick.view.ProductActivity;

import dagger.BindsInstance;
import dagger.Component;

@Component(modules = ProductDetailViewModelModule.class)
public interface ProductDetailActivityComponent {
    void inject(ProductActivity activity);

    @Component.Builder
    interface Builder {
        @BindsInstance
        Builder productDetailActivity(ProductActivity activity);

        ProductDetailActivityComponent build();
    }
}
