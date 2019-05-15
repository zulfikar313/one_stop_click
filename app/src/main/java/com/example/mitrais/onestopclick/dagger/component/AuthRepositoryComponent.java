package com.example.mitrais.onestopclick.dagger.component;

import com.example.mitrais.onestopclick.dagger.module.ServiceModule;
import com.example.mitrais.onestopclick.model.repository.AuthRepository;

import dagger.Component;

@Component(modules = ServiceModule.class)
public interface AuthRepositoryComponent {
    void inject(AuthRepository authRepository);
}
