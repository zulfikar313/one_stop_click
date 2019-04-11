package com.example.mitrais.onestopclick.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.support.annotation.NonNull;

import com.example.mitrais.onestopclick.dagger.component.DaggerRegistrationViewModelComponent;
import com.example.mitrais.onestopclick.dagger.component.RegistrationViewModelComponent;
import com.example.mitrais.onestopclick.model.repository.AuthRepository;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;

import javax.inject.Inject;

/**
 * RegistrationViewModelClass handle data lifecycle for RegistrationActivity
 */
public class RegistrationViewModel extends AndroidViewModel {
    @Inject
    AuthRepository authRepository;

    /**
     * RegistrationViewModel constructor
     *
     * @param application application to inject repository class
     */
    public RegistrationViewModel(@NonNull Application application) {
        super(application);
        initDagger();
    }

    /**
     * get logged in user
     *
     * @return logged in user
     */
    public FirebaseUser getCurrentUser() {
        return authRepository.getUser();
    }

    /**
     * register new user
     *
     * @param email    user email address
     * @param password user password
     * @return register task
     */
    public Task<AuthResult> register(String email, String password) {
        return authRepository.register(email, password);
    }

    /**
     * send verification email to verify user email address
     *
     * @param user logged in user
     * @return send verification email task
     */
    public Task<Void> sendVerificationEmail(FirebaseUser user) {
        return authRepository.sendVerificationEmail(user);
    }

    /**
     * initialize dagger injection
     */
    private void initDagger() {
        RegistrationViewModelComponent component = DaggerRegistrationViewModelComponent.builder()
                .build();
        component.inject(this);
    }
}
