package com.example.mitrais.onestopclick.dagger.component;

import com.example.mitrais.onestopclick.dagger.module.ProductAdapterModule;
import com.example.mitrais.onestopclick.dagger.module.ProductViewModelModule;
import com.example.mitrais.onestopclick.view.ProductListFragment;

import dagger.BindsInstance;
import dagger.Component;

@Component(modules = {ProductViewModelModule.class, ProductAdapterModule.class})
public interface ProductFragmentComponent {
    void inject(ProductListFragment fragment);

    @Component.Builder
    interface Builder {
        @BindsInstance
        Builder productFragment(ProductListFragment fragment);

        ProductFragmentComponent build();
    }
}
