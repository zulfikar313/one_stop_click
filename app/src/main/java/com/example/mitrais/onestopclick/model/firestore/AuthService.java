package com.example.mitrais.onestopclick.model.firestore;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

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

    /**
     * login using email and password
     *
     * @param email    user email address
     * @param password user password
     * @return login task
     */
    public Task<AuthResult> login(String email, String password) {
        return auth.signInWithEmailAndPassword(email, password);
    }

    /**
     * sign in using google account
     *
     * @param account google sign in account
     * @return google sign in task
     */
    public Task<AuthResult> googleSignIn(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        return auth.signInWithCredential(credential);
    }

    /**
     * register new user user
     *
     * @param email    user email address
     * @param password user password
     * @return register task
     */
    public Task<AuthResult> register(String email, String password) {
        return auth.createUserWithEmailAndPassword(email, password);
    }

    /**
     * log user out
     */
    public void logout() {
        auth.signOut();
    }

    /**
     * send verification email to user email address
     *
     * @param user logged in user
     * @return send verification email task
     */
    public Task<Void> sendVerificationEmail(FirebaseUser user) {
        return user.sendEmailVerification();
    }

    /**
     * send password reset email to user email address
     *
     * @param email user email address
     * @return send password reset email task
     */
    public Task<Void> sendPasswordResetEmail(String email) {
        return auth.sendPasswordResetEmail(email);
    }

    /**
     * @return logged in user
     */
    public FirebaseUser getUser() {
        return auth.getCurrentUser();
    }
}
