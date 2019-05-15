package com.example.mitrais.onestopclick.dagger.component;

import com.example.mitrais.onestopclick.dagger.module.ViewModelModule;
import com.example.mitrais.onestopclick.view.SplashActivity;

import dagger.BindsInstance;
import dagger.Component;

@Component(modules = ViewModelModule.class)
public interface SplashActivityComponent {
    void inject(SplashActivity splashActivity);

    @Component.Builder
    interface Builder {
        @BindsInstance
        Builder splashActivity(SplashActivity splashActivity);

        SplashActivityComponent build();
    }
}
