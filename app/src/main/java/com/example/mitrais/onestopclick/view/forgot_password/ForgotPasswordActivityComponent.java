package com.example.mitrais.onestopclick.view.forgot_password;

import com.example.mitrais.onestopclick.dagger.module.ViewModelModule;

import dagger.BindsInstance;
import dagger.Component;

@Component(modules = ViewModelModule.class)
public interface ForgotPasswordActivityComponent {
    void inject(ForgotPasswordActivity forgotPasswordActivity);

    @Component.Builder
    interface Builder {
        @BindsInstance
        Builder forgotPasswordActivity(ForgotPasswordActivity forgotPasswordActivity);

        ForgotPasswordActivityComponent build();
    }
}
