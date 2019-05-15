package com.example.mitrais.onestopclick.view.registration;

import com.example.mitrais.onestopclick.dagger.module.ViewModelModule;

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
