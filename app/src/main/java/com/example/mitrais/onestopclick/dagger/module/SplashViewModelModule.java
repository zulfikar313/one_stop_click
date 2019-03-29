package com.example.mitrais.onestopclick.dagger.module;

import android.arch.lifecycle.ViewModelProviders;

import com.example.mitrais.onestopclick.view.SplashActivity;
import com.example.mitrais.onestopclick.viewmodel.SplashViewModel;

import dagger.Module;
import dagger.Provides;

@Module
public class SplashViewModelModule {
    @Provides
    SplashViewModel provideSplashViewModule(SplashActivity activity) {
        return ViewModelProviders.of(activity).get(SplashViewModel.class);
    }
}
