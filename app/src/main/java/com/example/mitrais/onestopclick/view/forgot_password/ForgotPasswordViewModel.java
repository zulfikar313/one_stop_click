package com.example.mitrais.onestopclick.view.forgot_password;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.support.annotation.NonNull;

import com.example.mitrais.onestopclick.dagger.component.DaggerViewModelComponent;
import com.example.mitrais.onestopclick.dagger.component.ViewModelComponent;
import com.example.mitrais.onestopclick.model.repository.AuthRepository;
import com.google.android.gms.tasks.Task;

import javax.inject.Inject;

public class ForgotPasswordViewModel extends AndroidViewModel {
    @Inject
    AuthRepository authRepo;

    public ForgotPasswordViewModel(@NonNull Application application) {
        super(application);
        initDagger(application);
    }

    Task<Void> sendPasswordResetEmail(String email) {
        return authRepo.sendPasswordResetEmail(email);
    }

    private void initDagger(Application application) {
        ViewModelComponent component = DaggerViewModelComponent.builder()
                .application(application)
                .build();
        component.inject(this);
    }
}
