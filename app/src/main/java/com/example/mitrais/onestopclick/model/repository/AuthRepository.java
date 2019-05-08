package com.example.mitrais.onestopclick.model.repository;

import com.example.mitrais.onestopclick.dagger.component.AuthRepositoryComponent;
import com.example.mitrais.onestopclick.dagger.component.DaggerAuthRepositoryComponent;
import com.example.mitrais.onestopclick.model.firestore.AuthService;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;

import javax.inject.Inject;

public class AuthRepository {
    @Inject
    AuthService authService;

    public AuthRepository() {
        initDagger();
    }

    private void initDagger() {
        AuthRepositoryComponent component = DaggerAuthRepositoryComponent.builder()
                .build();
        component.inject(this);
    }

    public Task<AuthResult> login(String email, String password) {
        return authService.login(email, password);
    }

    public Task<AuthResult> register(String email, String password) {
        return authService.register(email, password);
    }

    public FirebaseUser getUser() {
        return authService.getUser();
    }

    public Task<Void> sendVerificationEmail(FirebaseUser user) {
        return authService.sendVerificationEmail(user);
    }

    public Task<Void> sendPasswordResetEmail(String email) {
        return authService.sendPasswordResetEmail(email);
    }

    public void logout() {
        authService.logout();
    }
}
