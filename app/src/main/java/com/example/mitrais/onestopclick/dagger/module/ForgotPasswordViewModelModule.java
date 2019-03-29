package com.example.mitrais.onestopclick.dagger.module;

import android.arch.lifecycle.ViewModelProviders;

import com.example.mitrais.onestopclick.view.ForgotPasswordActivity;
import com.example.mitrais.onestopclick.viewmodel.ForgotPasswordViewModel;

import dagger.Module;
import dagger.Provides;

@Module
public class ForgotPasswordViewModelModule {
    @Provides
    ForgotPasswordViewModel provideForgotPasswordViewModel(ForgotPasswordActivity activity) {
        return ViewModelProviders.of(activity).get(ForgotPasswordViewModel.class);
    }
}
