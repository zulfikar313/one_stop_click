package com.example.mitrais.onestopclick.dagger.module;

import com.example.mitrais.onestopclick.adapter.ProductAdapter;

import dagger.Module;
import dagger.Provides;

@Module
public class ProductAdapterModule {
    @Provides
    ProductAdapter provideProductAdapter() {
        return new ProductAdapter();
    }
}
