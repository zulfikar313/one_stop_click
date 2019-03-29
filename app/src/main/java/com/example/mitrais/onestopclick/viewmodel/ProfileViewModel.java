package com.example.mitrais.onestopclick.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.example.mitrais.onestopclick.dagger.component.DaggerProfileViewModelComponent;
import com.example.mitrais.onestopclick.dagger.component.ProfileViewModelComponent;
import com.example.mitrais.onestopclick.model.Profile;
import com.example.mitrais.onestopclick.model.repository.AuthRepository;
import com.example.mitrais.onestopclick.model.repository.ProfileRepository;
import com.example.mitrais.onestopclick.model.repository.StorageRepository;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;

import javax.inject.Inject;

public class ProfileViewModel extends AndroidViewModel {
    @Inject
    StorageRepository storageRepository;

    @Inject
    ProfileRepository profileRepository;

    @Inject
    AuthRepository authRepository;

    public ProfileViewModel(@NonNull Application application) {
        super(application);

        // initialize dagger injection
        ProfileViewModelComponent component = DaggerProfileViewModelComponent.builder()
                .application(application)
                .build();
        component.inject(this);

        storageRepository = new StorageRepository();
    }

    // get profile by email
    public LiveData<Profile> getProfileByEmail(String email) {
        return profileRepository.getLocalProfileByEmail(email);
    }

    // region firebase
    // get currently logged in user
    public FirebaseUser getUser() {
        return authRepository.getUser();
    }

    // save profile image
    public Task<Uri> saveProfileImage(Uri uri, String fileName) {
        return storageRepository.saveProfileImage(uri, fileName);
    }

    // update user
    public Task<Void> updateUser(String displayName) {
        return authRepository.updateUser(displayName);
    }

    // update user
    public Task<Void> updateUser(Uri photoUri) {
        return authRepository.updateUser(photoUri);
    }

    // add profile
    public Task<Void> addProfile(Profile profile) {
        return profileRepository.addProfile(profile);
    }

    // save profile
    public Task<Void> saveProfile(Profile profile) {
        return profileRepository.saveProfile(profile);
    }

    // save profile image data
    public Task<Void> saveProfileImageData(Profile profile) {
        return profileRepository.saveProfileImageData(profile);
    }
    // endregion
}
