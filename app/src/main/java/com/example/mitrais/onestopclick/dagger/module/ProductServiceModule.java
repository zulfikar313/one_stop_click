package com.example.mitrais.onestopclick.dagger.module;

import com.example.mitrais.onestopclick.model.firestore.ProductService;

import dagger.Module;
import dagger.Provides;

@Module
public class ProductServiceModule {
    @Provides
    ProductService provideProductService() {
        return ProductService.getInstance();
    }
}
