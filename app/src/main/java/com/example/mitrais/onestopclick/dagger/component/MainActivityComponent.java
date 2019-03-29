package com.example.mitrais.onestopclick.dagger.component;

import com.example.mitrais.onestopclick.dagger.module.MainViewModelModule;
import com.example.mitrais.onestopclick.view.MainActivity;

import dagger.BindsInstance;
import dagger.Component;

@Component(modules = MainViewModelModule.class)
public interface MainActivityComponent {
    void inject(MainActivity mainActivity);

    @Component.Builder
    interface Builder {
        @BindsInstance
        Builder mainActivity(MainActivity mainActivity);

        MainActivityComponent build();
    }
}
