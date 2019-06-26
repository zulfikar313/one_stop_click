package com.example.mitrais.onestopclick.dagger.component;

import android.app.Application;

import com.example.mitrais.onestopclick.dagger.module.RepositoryModule;
import com.example.mitrais.onestopclick.view.add_product.AddProductViewModel;
import com.example.mitrais.onestopclick.view.add_profile.AddProfileViewModel;
import com.example.mitrais.onestopclick.view.cart.CartViewModel;
import com.example.mitrais.onestopclick.view.edit_book.EditBookViewModel;
import com.example.mitrais.onestopclick.view.edit_movie.EditMovieViewModel;
import com.example.mitrais.onestopclick.view.edit_music.EditMusicViewModel;
import com.example.mitrais.onestopclick.view.forgot_password.ForgotPasswordViewModel;
import com.example.mitrais.onestopclick.view.library.LibraryViewModel;
import com.example.mitrais.onestopclick.view.login.LoginViewModel;
import com.example.mitrais.onestopclick.view.main.MainViewModel;
import com.example.mitrais.onestopclick.view.main.product_list.ProductListViewModel;
import com.example.mitrais.onestopclick.view.main.edit_profile.ProfileViewModel;
import com.example.mitrais.onestopclick.view.registration.RegistrationViewModel;
import com.example.mitrais.onestopclick.view.search_product.SearchProductViewModel;
import com.example.mitrais.onestopclick.view.splash.SplashViewModel;

import dagger.BindsInstance;
import dagger.Component;

@Component(modules = RepositoryModule.class)
public interface ViewModelComponent {
    void inject(RegistrationViewModel viewModel);

    void inject(ForgotPasswordViewModel viewModel);

    void inject(SplashViewModel viewModel);

    void inject(LoginViewModel viewModel);

    void inject(MainViewModel viewModel);

    void inject(ProfileViewModel viewModel);

    void inject(AddProductViewModel viewModel);

    void inject(ProductListViewModel viewModel);

    void inject(SearchProductViewModel viewModel);

    void inject(EditBookViewModel viewModel);

    void inject(EditMusicViewModel viewModel);

    void inject(EditMovieViewModel viewModel);

    void inject(AddProfileViewModel viewModel);

    void inject(CartViewModel viewModel);

    void inject(LibraryViewModel viewModel);

    @Component.Builder
    interface Builder {
        @BindsInstance
        Builder application(Application application);

        ViewModelComponent build();
    }
}
