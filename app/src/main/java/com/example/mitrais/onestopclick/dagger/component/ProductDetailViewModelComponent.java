package com.example.mitrais.onestopclick.dagger.component;

import android.app.Application;

import com.example.mitrais.onestopclick.dagger.module.RepositoryModule;
import com.example.mitrais.onestopclick.viewmodel.ProductDetailViewModel;

import dagger.BindsInstance;
import dagger.Component;

@Component(modules = RepositoryModule.class)
public interface ProductDetailViewModelComponent {
    void inject(ProductDetailViewModel viewModel);

    @Component.Builder
    interface Builder {
        @BindsInstance
        Builder application(Application application);

        ProductDetailViewModelComponent build();
    }
}
