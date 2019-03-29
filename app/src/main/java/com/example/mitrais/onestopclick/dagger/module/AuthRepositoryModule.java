package com.example.mitrais.onestopclick.dagger.module;

import com.example.mitrais.onestopclick.model.repository.AuthRepository;

import dagger.Module;
import dagger.Provides;

@Module
public class AuthRepositoryModule {
    @Provides
    AuthRepository provideAuthRepository() {
        return new AuthRepository();
    }
}
