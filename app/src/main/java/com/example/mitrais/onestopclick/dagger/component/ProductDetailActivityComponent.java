package com.example.mitrais.onestopclick.dagger.component;

import com.example.mitrais.onestopclick.dagger.module.ProductDetailViewModelModule;
import com.example.mitrais.onestopclick.view.ProductDetailActivity;

import dagger.BindsInstance;
import dagger.Component;

@Component(modules = ProductDetailViewModelModule.class)
public interface ProductDetailActivityComponent {
    void inject(ProductDetailActivity activity);

    @Component.Builder
    interface Builder {
        @BindsInstance
        Builder productDetailActivity(ProductDetailActivity activity);

        ProductDetailActivityComponent build();
    }
}
