package com.example.mitrais.onestopclick.dagger.module;

import android.arch.lifecycle.ViewModelProviders;

import com.example.mitrais.onestopclick.view.MainActivity;
import com.example.mitrais.onestopclick.viewmodel.MainViewModel;

import dagger.Module;
import dagger.Provides;

@Module
public class MainViewModelModule {
    @Provides
    MainViewModel provideMainViewModel(MainActivity activity) {
        return ViewModelProviders.of(activity).get(MainViewModel.class);
    }
}
