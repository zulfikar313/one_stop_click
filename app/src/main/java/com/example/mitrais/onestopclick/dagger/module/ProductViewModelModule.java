package com.example.mitrais.onestopclick.dagger.module;

import android.arch.lifecycle.ViewModelProviders;

import com.example.mitrais.onestopclick.view.ProductFragment;
import com.example.mitrais.onestopclick.viewmodel.ProductViewModel;

import dagger.Module;
import dagger.Provides;

@Module
public class ProductViewModelModule {
    @Provides
    ProductViewModel provideProductViewModel(ProductFragment fragment) {
        return ViewModelProviders.of(fragment).get(ProductViewModel.class);
    }
}
