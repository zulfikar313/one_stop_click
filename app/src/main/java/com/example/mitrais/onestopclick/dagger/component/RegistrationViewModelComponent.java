package com.example.mitrais.onestopclick.dagger.component;

import com.example.mitrais.onestopclick.dagger.module.RepositoryModule;
import com.example.mitrais.onestopclick.viewmodel.RegistrationViewModel;

import dagger.Component;

@Component(modules = RepositoryModule.class)
public interface RegistrationViewModelComponent {
    void inject(RegistrationViewModel registrationViewModel);
}
