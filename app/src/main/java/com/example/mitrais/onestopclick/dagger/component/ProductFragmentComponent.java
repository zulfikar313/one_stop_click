package com.example.mitrais.onestopclick.dagger.component;

import com.example.mitrais.onestopclick.dagger.module.ProductAdapterModule;
import com.example.mitrais.onestopclick.dagger.module.ProductViewModelModule;
import com.example.mitrais.onestopclick.view.ProductFragment;
import com.example.mitrais.onestopclick.view.adapter.ProductAdapter;

import dagger.BindsInstance;
import dagger.Component;

@Component(modules = {ProductViewModelModule.class, ProductAdapterModule.class})
public interface ProductFragmentComponent {
    void inject(ProductFragment fragment);

    @Component.Builder
    interface Builder {
        @BindsInstance
        Builder productFragment(ProductFragment fragment);

        ProductFragmentComponent build();
    }
}
