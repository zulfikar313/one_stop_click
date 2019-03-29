package com.example.mitrais.onestopclick.dagger.module;

import android.app.Application;

import com.example.mitrais.onestopclick.model.room.MainDatabase;
import com.example.mitrais.onestopclick.model.room.ProductDao;

import dagger.Module;
import dagger.Provides;

@Module
public class ProductDaoModule {
    @Provides
    ProductDao provideProductDao(Application application) {
        MainDatabase db = MainDatabase.getInstance(application);
        return db.productDao();
    }
}
