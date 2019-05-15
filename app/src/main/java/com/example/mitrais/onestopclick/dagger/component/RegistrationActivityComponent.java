package com.example.mitrais.onestopclick.dagger.component;

import com.example.mitrais.onestopclick.dagger.module.ViewModelModule;
import com.example.mitrais.onestopclick.view.RegistrationActivity;

import dagger.BindsInstance;
import dagger.Component;

@Component(modules = ViewModelModule.class)
public interface RegistrationActivityComponent {
    void inject(RegistrationActivity registrationActivity);

    @Component.Builder
    interface Builder{
        @BindsInstance
        Builder registrationActivity(RegistrationActivity registrationActivity);

        RegistrationActivityComponent build();
    }
}
