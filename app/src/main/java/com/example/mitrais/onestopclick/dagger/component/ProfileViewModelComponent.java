package com.example.mitrais.onestopclick.dagger.component;

import android.app.Application;

import com.example.mitrais.onestopclick.dagger.module.RepositoryModule;
import com.example.mitrais.onestopclick.viewmodel.ProfileViewModel;

import dagger.BindsInstance;
import dagger.Component;

@Component(modules = RepositoryModule.class)
public interface ProfileViewModelComponent {
    void inject(ProfileViewModel profileViewModel);

    @Component.Builder
    interface Builder {
        @BindsInstance
        Builder application(Application application);

        ProfileViewModelComponent build();
    }
}
