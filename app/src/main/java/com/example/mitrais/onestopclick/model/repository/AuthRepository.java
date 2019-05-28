package com.example.mitrais.onestopclick.model.repository;

import android.app.Application;

import com.example.mitrais.onestopclick.dagger.component.DaggerRepositoryComponent;
import com.example.mitrais.onestopclick.dagger.component.RepositoryComponent;
import com.example.mitrais.onestopclick.model.firestore.AuthService;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;

import javax.inject.Inject;

public class AuthRepository {
    @Inject
    AuthService authService;

    public AuthRepository(Application application) {
        initDagger(application);
    }

    public Task<AuthResult> login(String email, String password) {
        return authService.login(email, password);
    }

    public Task<AuthResult> googleSignIn(GoogleSignInAccount account) {
        return authService.googleSignIn(account);
    }

    public Task<AuthResult> register(String email, String password) {
        return authService.register(email, password);
    }

    public void logout() {
        authService.logout();
    }

    public Task<Void> sendVerificationEmail(FirebaseUser user) {
        return authService.sendVerificationEmail(user);
    }

    public Task<Void> sendPasswordResetEmail(String email) {
        return authService.sendPasswordResetEmail(email);
    }

    public FirebaseUser getUser() {
        return authService.getUser();
    }

    private void initDagger(Application application) {
        RepositoryComponent component = DaggerRepositoryComponent.builder()
                .application(application)
                .build();
        component.inject(this);
    }
}
