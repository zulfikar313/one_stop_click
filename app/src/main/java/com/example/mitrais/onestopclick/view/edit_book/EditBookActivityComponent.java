package com.example.mitrais.onestopclick.view.edit_book;

import com.example.mitrais.onestopclick.dagger.module.ViewModelModule;

import dagger.BindsInstance;
import dagger.Component;

@Component(modules = ViewModelModule.class)
public interface EditBookActivityComponent {
    void inject(EditBookActivity activity);

    @Component.Builder
    interface Builder {
        @BindsInstance
        Builder editBookActivity(EditBookActivity activity);

        EditBookActivityComponent build();
    }
}
