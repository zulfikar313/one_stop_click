package com.example.mitrais.onestopclick.view.product_detail;

import com.example.mitrais.onestopclick.dagger.module.ViewModelModule;

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
