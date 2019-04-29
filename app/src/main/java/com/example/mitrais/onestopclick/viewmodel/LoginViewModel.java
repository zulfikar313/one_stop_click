package com.example.mitrais.onestopclick.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.support.annotation.NonNull;

import com.example.mitrais.onestopclick.dagger.component.DaggerLoginViewModelComponent;
import com.example.mitrais.onestopclick.dagger.component.LoginViewModelComponent;
import com.example.mitrais.onestopclick.model.repository.AuthRepository;
import com.example.mitrais.onestopclick.model.repository.ProfileRepository;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;

import javax.inject.Inject;

/**
 * LoginViewModel class handle data lifecycle for LoginActivity
 */
public class LoginViewModel extends AndroidViewModel {
    @Inject
    AuthRepository authRepository;

    @Inject
    ProfileRepository profileRepository;

    /**
     * LoginViewModel constructor
     *
     * @param application application to inject repository class
     */
    public LoginViewModel(@NonNull Application application) {
        super(application);
        initDagger(application);
    }

    /**
     * initialize dagger injection
     *
     * @param application application to inject repository class
     */
    private void initDagger(Application application) {
        LoginViewModelComponent component = DaggerLoginViewModelComponent.builder()
                .application(application)
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
        return authRepository.login(email, password);
    }

    /**
     * @return logged in user
     */
    public FirebaseUser getCurrentUser() {
        return authRepository.getUser();
    }

    /**
     * send verification email
     *
     * @param user logged in user
     * @return send verification email task
     */
    public Task<Void> sendVerificationEmail(FirebaseUser user) {
        return authRepository.sendVerificationEmail(user);
    }

    /**
     * synchronize user data
     *
     * @param user logged in user
     * @return sync data task
     */
    public Task<DocumentSnapshot> syncData(FirebaseUser user) {
        if (user != null)
            return profileRepository.syncProfile(user.getEmail());
        else
            return null;
    }
}
