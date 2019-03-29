package com.example.mitrais.onestopclick.dagger.module;

import android.arch.lifecycle.ViewModelProviders;

import com.example.mitrais.onestopclick.view.LoginActivity;
import com.example.mitrais.onestopclick.viewmodel.LoginViewModel;

import dagger.Module;
import dagger.Provides;

@Module
public class LoginViewModelModule {
    @Provides
    LoginViewModel provideLoginViewModel(LoginActivity activity){
        return ViewModelProviders.of(activity).get(LoginViewModel.class);
    }
}
