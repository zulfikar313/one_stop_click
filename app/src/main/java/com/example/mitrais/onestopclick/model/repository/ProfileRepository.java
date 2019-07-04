package com.example.mitrais.onestopclick.model.repository;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.util.Log;

import com.example.mitrais.onestopclick.dagger.component.DaggerRepositoryComponent;
import com.example.mitrais.onestopclick.dagger.component.RepositoryComponent;
import com.example.mitrais.onestopclick.model.Profile;
import com.example.mitrais.onestopclick.model.firestore.ProfileService;
import com.example.mitrais.onestopclick.model.room.ProfileDao;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class ProfileRepository {
    private static final String TAG = "ProfileRepository";
    private ListenerRegistration listenerRegistration;

    @Inject
    ProfileDao profileDao;

    @Inject
    ProfileService profileService;

    public ProfileRepository(Application application) {
        initDagger(application);
    }

    // region room
    private void insertProfile(Profile profile) {
        Completable.fromAction(() -> profileDao.insert(profile))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onComplete() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, e.getMessage());
                    }
                });
    }

    // since only one profile matters all profile will be deleted
    // also in case there are undeleted profile data from previous login
    public void deleteProfile() {
        Completable.fromAction(() -> profileDao.delete())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onComplete() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, e.getMessage());
                    }
                });
    }

    public LiveData<Profile> getProfileByEmail(String email) {
        return profileDao.getProfileByEmail(email);
    }
    // endregion

    // region firestore
    public Task<Void> saveProfile(Profile profile) {
        return profileService.saveProfile(profile)
                .addOnSuccessListener(aVoid -> {
                    if (listenerRegistration == null)
                        addProfileListenerListener(profile.getEmail());
                });
    }

    public Task<Void> saveImageUri(Profile profile) {
        return profileService.saveProfileImageUri(profile)
                .addOnSuccessListener(aVoid -> {
                    if (listenerRegistration == null)
                        addProfileListenerListener(profile.getEmail());
                });
    }

    public Task<Void> saveProfileAdminAccess(String email, boolean isAdmin) {
        return profileService.saveProfileAdminAccess(email, isAdmin)
                .addOnSuccessListener(aVoid -> {
                    if (listenerRegistration == null)
                        addProfileListenerListener(email);
                });
    }

    public Task<QuerySnapshot> syncAllProfiles() {
        return profileService.syncAllProfiles()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                        Profile profile = queryDocumentSnapshot.toObject(Profile.class);
                        profile.setEmail(queryDocumentSnapshot.getId());
                        insertProfile(profile);
                    }
                });
    }

    public Task<DocumentSnapshot> syncProfileByEmail(String email) {
        return profileService.syncProfileByEmail(email)
                .addOnSuccessListener(documentSnapshot -> {
                    Profile profile = documentSnapshot.toObject(Profile.class);
                    if (profile != null) {
                        profile.setEmail(documentSnapshot.getId());
                        insertProfile(profile);
                        if (listenerRegistration == null)
                            addProfileListenerListener(email);
                    }
                });
    }

    private void addProfileListenerListener(String email) {
        listenerRegistration = profileService.getProfileReference(email)
                .addSnapshotListener((documentSnapshot, e) -> {
                    if (e != null)
                        Log.e(TAG, e.getMessage());
                    else {
                        if (documentSnapshot != null) {
                            Profile profile = documentSnapshot.toObject(Profile.class);
                            if (profile != null) {
                                profile.setEmail(documentSnapshot.getId());
                                insertProfile(profile);
                                // TODO: add conditinal to handle different kind of changes (ADDED, MODIFIED, REMOVED)
                            }
                        }
                    }
                });
    }
    // endregion

    private void initDagger(Application application) {
        RepositoryComponent component = DaggerRepositoryComponent.builder()
                .application(application)
                .build();
        component.inject(this);
    }
}
