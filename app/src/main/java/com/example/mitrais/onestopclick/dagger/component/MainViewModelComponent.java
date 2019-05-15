package com.example.mitrais.onestopclick.dagger.component;

import android.app.Application;

import com.example.mitrais.onestopclick.dagger.module.RepositoryModule;
import com.example.mitrais.onestopclick.viewmodel.MainViewModel;

import dagger.BindsInstance;
import dagger.Component;

@Component(modules = RepositoryModule.class)
public interface MainViewModelComponent {
    void inject(MainViewModel mainViewModel);

    @Component.Builder
    interface Builder{
        @BindsInstance
        Builder application(Application application);

        MainViewModelComponent build();
    }
}
