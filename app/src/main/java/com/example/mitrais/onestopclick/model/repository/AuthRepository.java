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

    private void initDagger(Application application) {
        RepositoryComponent component = DaggerRepositoryComponent.builder()
                .application(application)
                .build();
        component.inject(this);
    }

    /**
     * @param email    user email address
     * @param password user password
     * @return login task
     */
    public Task<AuthResult> login(String email, String password) {
        return authService.login(email, password);
    }

    /**
     * @param account google sign in account
     * @return google sign in task
     */
    public Task<AuthResult> googleSignIn(GoogleSignInAccount account) {
        return authService.googleSignIn(account);
    }

    /**
     * @param email    user email address
     * @param password user password
     * @return register task
     */
    public Task<AuthResult> register(String email, String password) {
        return authService.register(email, password);
    }

    /**
     * @return logged in user
     */
    public FirebaseUser getUser() {
        return authService.getUser();
    }

    /**
     * @param user logged in user
     * @return send verification email task
     */
    public Task<Void> sendVerificationEmail(FirebaseUser user) {
        return authService.sendVerificationEmail(user);
    }

    /**
     * @param email email address
     * @return send password reset task
     */
    public Task<Void> sendPasswordResetEmail(String email) {
        return authService.sendPasswordResetEmail(email);
    }

    /**
     * log user out
     */
    public void logout() {
        authService.logout();
    }
}
