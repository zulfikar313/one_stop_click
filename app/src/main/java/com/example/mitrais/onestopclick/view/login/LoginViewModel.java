package com.example.mitrais.onestopclick.view.login;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.support.annotation.NonNull;

import com.example.mitrais.onestopclick.dagger.component.DaggerViewModelComponent;
import com.example.mitrais.onestopclick.dagger.component.ViewModelComponent;
import com.example.mitrais.onestopclick.model.repository.AuthRepository;
import com.example.mitrais.onestopclick.model.repository.ProductRepository;
import com.example.mitrais.onestopclick.model.repository.ProfileRepository;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;

import javax.inject.Inject;

public class LoginViewModel extends AndroidViewModel {
    private static final String TAG = "LoginViewModel";

    @Inject
    AuthRepository authRepo;

    @Inject
    ProfileRepository profileRepo;

    @Inject
    ProductRepository productRepo;

    public LoginViewModel(@NonNull Application application) {
        super(application);
        initDagger(application);
    }

    Task<AuthResult> login(String email, String password) {
        return authRepo.login(email, password);
    }

    Task<AuthResult> googleSignIn(GoogleSignInAccount account) {
        return authRepo.googleSignIn(account);
    }

    FirebaseUser getUser() {
        return authRepo.getUser();
    }

    Task<Void> sendVerificationEmail(FirebaseUser user) {
        return authRepo.sendVerificationEmail(user);
    }

    Task<DocumentSnapshot> syncProfile(@NonNull FirebaseUser user) {
        return profileRepo.sync(user.getEmail());
    }

    private void initDagger(Application application) {
        ViewModelComponent component = DaggerViewModelComponent.builder()
                .application(application)
                .build();
        component.inject(this);
    }


}
