package com.example.mitrais.onestopclick.dagger.module;

import android.arch.lifecycle.ViewModelProviders;

import com.example.mitrais.onestopclick.view.ProductDetailActivity;
import com.example.mitrais.onestopclick.viewmodel.ProductDetailViewModel;

import dagger.Module;
import dagger.Provides;

@Module
public class ProductDetailViewModelModule {
    @Provides
    ProductDetailViewModel provideProductDetailViewModel(ProductDetailActivity activity) {
        return ViewModelProviders.of(activity).get(ProductDetailViewModel.class);
    }
}
