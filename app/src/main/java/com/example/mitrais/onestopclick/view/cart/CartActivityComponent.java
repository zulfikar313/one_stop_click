package com.example.mitrais.onestopclick.view.cart;

import com.example.mitrais.onestopclick.dagger.module.ViewModelModule;

import dagger.BindsInstance;
import dagger.Component;

@Component(modules = ViewModelModule.class)
public interface CartActivityComponent {
    void inject(CartActivity activity);

    @Component.Builder
    interface Builder {
        @BindsInstance
        Builder cartActivity(CartActivity activity);

        CartActivityComponent build();
    }
}
