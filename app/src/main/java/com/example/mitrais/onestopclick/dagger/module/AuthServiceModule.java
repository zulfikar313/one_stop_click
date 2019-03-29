package com.example.mitrais.onestopclick.dagger.module;

import com.example.mitrais.onestopclick.model.firestore.AuthService;

import dagger.Module;
import dagger.Provides;

@Module
public class AuthServiceModule {
    @Provides
    AuthService provideAuthService() {
        return AuthService.getInstance();
    }
}
