package com.example.mitrais.onestopclick.dagger.component;

import android.app.Application;

import com.example.mitrais.onestopclick.dagger.module.DaoModule;
import com.example.mitrais.onestopclick.dagger.module.ServiceModule;
import com.example.mitrais.onestopclick.model.repository.ProductRepository;
import com.example.mitrais.onestopclick.model.repository.ProfileRepository;

import dagger.BindsInstance;
import dagger.Component;

@Component(modules = {ServiceModule.class, DaoModule.class})
public interface RepositoryComponent {
    void inject(ProfileRepository profileRepository);

    void inject(ProductRepository productRepository);

    @Component.Builder
    interface Builder {
        @BindsInstance
        Builder application(Application application);

        RepositoryComponent build();
    }
}
