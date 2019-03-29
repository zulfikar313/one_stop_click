package com.example.mitrais.onestopclick.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.support.annotation.NonNull;

import com.example.mitrais.onestopclick.dagger.component.DaggerRegistrationViewModelComponent;
import com.example.mitrais.onestopclick.dagger.component.RegistrationViewModelComponent;
import com.example.mitrais.onestopclick.model.repository.AuthRepository;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;

import javax.inject.Inject;

public class RegistrationViewModel extends AndroidViewModel implements AuthRepository.LoginListener {
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

    public RegistrationViewModel(@NonNull Application application) {
        super(application);

        // // Initialize dagger injection
        RegistrationViewModelComponent component = DaggerRegistrationViewModelComponent.builder()
                .build();
        component.inject(this);

        authRepository.setLoginListener(this);
    }

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

    // region public methods
    // Get currently logged in user
    public FirebaseUser getCurrentUser() {
        return authRepository.getUser();
    }

    // Register new user with email and password
    public Task<AuthResult> register(String email, String password) {
        return authRepository.register(email, password);
    }

    // Send email verification to user email address
    public Task<Void> sendVerificationEmail(FirebaseUser user) {
        return authRepository.sendVerificationEmail(user);
    }
    //endregion
}
