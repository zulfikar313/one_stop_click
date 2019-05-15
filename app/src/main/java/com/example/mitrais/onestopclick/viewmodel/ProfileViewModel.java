package com.example.mitrais.onestopclick.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
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

public class ProfileViewModel extends AndroidViewModel {
    @Inject
    StorageRepository storageRepository;

    @Inject
    ProfileRepository profileRepository;

    @Inject
    AuthRepository authRepository;

    public ProfileViewModel(@NonNull Application application) {
        super(application);
        initDagger(application);
    }

    // region public methods

    /**
     * @param email user email address
     * @return user profile live data
     */
    public LiveData<Profile> getProfileByEmail(String email) {
        return profileRepository.getProfile(email);
    }

    public FirebaseUser getUser() {
        return authRepository.getUser();
    }

    /**
     * @param uri      profile image uri
     * @param filename profile image filename
     * @return upload profile image task
     */
    public Task<Uri> uploadProfileImage(Uri uri, String filename) {
        return storageRepository.uploadProfileImage(uri, filename);
    }

    /**
     * @param profile profile object
     * @return save profile task
     */
    public Task<Void> saveProfile(Profile profile) {
        return profileRepository.saveProfile(profile);
    }

    /**
     * @param profile user profile
     * @return save profile task
     */
    public Task<Void> saveProfileImageUri(Profile profile) {
        return profileRepository.saveProfileImageUri(profile);
    }

    /**
     * initialize dagger injection
     *
     * @param application for repository injection
     */
    private void initDagger(Application application) {
        ViewModelComponent component = DaggerViewModelComponent.builder()
                .application(application)
                .build();
        component.inject(this);
    }
}
