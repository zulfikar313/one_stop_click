package com.example.mitrais.onestopclick.dagger.component;

import android.app.Application;

import com.example.mitrais.onestopclick.dagger.module.RepositoryModule;
import com.example.mitrais.onestopclick.view.edit_book.EditBookViewModel;
import com.example.mitrais.onestopclick.view.edit_music.EditMusicViewModel;
import com.example.mitrais.onestopclick.view.forgot_password.ForgotPasswordViewModel;
import com.example.mitrais.onestopclick.view.login.LoginViewModel;
import com.example.mitrais.onestopclick.view.main.MainViewModel;
import com.example.mitrais.onestopclick.view.product_detail.ProductDetailViewModel;
import com.example.mitrais.onestopclick.view.main.product_list.ProductListViewModel;
import com.example.mitrais.onestopclick.view.main.edit_profile.ProfileViewModel;
import com.example.mitrais.onestopclick.view.registration.RegistrationViewModel;
import com.example.mitrais.onestopclick.view.search_product.SearchProductViewModel;
import com.example.mitrais.onestopclick.view.splash.SplashViewModel;

import dagger.BindsInstance;
import dagger.Component;

@Component(modules = RepositoryModule.class)
public interface ViewModelComponent {
    void inject(RegistrationViewModel registrationViewModel);

    void inject(ForgotPasswordViewModel forgotPasswordViewModel);

    void inject(SplashViewModel splashViewModel);

    void inject(LoginViewModel loginViewModel);

    void inject(MainViewModel mainViewModel);

    void inject(ProfileViewModel profileViewModel);

    void inject(ProductListViewModel productListViewModel);

    void inject(SearchProductViewModel viewModel);

    void inject(ProductDetailViewModel viewModel);

    void inject(EditBookViewModel viewModel);

    void inject(EditMusicViewModel viewModel);

    @Component.Builder
    interface Builder {
        @BindsInstance
        Builder application(Application application);

        ViewModelComponent build();
    }
}
