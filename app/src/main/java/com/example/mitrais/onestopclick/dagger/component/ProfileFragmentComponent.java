package com.example.mitrais.onestopclick.dagger.component;

import com.example.mitrais.onestopclick.dagger.module.ProfileViewModelModule;
import com.example.mitrais.onestopclick.view.ProfileFragment;

import dagger.BindsInstance;
import dagger.Component;

@Component(modules = ProfileViewModelModule.class)
public interface ProfileFragmentComponent {
    void inject(ProfileFragment fragment);

    @Component.Builder
    interface Builder {
        @BindsInstance
        Builder profileFragment(ProfileFragment fragment);

        ProfileFragmentComponent build();
    }
}
