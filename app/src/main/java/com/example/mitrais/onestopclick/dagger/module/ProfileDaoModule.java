package com.example.mitrais.onestopclick.dagger.module;

import android.app.Application;

import com.example.mitrais.onestopclick.model.room.MainDatabase;
import com.example.mitrais.onestopclick.model.room.ProfileDao;

import dagger.Module;
import dagger.Provides;

@Module
public class ProfileDaoModule {
    @Provides
    ProfileDao provideProfileDao(Application application) {
        MainDatabase db = MainDatabase.getInstance(application);
        return db.profileDao();
    }
}
