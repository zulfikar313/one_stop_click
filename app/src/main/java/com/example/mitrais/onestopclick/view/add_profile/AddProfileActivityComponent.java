package com.example.mitrais.onestopclick.view.add_profile;

import com.example.mitrais.onestopclick.dagger.module.ViewModelModule;

import dagger.BindsInstance;
import dagger.Component;

@Component(modules = ViewModelModule.class)
public interface AddProfileActivityComponent {
    void inject(AddProfileActivity activity);

    @Component.Builder
    interface Builder {
        @BindsInstance
        Builder addProfileActivity(AddProfileActivity activity);

        AddProfileActivityComponent build();
    }
}
