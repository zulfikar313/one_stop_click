package com.example.mitrais.onestopclick.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.support.annotation.NonNull;

import com.example.mitrais.onestopclick.dagger.component.DaggerForgotPasswordViewModelComponent;
import com.example.mitrais.onestopclick.dagger.component.ForgotPasswordViewModelComponent;
import com.example.mitrais.onestopclick.model.repository.AuthRepository;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;

import javax.inject.Inject;

public class ForgotPasswordViewModel extends AndroidViewModel {
    @Inject
    AuthRepository authRepository;

    public ForgotPasswordViewModel(@NonNull Application application) {
        super(application);
        initDagger();
    }

    // get logged in user
    public FirebaseUser getCurrentUser() {
        return authRepository.getUser();
    }

    // send password reset email to user email address
    public Task<Void> sendPasswordResetEmail(String email) {
        return authRepository.sendPasswordResetEmail(email);
    }

    // initialize dagger injection
    private void initDagger() {
        ForgotPasswordViewModelComponent component = DaggerForgotPasswordViewModelComponent.builder()
                .build();
        component.inject(this);
    }
}
