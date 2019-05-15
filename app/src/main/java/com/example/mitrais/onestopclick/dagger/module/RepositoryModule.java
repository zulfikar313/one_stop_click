package com.example.mitrais.onestopclick.dagger.module;

import android.app.Application;

import com.example.mitrais.onestopclick.model.repository.AuthRepository;
import com.example.mitrais.onestopclick.model.repository.ProductRepository;
import com.example.mitrais.onestopclick.model.repository.ProfileRepository;
import com.example.mitrais.onestopclick.model.repository.StorageRepository;

import dagger.Module;
import dagger.Provides;

@Module
public class RepositoryModule {
    @Provides
    AuthRepository provideAuthRepository() {
        return new AuthRepository();
    }

    @Provides
    StorageRepository provideStorageRepository() {
        return new StorageRepository();
    }

    @Provides
    ProfileRepository provideProfileRepository(Application application) {
        return new ProfileRepository(application);
    }

    @Provides
    ProductRepository provideProductRepository(Application application) {
        return new ProductRepository(application);
    }
}
