package com.example.mitrais.onestopclick.dagger.component;

import android.app.Application;

import com.example.mitrais.onestopclick.dagger.module.AuthRepositoryModule;
import com.example.mitrais.onestopclick.dagger.module.ProfileRepositoryModule;
import com.example.mitrais.onestopclick.model.repository.ProfileRepository;
import com.example.mitrais.onestopclick.viewmodel.SplashViewModel;

import dagger.BindsInstance;
import dagger.Component;

@Component(modules = {AuthRepositoryModule.class, ProfileRepositoryModule.class})
public interface SplashViewModelComponent {
    void inject(SplashViewModel splashViewModel);

    @Component.Builder
    interface Builder {
        @BindsInstance
        Builder application(Application application);

        SplashViewModelComponent build();
    }
}
