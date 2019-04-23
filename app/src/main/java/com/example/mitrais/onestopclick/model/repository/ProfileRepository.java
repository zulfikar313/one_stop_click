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

/**
 * ProfileRepository class provide access to ProfileDao and ProfileService
 */
public class ProfileRepository {
    private static final String TAG = "ProfileRepository";
    private static ListenerRegistration profileListenerRegistration;

    @Inject
    ProfileDao profileDao;

    @Inject
    ProfileService profileService;

    /**
     * ProfileRepository constructor
     *
     * @param application application for dao injection
     */
    public ProfileRepository(Application application) {
        initDagger(application);
    }

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

    // region room

    /**
     * insert profile to local database
     *
     * @param profile user profile
     */
    private void insertProfile(Profile profile) {
        new InsertProfileAsyncTask(profileDao).execute(profile);
    }

    /**
     * update profile in local database
     *
     * @param profile user profile
     */
    public void updateProfile(Profile profile) {
        new UpdateProfileAsyncTask(profileDao).execute(profile);
    }

    /**
     * delete profile in local database
     *
     * @param profile user profile
     */
    private void deleteProfile(Profile profile) {
        new DeleteProfileAsyncTask(profileDao).execute(profile);
    }

    /**
     * delete profile in local database by email
     *
     * @param email user email address
     */
    private void deleteProfileByEmail(String email) {
        new DeleteProfileByEmailAsyncTask(profileDao).execute(email);
    }

    /**
     * returns profile data by email
     *
     * @param email user email address
     * @return profile live data
     */
    public LiveData<Profile> getProfileByEmail(String email) {
        return profileDao.getProfileByEmail(email);
    }

    /**
     * insert profile to local database in background
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
     * update profile in local database in background
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
     * delete profile in local database in background
     */
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

    /**
     * delete profile by email in local database in background
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
     * save profile
     *
     * @param profile user profile
     * @return save profile task
     */
    public Task<Void> saveProfile(Profile profile) {
        addProfileListener(profile.getEmail());
        return profileService.saveProfile(profile);
    }

    /**
     * retrieve profile by email from firestore
     *
     * @param email user email address
     * @return retrieve profile by email task
     */
    public Task<DocumentSnapshot> retrieveProfileByEmail(String email) {
        addProfileListener(email);
        return profileService.retrieveProfileByEmail(email).addOnSuccessListener(documentSnapshot -> {
            Profile profile = documentSnapshot.toObject(Profile.class);
            if (profile != null) {
                profile.setEmail(documentSnapshot.getId());
                insertProfile(profile);
            }
        });
    }

    /**
     * add listener to listen for changes to profile data in firestore
     * only profile data with specific email adress will be listened
     * changes will be reflected to local data
     *
     * @param email user email address
     */
    private void addProfileListener(String email) {
        if (profileListenerRegistration == null) {
            profileListenerRegistration = profileService.retrieveProfileDocRef(email).addSnapshotListener((documentSnapshot, e) -> {
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
