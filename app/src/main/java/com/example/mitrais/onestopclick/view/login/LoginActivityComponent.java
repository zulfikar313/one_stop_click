package com.example.mitrais.onestopclick.view.login;

import com.example.mitrais.onestopclick.dagger.module.ViewModelModule;

import dagger.BindsInstance;
import dagger.Component;

@Component(modules = ViewModelModule.class)
public interface LoginActivityComponent {
    void inject(LoginActivity loginActivity);

    @Component.Builder
    interface Builder {
        @BindsInstance
        Builder loginActivity(LoginActivity loginActivity);

        LoginActivityComponent build();
    }
}