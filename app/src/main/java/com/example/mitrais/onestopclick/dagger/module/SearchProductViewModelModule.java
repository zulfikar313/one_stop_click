package com.example.mitrais.onestopclick.dagger.module;

import android.arch.lifecycle.ViewModelProviders;

import com.example.mitrais.onestopclick.view.SearchProductActivity;
import com.example.mitrais.onestopclick.viewmodel.SearchProductViewModel;

import dagger.Module;
import dagger.Provides;

@Module
public class SearchProductViewModelModule {
    @Provides
    SearchProductViewModel provideSearchProductViewModel(SearchProductActivity activity) {
        return ViewModelProviders.of(activity).get(SearchProductViewModel.class);
    }
}
