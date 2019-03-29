package com.example.mitrais.onestopclick.dagger.module;

import android.app.Application;

import com.example.mitrais.onestopclick.model.repository.ProductRepository;

import dagger.Module;
import dagger.Provides;

@Module
public class ProductRepositoryModule {
    @Provides
    ProductRepository provideProductRepository(Application application) {
        return new ProductRepository(application);
    }
}
