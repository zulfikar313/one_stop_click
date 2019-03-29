package com.example.mitrais.onestopclick.model.repository;

import android.net.Uri;

import com.example.mitrais.onestopclick.dagger.component.AuthRepositoryComponent;
import com.example.mitrais.onestopclick.dagger.component.DaggerAuthRepositoryComponent;
import com.example.mitrais.onestopclick.model.firestore.AuthService;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;

import javax.inject.Inject;

public class AuthRepository implements AuthService.ResultListener {
    private AuthChangeListener authChangeListener;
    private LoginListener loginListener;

    @Inject
    AuthService authService;

    public interface AuthChangeListener {
        void onAuthChangedListener(FirebaseUser user);
    }

    public interface LoginListener {
        void onLoginSuccess();

        void onLoginFailed(Exception e);
    }

    public void setAuthChangeListener(AuthChangeListener authChangeListener) {
        this.authChangeListener = authChangeListener;
    }

    public void setLoginListener(LoginListener loginListener) {
        this.loginListener = loginListener;
    }

    public AuthRepository() {
        // initialize dagger
        AuthRepositoryComponent component = DaggerAuthRepositoryComponent.builder()
                .build();
        component.inject(this);

        authService.setListener(this);
    }

    @Override
    public void onAuthChanged(FirebaseUser user) {
        if (authChangeListener != null)
            authChangeListener.onAuthChangedListener(user);
    }

    // region public methods
    // login with email and password
    public Task<AuthResult> login(String email, String password) {
        return authService.login(email, password)
                .addOnSuccessListener(authResult -> {
                    if (loginListener != null)
                        loginListener.onLoginSuccess();
                })
                .addOnFailureListener(e -> {
                    if (loginListener != null)
                        loginListener.onLoginFailed(e);
                });
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

    // update user
    public Task<Void> updateUser(String displayName) {
        return authService.updateUser(displayName);
    }

    // update user
    public Task<Void> updateUser(Uri photoUri) {
        return authService.updateUser(photoUri);
    }
    // endregion
}
