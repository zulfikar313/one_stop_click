package com.example.mitrais.onestopclick.dagger.module;

import android.arch.lifecycle.ViewModelProviders;
import com.example.mitrais.onestopclick.view.ForgotPasswordActivity;
import com.example.mitrais.onestopclick.view.LoginActivity;
import com.example.mitrais.onestopclick.view.RegistrationActivity;
import com.example.mitrais.onestopclick.view.SplashActivity;
import com.example.mitrais.onestopclick.viewmodel.ForgotPasswordViewModel;
import com.example.mitrais.onestopclick.viewmodel.LoginViewModel;
import com.example.mitrais.onestopclick.viewmodel.RegistrationViewModel;
import com.example.mitrais.onestopclick.viewmodel.SplashViewModel;

import dagger.Module;
import dagger.Provides;

@Module
public class ViewModelModule {
    @Provides
    SplashViewModel provideSplashViewModel(SplashActivity activity) {
        return ViewModelProviders.of(activity).get(SplashViewModel.class);
    }

    @Provides
    LoginViewModel provideLoginViewModel(LoginActivity activity) {
        return ViewModelProviders.of(activity).get(LoginViewModel.class);
    }

    @Provides
    RegistrationViewModel provideRegistrationViewModel(RegistrationActivity activity) {
        return ViewModelProviders.of(activity).get(RegistrationViewModel.class);
    }

    @Provides
    ForgotPasswordViewModel provideForgotPasswordViewModel(ForgotPasswordActivity activity) {
        return ViewModelProviders.of(activity).get(ForgotPasswordViewModel.class);
    }
}
