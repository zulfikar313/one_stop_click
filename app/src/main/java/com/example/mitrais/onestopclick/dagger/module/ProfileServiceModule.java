package com.example.mitrais.onestopclick.dagger.module;

import com.example.mitrais.onestopclick.model.firestore.ProfileService;

import dagger.Module;
import dagger.Provides;

@Module
public class ProfileServiceModule {
    @Provides
    ProfileService provideProfileService() {
        return ProfileService.getInstance();
    }
}
