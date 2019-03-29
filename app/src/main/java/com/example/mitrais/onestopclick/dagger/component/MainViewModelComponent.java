package com.example.mitrais.onestopclick.dagger.component;

import android.app.Application;

import com.example.mitrais.onestopclick.dagger.module.AuthRepositoryModule;
import com.example.mitrais.onestopclick.dagger.module.ProductRepositoryModule;
import com.example.mitrais.onestopclick.dagger.module.ProfileRepositoryModule;
import com.example.mitrais.onestopclick.viewmodel.MainViewModel;

import dagger.BindsInstance;
import dagger.Component;

@Component(modules = {AuthRepositoryModule.class, ProfileRepositoryModule.class, ProductRepositoryModule.class})
public interface MainViewModelComponent {
    void inject(MainViewModel mainViewModel);

    @Component.Builder
    interface Builder{
        @BindsInstance
        Builder application(Application application);

        MainViewModelComponent build();
    }
}
