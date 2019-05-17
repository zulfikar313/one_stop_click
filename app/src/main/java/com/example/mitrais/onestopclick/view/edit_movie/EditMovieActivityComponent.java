package com.example.mitrais.onestopclick.view.edit_movie;

import com.example.mitrais.onestopclick.dagger.module.ViewModelModule;

import dagger.BindsInstance;
import dagger.Component;

@Component(modules = ViewModelModule.class)
public interface EditMovieActivityComponent {
    void inject(EditMovieActivity activity);

    @Component.Builder
    interface Builder {
        @BindsInstance
        Builder editMovieActivity(EditMovieActivity activity);

        EditMovieActivityComponent build();
    }
}
