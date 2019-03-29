package com.example.mitrais.onestopclick.dagger.component;

import com.example.mitrais.onestopclick.dagger.module.AuthServiceModule;
import com.example.mitrais.onestopclick.model.repository.AuthRepository;

import dagger.Component;

@Component(modules = AuthServiceModule.class)
public interface AuthRepositoryComponent {
    void inject(AuthRepository authRepository);
}
