package com.example.mitrais.onestopclick.view.main.edit_profile;

import com.example.mitrais.onestopclick.dagger.module.ViewModelModule;

import dagger.BindsInstance;
import dagger.Component;

@Component(modules = ViewModelModule.class)
public interface ProfileFragmentComponent {
    void inject(ProfileFragment fragment);

    @Component.Builder
    interface Builder {
        @BindsInstance
        Builder profileFragment(ProfileFragment fragment);

        ProfileFragmentComponent build();
    }
}
