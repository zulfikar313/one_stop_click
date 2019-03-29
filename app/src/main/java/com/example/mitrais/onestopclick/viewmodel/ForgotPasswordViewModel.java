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

public class ForgotPasswordViewModel extends AndroidViewModel implements AuthRepository.LoginListener {
    private ResultListener listener;

    public interface ResultListener {
        void onLoginSuccess();

        void onLoginFailed(Exception e);
    }

    public void setListener(ResultListener listener) {
        this.listener = listener;
    }

    @Inject
    AuthRepository authRepository;

    public ForgotPasswordViewModel(@NonNull Application application) {
        super(application);

        // Initialize dagger injection
        ForgotPasswordViewModelComponent component = DaggerForgotPasswordViewModelComponent.builder()
                .build();
        component.inject(this);

        authRepository.setLoginListener(this);
    }

    // region public methods
    // Get currently logged in user
    public FirebaseUser getCurrentUser() {
        return authRepository.getUser();
    }

    // Send password reset email to user email address
    public Task<Void> sendPasswordResetEmail(String email) {
        return authRepository.sendPasswordResetEmail(email);
    }
    //endregion

    @Override
    public void onLoginSuccess() {
        if (listener != null)
            listener.onLoginSuccess();
    }

    @Override
    public void onLoginFailed(Exception e) {
        if (listener != null)
            listener.onLoginFailed(e);
    }
}
