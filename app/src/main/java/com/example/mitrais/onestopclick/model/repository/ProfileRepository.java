package com.example.mitrais.onestopclick.model.repository;

import android.app.Application;
import android.arch.lifecycle.LiveData;
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

import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class ProfileRepository {
    private static final String TAG = "ProfileRepository";
    private ListenerRegistration profileListenerRegistration;

    @Inject
    ProfileDao profileDao;

    @Inject
    ProfileService profileService;

    public ProfileRepository(Application application) {
        initDagger(application);
    }

    // region room
    private void insertProfile(Profile profile) {
        Completable.fromAction(() -> profileDao.insertProfile(profile))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onComplete() {
                        Log.i(TAG, "Insert profile completed");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, e.getMessage());
                    }
                });
    }

    public void updateProfile(Profile profile) {
        Completable.fromAction(() -> profileDao.updateProfile(profile))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onComplete() {
                        Log.i(TAG, "Update profile completed");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, e.getMessage());
                    }
                });
    }

    public void deleteProfile() {
        Completable.fromAction(() -> profileDao.deleteProfile())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onComplete() {
                        Log.i(TAG, "Delete profile complete");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, e.getMessage());
                    }
                });
    }

    public LiveData<Profile> getProfile(String email) {
        return profileDao.getProfile(email);
    }
    // endregion

    // region firestore
    public Task<Void> saveProfile(Profile profile) {
        return profileService.saveProfile(profile)
                .addOnSuccessListener(aVoid -> {
                    addProfileListener(profile.getEmail());
                });
    }

    public Task<Void> saveProfileImageUri(Profile profile) {
        return profileService.saveProfileImageUri(profile)
                .addOnSuccessListener(aVoid -> {
                    addProfileListener(profile.getEmail());
                });
    }

    public Task<DocumentSnapshot> syncProfile(String email) {
        return profileService.syncProfile(email)
                .addOnSuccessListener(documentSnapshot -> {
                    Profile profile = documentSnapshot.toObject(Profile.class);
                    if (profile != null) {
                        profile.setEmail(documentSnapshot.getId());
                        insertProfile(profile);
                        addProfileListener(email);
                    }
                });
    }

    /**
     * listen to profile data changes in firestore
     *
     * @param email user email address
     */
    private void addProfileListener(String email) {
        if (profileListenerRegistration != null)
            profileListenerRegistration.remove();

        profileListenerRegistration = profileService.getProfileRef(email)
                .addSnapshotListener((documentSnapshot, e) -> {
                    Log.i(TAG, "addProfileListener:called");
                    if (e != null)
                        Log.e(TAG, e.getMessage());
                    else {
                        Profile profile = documentSnapshot.toObject(Profile.class);
                        if (profile != null) {
                            profile.setEmail(documentSnapshot.getId());
                            insertProfile(profile);
                        }
                    }
                });
    }
    // endregion

    private void initDagger(Application application) {
        ProfileRepositoryComponent component = DaggerProfileRepositoryComponent.builder()
                .application(application)
                .build();
        component.inject(this);
    }
}
