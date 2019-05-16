package com.example.mitrais.onestopclick.view.edit_music;

import com.example.mitrais.onestopclick.dagger.module.ViewModelModule;

import dagger.BindsInstance;
import dagger.Component;

@Component(modules = ViewModelModule.class)
public interface EditMusicActivityComponent {
    void inject(EditMusicActivity activity);

    @Component.Builder
    interface Builder {
        @BindsInstance
        Builder editMusicActivity(EditMusicActivity activity);

        EditMusicActivityComponent build();
    }
}