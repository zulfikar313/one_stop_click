package com.example.mitrais.onestopclick.dagger.component;

import com.example.mitrais.onestopclick.dagger.module.RepositoryModule;
import com.example.mitrais.onestopclick.viewmodel.ForgotPasswordViewModel;

import dagger.Component;

@Component(modules = RepositoryModule.class)
public interface ForgotPasswordViewModelComponent {
    void inject(ForgotPasswordViewModel forgotPasswordViewModel);
}
