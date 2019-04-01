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

public class LoginViewModel extends AndroidViewModel {
    @Inject
    AuthRepository authRepository;

    @Inject
    ProfileRepository profileRepository;

    public LoginViewModel(@NonNull Application application) {
        super(application);
        initDagger(application);
    }

    // initialize dagger injection
    private void initDagger(Application application) {
        LoginViewModelComponent component = DaggerLoginViewModelComponent.builder()
                .application(application)
                .build();
        component.inject(this);
    }

    // login with email and password
    public Task<AuthResult> login(String email, String password) {
        return authRepository.login(email, password);
    }

    // get logged in øuser
    public FirebaseUser getCurrentUser() {
        return authRepository.getUser();
    }

    // send email verification to user email address
    public Task<Void> sendVerificationEmail(FirebaseUser user) {
        return authRepository.sendVerificationEmail(user);
    }

    // sync data with firebase
    public Task<DocumentSnapshot> syncData(FirebaseUser user) {
        if (user != null)
            return profileRepository.getProfileByEmail(user.getEmail());
        else
            return null;
    }
}
