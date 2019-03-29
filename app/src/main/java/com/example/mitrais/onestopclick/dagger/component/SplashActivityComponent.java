package com.example.mitrais.onestopclick.dagger.component;

import com.example.mitrais.onestopclick.dagger.module.SplashViewModelModule;
import com.example.mitrais.onestopclick.view.SplashActivity;

import dagger.BindsInstance;
import dagger.Component;

@Component(modules = SplashViewModelModule.class)
public interface SplashActivityComponent {
    void inject(SplashActivity splashActivity);

    @Component.Builder
    interface Builder {
        @BindsInstance
        Builder splashActivity(SplashActivity splashActivity);

        SplashActivityComponent build();
    }
}
