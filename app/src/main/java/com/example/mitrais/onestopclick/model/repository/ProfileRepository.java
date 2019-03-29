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
        // initialize dagger injection
        ProfileRepositoryComponent component = DaggerProfileRepositoryComponent.builder()
                .application(application)
                .build();
        component.inject(this);

    }

    // region room
    // insert local profile
    private void insertLocalProfile(Profile profile) {
        new InsertLocalProfileAsyncTask(profileDao).execute(profile);
    }

    // update local profile
    public void updateLocalProfile(Profile profile) {
        new UpdateLocalProfileAsyncTask(profileDao).execute(profile);
    }

    // delete local profile
    private void deleteLocalProfile(Profile profile) {
        new DeleteLocalProfileAsyncTask(profileDao).execute(profile);
    }

    // delete local profile by email
    private void deleteLocalProfileByEmail(String email) {
        new DeleteLocalProfileByEmailAsyncTask(profileDao).execute(email);
    }

    // get local profile by email
    public LiveData<Profile> getLocalProfileByEmail(String email) {
        return profileDao.getProfileByEmail(email);
    }

    // insert profile in background
    static class InsertLocalProfileAsyncTask extends AsyncTask<Profile, Void, Void> {
        private final ProfileDao profileDao;

        InsertLocalProfileAsyncTask(ProfileDao profileDao) {
            this.profileDao = profileDao;
        }

        @Override
        protected Void doInBackground(Profile... profiles) {
            profileDao.insertProfile(profiles[0]);
            return null;
        }
    }

    // update profile in background
    static class UpdateLocalProfileAsyncTask extends AsyncTask<Profile, Void, Void> {
        private final ProfileDao profileDao;

        UpdateLocalProfileAsyncTask(ProfileDao profileDao) {
            this.profileDao = profileDao;
        }

        @Override
        protected Void doInBackground(Profile... profiles) {
            profileDao.updateProfile(profiles[0]);
            return null;
        }
    }

    // delete profile in background
    static class DeleteLocalProfileAsyncTask extends AsyncTask<Profile, Void, Void> {
        private final ProfileDao profileDao;

        DeleteLocalProfileAsyncTask(ProfileDao profileDao) {
            this.profileDao = profileDao;
        }

        @Override
        protected Void doInBackground(Profile... profiles) {
            profileDao.deleteProfile(profiles[0]);
            return null;
        }
    }

    // delete local profile by email in background
    static class DeleteLocalProfileByEmailAsyncTask extends AsyncTask<String, Void, Void> {
        private final ProfileDao profileDao;

        DeleteLocalProfileByEmailAsyncTask(ProfileDao profileDao) {
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

    // delete profile in firestore
    public Task<Void> deleteProfile(Profile profile) {
        addProfileListener(profile.getEmail());
        return profileService.deleteProfile(profile);
    }

    // get profile by email from firestore
    public Task<DocumentSnapshot> getProfileByEmail(String email) {
        addProfileListener(email);
        return profileService.getProfileByEmail(email).addOnSuccessListener(documentSnapshot -> {
            Profile profile = documentSnapshot.toObject(Profile.class);
            if (profile != null) {
                profile.setEmail(documentSnapshot.getId());
                insertLocalProfile(profile);
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
                        insertLocalProfile(profile);
                    } else
                        deleteLocalProfileByEmail(email);
                }
            });
        }
    }
    // endregion
}
