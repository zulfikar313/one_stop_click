package com.example.mitrais.onestopclick.dagger.component;

import android.app.Application;

import com.example.mitrais.onestopclick.dagger.module.RepositoryModule;
import com.example.mitrais.onestopclick.viewmodel.LoginViewModel;

import dagger.BindsInstance;
import dagger.Component;

@Component(modules = RepositoryModule.class)
public interface LoginViewModelComponent {
    void inject(LoginViewModel loginViewModel);

    @Component.Builder
    interface Builder{
        @BindsInstance
        Builder application(Application application);

        LoginViewModelComponent build();
    }
}
