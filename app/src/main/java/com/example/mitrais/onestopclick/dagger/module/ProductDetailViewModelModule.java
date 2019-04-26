package com.example.mitrais.onestopclick.dagger.module;

import android.arch.lifecycle.ViewModelProviders;

import com.example.mitrais.onestopclick.view.ProductActivity;
import com.example.mitrais.onestopclick.viewmodel.ProductViewModel;

import dagger.Module;
import dagger.Provides;

@Module
public class ProductDetailViewModelModule {
    @Provides
    ProductViewModel provideProductDetailViewModel(ProductActivity activity) {
        return ViewModelProviders.of(activity).get(ProductViewModel.class);
    }
}
