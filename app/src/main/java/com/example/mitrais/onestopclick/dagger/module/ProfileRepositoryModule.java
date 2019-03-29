package com.example.mitrais.onestopclick.dagger.module;

import android.app.Application;

import com.example.mitrais.onestopclick.model.repository.ProfileRepository;

import dagger.Module;
import dagger.Provides;

@Module
public class ProfileRepositoryModule {
    @Provides
    ProfileRepository provideProfileRepository(Application application) {
        return new ProfileRepository(application);
    }
}
