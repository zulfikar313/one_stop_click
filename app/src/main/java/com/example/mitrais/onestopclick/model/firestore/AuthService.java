package com.example.mitrais.onestopclick.model.firestore;

import android.net.Uri;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

/**
 * AuthService class provide access to FirebaseAuth
 */
public class AuthService {
    private static AuthService instance;
    private static FirebaseAuth auth;

    /**
     * returns AuthService singleton instance
     *
     * @return AuthService instance
     */
    public static AuthService getInstance() {
        if (instance == null) {
            instance = new AuthService();
            auth = FirebaseAuth.getInstance();
        }

        return instance;
    }

    /**
     * log user in and returns login task
     *
     * @param email    user email address
     * @param password user password
     * @return login task
     */
    public Task<AuthResult> login(String email, String password) {
        return auth.signInWithEmailAndPassword(email, password);
    }

    /**
     * Register user and returns register task
     *
     * @param email    user email address
     * @param password user password
     * @return register task
     */
    public Task<AuthResult> register(String email, String password) {
        return auth.createUserWithEmailAndPassword(email, password);
    }

    /**
     * log user out and remove user data
     */
    public void logout() {
        auth.signOut();
    }

    /**
     * send verification email to user email address
     * and returns send verification email task
     *
     * @param user logged in user
     * @return send verification email task
     */
    public Task<Void> sendVerificationEmail(FirebaseUser user) {
        return user.sendEmailVerification();
    }

    /**
     * send password reset email to user email address
     * and returns send password reset email task
     *
     * @param email user email address
     * @return send password reset email task
     */
    public Task<Void> sendPasswordResetEmail(String email) {
        return auth.sendPasswordResetEmail(email);
    }

    /**
     * returns currently logged in user
     *
     * @return logged in user
     */
    public FirebaseUser getUser() {
        return auth.getCurrentUser();
    }
}
