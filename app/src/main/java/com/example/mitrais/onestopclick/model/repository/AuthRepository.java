package com.example.mitrais.onestopclick.model.repository;

import com.example.mitrais.onestopclick.dagger.component.AuthRepositoryComponent;
import com.example.mitrais.onestopclick.dagger.component.DaggerAuthRepositoryComponent;
import com.example.mitrais.onestopclick.model.firestore.AuthService;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
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

    /**
     * login using email and password
     *
     * @param email    user email address
     * @param password user password
     * @return login task
     */
    public Task<AuthResult> login(String email, String password) {
        return authService.login(email, password);
    }

    /**
     * sign in using google account
     *
     * @param account google sign in account
     * @return google sign in task
     */
    public Task<AuthResult> googleSignIn(GoogleSignInAccount account) {
        return authService.googleSignIn(account);
    }

    /**
     * register new user user
     *
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
     * send verification email to user email address
     *
     * @param user logged in user
     * @return send verification email task
     */
    public Task<Void> sendVerificationEmail(FirebaseUser user) {
        return authService.sendVerificationEmail(user);
    }

    /**
     * send password reset email to user email address
     *
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
