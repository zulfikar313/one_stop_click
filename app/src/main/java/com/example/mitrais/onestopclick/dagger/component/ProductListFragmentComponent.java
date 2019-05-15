package com.example.mitrais.onestopclick.dagger.component;

import com.example.mitrais.onestopclick.dagger.module.ProductAdapterModule;
import com.example.mitrais.onestopclick.dagger.module.ViewModelModule;
import com.example.mitrais.onestopclick.view.ProductListFragment;

import dagger.BindsInstance;
import dagger.Component;

@Component(modules = {ViewModelModule.class, ProductAdapterModule.class})
public interface ProductListFragmentComponent {
    void inject(ProductListFragment fragment);

    @Component.Builder
    interface Builder {
        @BindsInstance
        Builder productFragment(ProductListFragment fragment);

        ProductListFragmentComponent build();
    }
}
