package com.example.mitrais.onestopclick.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.support.annotation.NonNull;

import com.example.mitrais.onestopclick.dagger.component.DaggerViewModelComponent;
import com.example.mitrais.onestopclick.dagger.component.ViewModelComponent;
import com.example.mitrais.onestopclick.model.repository.AuthRepository;
import com.google.android.gms.tasks.Task;

import javax.inject.Inject;

public class ForgotPasswordViewModel extends AndroidViewModel {
    @Inject
    AuthRepository authRepository;

    public ForgotPasswordViewModel(@NonNull Application application) {
        super(application);
        initDagger(application);
    }

    /**
     * @param email user email address
     * @return send password reset email task
     */
    public Task<Void> sendPasswordResetEmail(String email) {
        return authRepository.sendPasswordResetEmail(email);
    }

    /**
     * initialize dagger injection
     */
    private void initDagger(Application application) {
        ViewModelComponent component = DaggerViewModelComponent.builder()
                .application(application)
                .build();
        component.inject(this);
    }
}
