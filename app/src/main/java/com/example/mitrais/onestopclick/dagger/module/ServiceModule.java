package com.example.mitrais.onestopclick.dagger.module;

import com.example.mitrais.onestopclick.model.firestore.AuthService;
import com.example.mitrais.onestopclick.model.firestore.ProductService;
import com.example.mitrais.onestopclick.model.firestore.ProfileService;
import com.example.mitrais.onestopclick.model.firestore.StorageService;

import dagger.Module;
import dagger.Provides;

@Module
public class ServiceModule {
    @Provides
    AuthService provideAuthService() {
        return AuthService.getInstance();
    }

    @Provides
    StorageService provideStorageService() {
        return StorageService.getInstance();
    }

    @Provides
    ProfileService provideProfileService() {
        return ProfileService.getInstance();
    }

    @Provides
    ProductService provideProductService() {
        return ProductService.getInstance();
    }
}
