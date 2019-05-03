package com.example.mitrais.onestopclick.dagger.module;

import android.arch.lifecycle.ViewModelProviders;

import com.example.mitrais.onestopclick.view.ProductListFragment;
import com.example.mitrais.onestopclick.viewmodel.ProductListViewModel;

import dagger.Module;
import dagger.Provides;

@Module
public class ProductListViewModelModule {
    @Provides
    ProductListViewModel provideProductViewModel(ProductListFragment fragment) {
        return ViewModelProviders.of(fragment).get(ProductListViewModel.class);
    }
}
