package com.example.mitrais.onestopclick.dagger.module;

import android.arch.lifecycle.ViewModelProviders;
import com.example.mitrais.onestopclick.view.forgot_password.ForgotPasswordActivity;
import com.example.mitrais.onestopclick.view.login.LoginActivity;
import com.example.mitrais.onestopclick.view.main.MainActivity;
import com.example.mitrais.onestopclick.view.product_detail.ProductDetailActivity;
import com.example.mitrais.onestopclick.view.main.product_list.ProductListFragment;
import com.example.mitrais.onestopclick.view.main.edit_profile.ProfileFragment;
import com.example.mitrais.onestopclick.view.registration.RegistrationActivity;
import com.example.mitrais.onestopclick.view.search_product.SearchProductActivity;
import com.example.mitrais.onestopclick.view.splash.SplashActivity;
import com.example.mitrais.onestopclick.view.forgot_password.ForgotPasswordViewModel;
import com.example.mitrais.onestopclick.view.login.LoginViewModel;
import com.example.mitrais.onestopclick.view.main.MainViewModel;
import com.example.mitrais.onestopclick.view.product_detail.ProductDetailViewModel;
import com.example.mitrais.onestopclick.view.main.product_list.ProductListViewModel;
import com.example.mitrais.onestopclick.view.main.edit_profile.ProfileViewModel;
import com.example.mitrais.onestopclick.view.registration.RegistrationViewModel;
import com.example.mitrais.onestopclick.view.search_product.SearchProductViewModel;
import com.example.mitrais.onestopclick.view.splash.SplashViewModel;

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

    @Provides
    MainViewModel provideMainViewModel(MainActivity activity) {
        return ViewModelProviders.of(activity).get(MainViewModel.class);
    }

    @Provides
    ProductDetailViewModel provideProductDetailViewModel(ProductDetailActivity activity) {
        return ViewModelProviders.of(activity).get(ProductDetailViewModel.class);
    }

    @Provides
    SearchProductViewModel provideSearchProductViewModel(SearchProductActivity activity) {
        return ViewModelProviders.of(activity).get(SearchProductViewModel.class);
    }

    @Provides
    ProductListViewModel provideProductViewModel(ProductListFragment fragment) {
        return ViewModelProviders.of(fragment).get(ProductListViewModel.class);
    }

    @Provides
    ProfileViewModel provideProfileViewModel(ProfileFragment fragment) {
        return ViewModelProviders.of(fragment).get(ProfileViewModel.class);
    }
}
