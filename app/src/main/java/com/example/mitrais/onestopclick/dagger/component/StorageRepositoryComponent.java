package com.example.mitrais.onestopclick.dagger.component;

import com.example.mitrais.onestopclick.dagger.module.StorageServiceModule;
import com.example.mitrais.onestopclick.model.repository.StorageRepository;

import dagger.Component;

@Component(modules = StorageServiceModule.class)
public interface StorageRepositoryComponent {
    void inject(StorageRepository storageRepository);
}
