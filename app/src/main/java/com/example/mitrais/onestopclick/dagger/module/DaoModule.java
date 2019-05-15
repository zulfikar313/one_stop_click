package com.example.mitrais.onestopclick.dagger.module;

import android.app.Application;

import com.example.mitrais.onestopclick.model.room.MainDatabase;
import com.example.mitrais.onestopclick.model.room.ProductDao;
import com.example.mitrais.onestopclick.model.room.ProfileDao;

import dagger.Module;
import dagger.Provides;

@Module
public class DaoModule {
    @Provides
    ProductDao provideProductDao(Application application) {
        MainDatabase db = MainDatabase.getInstance(application);
        return db.productDao();
    }

    @Provides
    ProfileDao provideProfileDao(Application application) {
        MainDatabase db = MainDatabase.getInstance(application);
        return db.profileDao();
    }
}
