package com.example.mitrais.onestopclick.dagger.module;

import com.example.mitrais.onestopclick.model.repository.StorageRepository;

import dagger.Module;
import dagger.Provides;

@Module
public class StorageRepositoryModule {
    @Provides
    StorageRepository provideStorageRepository() {
        return new StorageRepository();
    }
}
