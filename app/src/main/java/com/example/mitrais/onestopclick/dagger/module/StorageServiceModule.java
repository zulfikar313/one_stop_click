package com.example.mitrais.onestopclick.dagger.module;

import com.example.mitrais.onestopclick.model.firestore.StorageService;

import dagger.Module;
import dagger.Provides;

@Module
public class StorageServiceModule {
    @Provides
    StorageService provideStorageService() {
        return StorageService.getInstance();
    }
}
