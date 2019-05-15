package com.example.mitrais.onestopclick.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.support.annotation.NonNull;

import com.example.mitrais.onestopclick.dagger.component.DaggerServiceViewModelComponent;
import com.example.mitrais.onestopclick.dagger.component.ServiceViewModelComponent;
import com.example.mitrais.onestopclick.model.repository.AuthRepository;
import com.google.android.gms.tasks.Task;

import javax.inject.Inject;

/**
 * ForgotPasswordViewModel class handle data lifecycle for ForgotPasswordActivity
 */
public class ForgotPasswordViewModel extends AndroidViewModel {
    @Inject
    AuthRepository authRepository;

    /**
     * ForgotPasswordViewModel constructor
     *
     * @param application application to inject repository class
     */
    public ForgotPasswordViewModel(@NonNull Application application) {
        super(application);
        initDagger();
    }

    /**
     * send password reset email to user email address
     *
     * @param email user email address
     * @return send password reset email task
     */
    public Task<Void> sendPasswordResetEmail(String email) {
        return authRepository.sendPasswordResetEmail(email);
    }

    /**
     * initialize dagger injection
     */
    private void initDagger() {
        ServiceViewModelComponent component = DaggerServiceViewModelComponent.builder()
                .build();
        component.inject(this);
    }
}
