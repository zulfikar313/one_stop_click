package com.example.mitrais.onestopclick.dagger.component;

import android.app.Application;

import com.example.mitrais.onestopclick.dagger.module.RepositoryModule;
import com.example.mitrais.onestopclick.viewmodel.LoginViewModel;
import com.example.mitrais.onestopclick.viewmodel.MainViewModel;
import com.example.mitrais.onestopclick.viewmodel.ProductDetailViewModel;
import com.example.mitrais.onestopclick.viewmodel.ProductListViewModel;
import com.example.mitrais.onestopclick.viewmodel.ProfileViewModel;
import com.example.mitrais.onestopclick.viewmodel.SearchProductViewModel;
import com.example.mitrais.onestopclick.viewmodel.SplashViewModel;

import dagger.BindsInstance;
import dagger.Component;

@Component(modules = RepositoryModule.class)
public interface ViewModelComponent {
    void inject(SplashViewModel splashViewModel);

    void inject(LoginViewModel loginViewModel);

    void inject(MainViewModel mainViewModel);

    void inject(ProfileViewModel profileViewModel);

    void inject(ProductListViewModel productListViewModel);

    void inject(SearchProductViewModel viewModel);

    void inject(ProductDetailViewModel viewModel);

    @Component.Builder
    interface Builder {
        @BindsInstance
        Builder application(Application application);

        ViewModelComponent build();
    }
}
