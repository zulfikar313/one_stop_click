package com.example.mitrais.onestopclick.dagger.component;

import com.example.mitrais.onestopclick.dagger.module.ViewModelModule;
import com.example.mitrais.onestopclick.view.ProductDetailActivity;

import dagger.BindsInstance;
import dagger.Component;

@Component(modules = ViewModelModule.class)
public interface ProductDetailActivityComponent {
    void inject(ProductDetailActivity activity);

    @Component.Builder
    interface Builder {
        @BindsInstance
        Builder productDetailActivity(ProductDetailActivity activity);

        ProductDetailActivityComponent build();
    }
}
