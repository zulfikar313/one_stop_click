package com.example.mitrais.onestopclick.dagger.component;

import com.example.mitrais.onestopclick.dagger.module.AuthRepositoryModule;
import com.example.mitrais.onestopclick.viewmodel.RegistrationViewModel;

import dagger.Component;

@Component(modules = AuthRepositoryModule.class)
public interface RegistrationViewModelComponent {
    void inject(RegistrationViewModel registrationViewModel);
}
