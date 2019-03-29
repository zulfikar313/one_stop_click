package com.example.mitrais.onestopclick.dagger.component;

import com.example.mitrais.onestopclick.dagger.module.ForgotPasswordViewModelModule;
import com.example.mitrais.onestopclick.view.ForgotPasswordActivity;

import dagger.BindsInstance;
import dagger.Component;

@Component(modules = ForgotPasswordViewModelModule.class)
public interface ForgotPasswordActivityComponent {
    void inject(ForgotPasswordActivity forgotPasswordActivity);

    @Component.Builder
    interface Builder {
        @BindsInstance
        Builder forgotPasswordActivity(ForgotPasswordActivity forgotPasswordActivity);

        ForgotPasswordActivityComponent build();
    }
}
