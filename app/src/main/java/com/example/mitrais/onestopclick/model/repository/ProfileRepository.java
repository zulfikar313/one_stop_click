package com.example.mitrais.onestopclick.model.repository;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.example.mitrais.onestopclick.dagger.component.DaggerProfileRepositoryComponent;
import com.example.mitrais.onestopclick.dagger.component.ProfileRepositoryComponent;
import com.example.mitrais.onestopclick.model.Profile;
import com.example.mitrais.onestopclick.model.firestore.ProfileService;
import com.example.mitrais.onestopclick.model.room.ProfileDao;
import com.google.android.gms.tasks.OnSuccessListener;
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

    // region room

    /**
     * @param profile user profile
     */
    private void insertProfile(Profile profile) {
        new InsertProfileAsyncTask(profileDao).execute(profile);
    }

    /**
     * @param profile user profile
     */
    public void updateProfile(Profile profile) {
        new UpdateProfileAsyncTask(profileDao).execute(profile);
    }

    /**
     * delete user profile
     */
    public void deleteProfile() {
        new DeleteProfileAsyncTask(profileDao).execute();
    }

    /**
     * @param email user email address
     * @return profile live data
     */
    public LiveData<Profile> getProfile(String email) {
        return profileDao.getProfile(email);
    }

    /**
     * insert profile to local db in background
     */
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

    /**
     * update profile in local db in background
     */
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

    /**
     * delete profile in local db in background
     */
    static class DeleteProfileAsyncTask extends AsyncTask<Void, Void, Void> {
        private final ProfileDao profileDao;

        DeleteProfileAsyncTask(ProfileDao profileDao) {
            this.profileDao = profileDao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            profileDao.deleteProfile();
            return null;
        }
    }

    /**
     * delete profile by email in local db in background
     */
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

    /**
     * @param profile user profile
     * @return save profile task
     */
    public Task<Void> saveProfile(Profile profile) {
        return profileService.saveProfile(profile)
                .addOnSuccessListener(aVoid -> {
                    addProfileListener(profile.getEmail());
                });
    }

    /**
     * @param email email address
     * @param uri   image uri
     * @return save profile task
     */
    public Task<Void> saveProfileImageUri(String email, Uri uri) {
        return profileService.saveProfileImageUri(email, uri)
                .addOnSuccessListener(aVoid -> {
                    addProfileListener(email);
                });
    }

    /**
     * @param email user email address
     * @return retrieve profile by email task
     */
    public Task<DocumentSnapshot> syncProfile(String email) {
        return profileService.syncProfile(email).addOnSuccessListener(documentSnapshot -> {
            Profile profile = documentSnapshot.toObject(Profile.class);
            if (profile != null) {
                addProfileListener(email);
                profile.setEmail(documentSnapshot.getId());
                insertProfile(profile);
            }
        });
    }

    /**
     * add listener to listen for changes to profile data in firestore
     * only profile data with specific email adress will be listened
     *
     * @param email user email address
     */
    private void addProfileListener(String email) {
        if (profileListenerRegistration == null) {
            profileListenerRegistration = profileService.getProfileRef(email).addSnapshotListener((documentSnapshot, e) -> {
                if (e != null)
                    Log.e(TAG, e.getMessage());
                else {
                    Profile profile = documentSnapshot.toObject(Profile.class);
                    if (profile != null) {
                        profile.setEmail(documentSnapshot.getId());
                        insertProfile(profile);
                    } else
                        deleteProfile();
                }
            });
        }
    }
    // endregion

    /**
     * initialize dagger injection
     *
     * @param application application for dao injection
     */
    private void initDagger(Application application) {
        ProfileRepositoryComponent component = DaggerProfileRepositoryComponent.builder()
                .application(application)
                .build();
        component.inject(this);
    }
}
