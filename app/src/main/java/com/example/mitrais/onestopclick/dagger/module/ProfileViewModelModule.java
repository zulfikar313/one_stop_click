package com.example.mitrais.onestopclick.dagger.module;

import android.arch.lifecycle.ViewModelProviders;

import com.example.mitrais.onestopclick.view.ProfileFragment;
import com.example.mitrais.onestopclick.viewmodel.ProfileViewModel;

import dagger.Module;
import dagger.Provides;

@Module
public class ProfileViewModelModule {
    @Provides
    ProfileViewModel provideProfileViewModel(ProfileFragment fragment) {
        return ViewModelProviders.of(fragment).get(ProfileViewModel.class);
    }
}
