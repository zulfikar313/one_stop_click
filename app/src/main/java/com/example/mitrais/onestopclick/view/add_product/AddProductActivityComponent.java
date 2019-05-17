package com.example.mitrais.onestopclick.view.add_product;

import com.example.mitrais.onestopclick.dagger.module.ViewModelModule;

import dagger.BindsInstance;
import dagger.Component;

@Component(modules = ViewModelModule.class)
public interface AddProductActivityComponent {
    void inject(AddProductActivity activity);

    @Component.Builder
    interface Builder {
        @BindsInstance
        Builder addProductActivity(AddProductActivity activity);

        AddProductActivityComponent build();
    }
}
