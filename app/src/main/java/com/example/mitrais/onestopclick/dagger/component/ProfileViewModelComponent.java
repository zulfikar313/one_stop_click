package com.example.mitrais.onestopclick.dagger.component;

import android.app.Application;

import com.example.mitrais.onestopclick.dagger.module.AuthRepositoryModule;
import com.example.mitrais.onestopclick.dagger.module.ProfileRepositoryModule;
import com.example.mitrais.onestopclick.dagger.module.StorageRepositoryModule;
import com.example.mitrais.onestopclick.viewmodel.ProfileViewModel;

import dagger.BindsInstance;
import dagger.Component;

@Component(modules = {AuthRepositoryModule.class, StorageRepositoryModule.class, ProfileRepositoryModule.class})
public interface ProfileViewModelComponent {
    void inject(ProfileViewModel profileViewModel);

    @Component.Builder
    interface Builder {
        @BindsInstance
        Builder application(Application application);

        ProfileViewModelComponent build();
    }
}
