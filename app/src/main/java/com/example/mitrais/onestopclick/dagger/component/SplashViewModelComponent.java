package com.example.mitrais.onestopclick.dagger.component;

import android.app.Application;

import com.example.mitrais.onestopclick.dagger.module.RepositoryModule;
import com.example.mitrais.onestopclick.viewmodel.SplashViewModel;

import dagger.BindsInstance;
import dagger.Component;

@Component(modules = RepositoryModule.class)
public interface SplashViewModelComponent {
    void inject(SplashViewModel splashViewModel);

    @Component.Builder
    interface Builder {
        @BindsInstance
        Builder application(Application application);

        SplashViewModelComponent build();
    }
}
