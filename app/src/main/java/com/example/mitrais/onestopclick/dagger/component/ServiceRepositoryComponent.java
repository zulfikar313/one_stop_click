package com.example.mitrais.onestopclick.dagger.component;

import com.example.mitrais.onestopclick.dagger.module.ServiceModule;
import com.example.mitrais.onestopclick.model.repository.AuthRepository;
import com.example.mitrais.onestopclick.model.repository.StorageRepository;

import dagger.Component;

@Component(modules = ServiceModule.class)
public interface ServiceRepositoryComponent {
    void inject(AuthRepository authRepository);

    void inject(StorageRepository storageRepository);
}
