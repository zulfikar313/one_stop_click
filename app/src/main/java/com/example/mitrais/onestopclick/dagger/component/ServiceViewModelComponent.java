package com.example.mitrais.onestopclick.dagger.component;

import com.example.mitrais.onestopclick.dagger.module.RepositoryModule;
import com.example.mitrais.onestopclick.viewmodel.ForgotPasswordViewModel;
import com.example.mitrais.onestopclick.viewmodel.RegistrationViewModel;

import dagger.Component;

@Component(modules = RepositoryModule.class)
public interface ServiceViewModelComponent {
    void inject(RegistrationViewModel registrationViewModel);

    void inject(ForgotPasswordViewModel forgotPasswordViewModel);
}
