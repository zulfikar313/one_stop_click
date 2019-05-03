package com.example.mitrais.onestopclick.dagger.component;

import android.app.Application;

import com.example.mitrais.onestopclick.dagger.module.ProductRepositoryModule;
import com.example.mitrais.onestopclick.viewmodel.SearchProductViewModel;

import dagger.BindsInstance;
import dagger.Component;

@Component(modules = ProductRepositoryModule.class)
public interface SearchProductViewModelComponent {
    void inject(SearchProductViewModel viewModel);

    @Component.Builder
    interface Builder {
        @BindsInstance
        Builder application(Application application);

        SearchProductViewModelComponent build();
    }
}

