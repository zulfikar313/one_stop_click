package com.example.mitrais.onestopclick.dagger.module;

import android.arch.lifecycle.ViewModelProviders;

import com.example.mitrais.onestopclick.view.RegistrationActivity;
import com.example.mitrais.onestopclick.viewmodel.RegistrationViewModel;

import dagger.Module;
import dagger.Provides;

@Module
public class RegistrationViewModelModule {
    @Provides
    RegistrationViewModel provideRegistrationViewModel(RegistrationActivity activity) {
        return ViewModelProviders.of(activity).get(RegistrationViewModel.class);
    }
}
