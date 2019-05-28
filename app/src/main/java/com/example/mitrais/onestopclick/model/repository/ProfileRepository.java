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
    private void insert(Profile profile) {
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

    public void delete() {
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

    public LiveData<Profile> getProfile(String email) {
        return profileDao.getProfile(email);
    }
    // endregion

    // region firestore
    public Task<Void> saveProfile(Profile profile) {
        return profileService.save(profile)
                .addOnSuccessListener(aVoid -> {
                    if (listenerRegistration == null)
                        addListener(profile.getEmail());
                });
    }

    public Task<Void> saveImageUri(Profile profile) {
        return profileService.saveImageUri(profile)
                .addOnSuccessListener(aVoid -> {
                    if (listenerRegistration == null)
                        addListener(profile.getEmail());
                });
    }

    public Task<DocumentSnapshot> sync(String email) {
        return profileService.sync(email)
                .addOnSuccessListener(documentSnapshot -> {
                    Profile profile = documentSnapshot.toObject(Profile.class);
                    if (profile != null) {
                        profile.setEmail(documentSnapshot.getId());
                        insert(profile);
                        if (listenerRegistration == null)
                            addListener(email);
                    }
                });
    }

    private void addListener(String email) {
        listenerRegistration = profileService.getProfileRef(email)
                .addSnapshotListener((documentSnapshot, e) -> {
                    if (e != null)
                        Log.e(TAG, e.getMessage());
                    else {
                        if (documentSnapshot != null) {
                            Profile profile = documentSnapshot.toObject(Profile.class);
                            if (profile != null) {
                                profile.setEmail(documentSnapshot.getId());
                                insert(profile);
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
