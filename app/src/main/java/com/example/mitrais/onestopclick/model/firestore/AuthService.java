package com.example.mitrais.onestopclick.model.firestore;

import android.net.Uri;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class AuthService {
    private static AuthService instance;
    private static FirebaseAuth auth;

    public static AuthService getInstance() {
        if (instance == null) {
            instance = new AuthService();
            auth = FirebaseAuth.getInstance();
        }

        return instance;
    }

    // login to firebase auth using email and password
    public Task<AuthResult> login(String email, String password) {
        return auth.signInWithEmailAndPassword(email, password);
    }

    // register new user with email and password
    public Task<AuthResult> register(String email, String password) {
        return auth.createUserWithEmailAndPassword(email, password);
    }

    // logout from current user
    public void logout() {
        auth.signOut();
    }

    // send email verification to user email address
    public Task<Void> sendVerificationEmail(FirebaseUser user) {
        return user.sendEmailVerification();
    }

    // send password reset email to user email address
    public Task<Void> sendPasswordResetEmail(String email) {
        return auth.sendPasswordResetEmail(email);
    }

    // get currently signed in user
    public FirebaseUser getUser() {
        return auth.getCurrentUser();
    }

    // save user display name
    public Task<Void> saveUser(String displayName) {
        UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                .setDisplayName(displayName)
                .build();
        return getUser().updateProfile(request);
    }

    // save user photo uri
    public Task<Void> saveUser(Uri photoUri) {
        UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                .setPhotoUri(photoUri)
                .build();
        return getUser().updateProfile(request);
    }
}
