package com.example.mitrais.onestopclick.dagger.component;

import com.example.mitrais.onestopclick.dagger.module.AuthRepositoryModule;
import com.example.mitrais.onestopclick.viewmodel.ForgotPasswordViewModel;

import dagger.Component;

@Component(modules = AuthRepositoryModule.class)
public interface ForgotPasswordViewModelComponent {
    void inject(ForgotPasswordViewModel forgotPasswordViewModel);
}
