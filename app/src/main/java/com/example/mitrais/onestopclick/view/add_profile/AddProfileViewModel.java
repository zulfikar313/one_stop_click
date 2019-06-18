package com.example.mitrais.onestopclick.view.add_profile;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.example.mitrais.onestopclick.dagger.component.DaggerViewModelComponent;
import com.example.mitrais.onestopclick.dagger.component.ViewModelComponent;
import com.example.mitrais.onestopclick.model.Profile;
import com.example.mitrais.onestopclick.model.repository.AuthRepository;
import com.example.mitrais.onestopclick.model.repository.ProfileRepository;
import com.example.mitrais.onestopclick.model.repository.StorageRepository;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;

import javax.inject.Inject;

public class AddProfileViewModel extends AndroidViewModel {
    @Inject
    StorageRepository storageRepo;

    @Inject
    ProfileRepository profileRepo;

    @Inject
    AuthRepository authRepo;

    public AddProfileViewModel(@NonNull Application application) {
        super(application);
        initDagger(application);
    }

    FirebaseUser getUser() {
        return authRepo.getUser();
    }

    Task<Uri> uploadProfileImage(Uri uri, String filename) {
        return storageRepo.uploadProfileImage(uri, filename);
    }

    Task<Void> saveProfile(Profile profile) {
        return profileRepo.saveProfile(profile);
    }

    Task<Void> saveProfileImageUri(Profile profile) {
        return profileRepo.saveImageUri(profile);
    }

    private void initDagger(Application application) {
        ViewModelComponent component = DaggerViewModelComponent.builder()
                .application(application)
                .build();
        component.inject(this);
    }
}
