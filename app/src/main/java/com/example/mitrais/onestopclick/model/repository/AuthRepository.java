package com.example.mitrais.onestopclick.model.repository;

import android.net.Uri;

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

    // initialize dagger injection
    private void initDagger() {
        AuthRepositoryComponent component = DaggerAuthRepositoryComponent.builder()
                .build();
        component.inject(this);
    }

    // region public methods
    // login with email and password
    public Task<AuthResult> login(String email, String password) {
        return authService.login(email, password);
    }

    // register with email and password
    public Task<AuthResult> register(String email, String password) {
        return authService.register(email, password);
    }

    // get currently logged in user
    public FirebaseUser getUser() {
        return authService.getUser();
    }

    // send verification email to user email address
    public Task<Void> sendVerificationEmail(FirebaseUser user) {
        return authService.sendVerificationEmail(user);
    }

    // send password reset email to user email address
    public Task<Void> sendPasswordResetEmail(String email) {
        return authService.sendPasswordResetEmail(email);
    }

    // logout from current user
    public void logout() {
        authService.logout();
    }

    // update user display name
    public Task<Void> saveUser(String displayName) {
        return authService.saveUser(displayName);
    }

    // update user photo uri
    public Task<Void> saveUser(Uri photoUri) {
        return authService.saveUser(photoUri);
    }
    // endregion
}
