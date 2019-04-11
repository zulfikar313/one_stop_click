package com.example.mitrais.onestopclick.model.repository;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;
import android.util.Log;

import com.example.mitrais.onestopclick.dagger.component.DaggerProfileRepositoryComponent;
import com.example.mitrais.onestopclick.dagger.component.ProfileRepositoryComponent;
import com.example.mitrais.onestopclick.model.Profile;
import com.example.mitrais.onestopclick.model.firestore.ProfileService;
import com.example.mitrais.onestopclick.model.room.ProfileDao;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.ListenerRegistration;

import javax.inject.Inject;

public class ProfileRepository {
    private static final String TAG = "ProfileRepository";
    private static ListenerRegistration profileListenerRegistration;

    @Inject
    ProfileDao profileDao;

    @Inject
    ProfileService profileService;

    public ProfileRepository(Application application) {
        initDagger(application);
    }

    // initialize dagger injection
    private void initDagger(Application application) {
        ProfileRepositoryComponent component = DaggerProfileRepositoryComponent.builder()
                .application(application)
                .build();
        component.inject(this);
    }

    // region room
    // insert local profile
    private void insertProfile(Profile profile) {
        new InsertProfileAsyncTask(profileDao).execute(profile);
    }

    // update profile
    public void updateProfile(Profile profile) {
        new UpdateProfileAsyncTask(profileDao).execute(profile);
    }

    // delete profile
    private void deleteProfile(Profile profile) {
        new DeleteProfileAsyncTask(profileDao).execute(profile);
    }

    // delete local profile by email
    private void deleteProfileByEmail(String email) {
        new DeleteProfileByEmailAsyncTask(profileDao).execute(email);
    }

    // retrieve local profile by email
    public LiveData<Profile> retrieveProfileByEmail(String email) {
        return profileDao.getProfileByEmail(email);
    }

    // insert profile in background
    static class InsertProfileAsyncTask extends AsyncTask<Profile, Void, Void> {
        private final ProfileDao profileDao;

        InsertProfileAsyncTask(ProfileDao profileDao) {
            this.profileDao = profileDao;
        }

        @Override
        protected Void doInBackground(Profile... profiles) {
            profileDao.insertProfile(profiles[0]);
            return null;
        }
    }

    // update profile in background
    static class UpdateProfileAsyncTask extends AsyncTask<Profile, Void, Void> {
        private final ProfileDao profileDao;

        UpdateProfileAsyncTask(ProfileDao profileDao) {
            this.profileDao = profileDao;
        }

        @Override
        protected Void doInBackground(Profile... profiles) {
            profileDao.updateProfile(profiles[0]);
            return null;
        }
    }

    // delete profile in background
    static class DeleteProfileAsyncTask extends AsyncTask<Profile, Void, Void> {
        private final ProfileDao profileDao;

        DeleteProfileAsyncTask(ProfileDao profileDao) {
            this.profileDao = profileDao;
        }

        @Override
        protected Void doInBackground(Profile... profiles) {
            profileDao.deleteProfile(profiles[0]);
            return null;
        }
    }

    // delete local profile by email in background
    static class DeleteProfileByEmailAsyncTask extends AsyncTask<String, Void, Void> {
        private final ProfileDao profileDao;

        DeleteProfileByEmailAsyncTask(ProfileDao profileDao) {
            this.profileDao = profileDao;
        }

        @Override
        protected Void doInBackground(String... strings) {
            profileDao.deleteProfileByEmail(strings[0]);
            return null;
        }
    }
    // endregion

    // region firestore
    // add profile
    public Task<Void> addProfile(Profile profile) {
        addProfileListener(profile.getEmail());
        return profileService.addProfile(profile);
    }

    // save existing profile to firestore
    public Task<Void> saveProfile(Profile profile) {
        addProfileListener(profile.getEmail());
        return profileService.saveProfile(profile);
    }

    // save existing profile to firestore
    public Task<Void> saveProfileImageData(Profile profile) {
        addProfileListener(profile.getEmail());
        return profileService.saveProfileImageData(profile);
    }

    // get profile by email from firestore
    public Task<DocumentSnapshot> getProfileByEmail(String email) {
        addProfileListener(email);
        return profileService.getProfileByEmail(email).addOnSuccessListener(documentSnapshot -> {
            Profile profile = documentSnapshot.toObject(Profile.class);
            if (profile != null) {
                profile.setEmail(documentSnapshot.getId());
                insertProfile(profile);
            }
        });
    }

    // listen to change in profile
    private void addProfileListener(String email) {
        if (profileListenerRegistration == null) {
            profileListenerRegistration = profileService.getProfileDocumentReference(email).addSnapshotListener((documentSnapshot, e) -> {
                if (e != null)
                    Log.e(TAG, e.getMessage());
                else {
                    Profile profile = documentSnapshot.toObject(Profile.class);
                    if (profile != null) {
                        profile.setEmail(documentSnapshot.getId());
                        insertProfile(profile);
                    } else
                        deleteProfileByEmail(email);
                }
            });
        }
    }
    // endregion
}