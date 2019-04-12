package com.example.mitrais.onestopclick.model.repository;

import android.net.Uri;

import com.example.mitrais.onestopclick.dagger.component.AuthRepositoryComponent;
import com.example.mitrais.onestopclick.dagger.component.DaggerAuthRepositoryComponent;
import com.example.mitrais.onestopclick.model.firestore.AuthService;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;

import javax.inject.Inject;

/**
 * AuthRepository class provide access to AuthService
 */
public class AuthRepository {
    @Inject
    AuthService authService;

    /**
     * AuthRepository constructor
     */
    public AuthRepository() {
        initDagger();
    }

    /**
     * initialize dagger injection
     */
    private void initDagger() {
        AuthRepositoryComponent component = DaggerAuthRepositoryComponent.builder()
                .build();
        component.inject(this);
    }

    /**
     * log user in
     *
     * @param email    user email address
     * @param password user password
     * @return login task
     */
    public Task<AuthResult> login(String email, String password) {
        return authService.login(email, password);
    }


    /**
     * register user
     *
     * @param email    user email address
     * @param password user password
     * @return register task
     */
    public Task<AuthResult> register(String email, String password) {
        return authService.register(email, password);
    }

    /**
     * returns logged in user
     *
     * @return user
     */
    public FirebaseUser getUser() {
        return authService.getUser();
    }

    /**
     * send verification email
     *
     * @param user logged in user
     * @return send verification email task
     */
    public Task<Void> sendVerificationEmail(FirebaseUser user) {
        return authService.sendVerificationEmail(user);
    }

    /**
     * send password reset email
     *
     * @param email user email address
     * @return send password reset email task
     */
    public Task<Void> sendPasswordResetEmail(String email) {
        return authService.sendPasswordResetEmail(email);
    }

    /**
     * log user out and remove user data
     */
    public void logout() {
        authService.logout();
    }

    /**
     * set user display name
     *
     * @param displayname user displayed name
     * @return set user task
     */
    public Task<Void> setUser(String displayname) {
        return authService.setUser(displayname);
    }

    /**
     * set user photo uri
     *
     * @param photoUri photo uri
     * @return set user task
     */
    public Task<Void> setUser(Uri photoUri) {
        return authService.setUser(photoUri);
    }
}
