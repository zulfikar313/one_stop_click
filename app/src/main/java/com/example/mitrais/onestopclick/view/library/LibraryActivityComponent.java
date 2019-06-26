package com.example.mitrais.onestopclick.view.library;

import com.example.mitrais.onestopclick.dagger.module.ViewModelModule;

import dagger.BindsInstance;
import dagger.Component;

@Component(modules = ViewModelModule.class)
interface LibraryActivityComponent {
    void inject(LibraryActivity activity);

    @Component.Builder
    interface Builder {
        @BindsInstance
        Builder libraryActivity(LibraryActivity activity);

        LibraryActivityComponent build();
    }
}
