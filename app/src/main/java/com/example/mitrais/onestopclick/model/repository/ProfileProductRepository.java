package com.example.mitrais.onestopclick.model.repository;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.util.Log;

import com.example.mitrais.onestopclick.dagger.component.DaggerRepositoryComponent;
import com.example.mitrais.onestopclick.dagger.component.RepositoryComponent;
import com.example.mitrais.onestopclick.model.ProfileProduct;
import com.example.mitrais.onestopclick.model.firestore.ProfileProductService;
import com.example.mitrais.onestopclick.model.room.ProfileProductDao;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class ProfileProductRepository {
    private static final String TAG = "ProfileProductRepo";
    private ListenerRegistration listenerRegistration;

    @Inject
    ProfileProductDao profileProductDao;

    @Inject
    ProfileProductService profileProductService;

    public ProfileProductRepository(Application application) {
        initDagger(application);
        if (listenerRegistration == null)
            addListener();
    }

    // region room
    public LiveData<List<ProfileProduct>> getAll() {
        return profileProductDao.getAll();
    }

    private void insert(ProfileProduct profileProduct) {
        Completable.fromAction(() -> profileProductDao.insert(profileProduct))
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

    private void delete(ProfileProduct profileProduct) {
        Completable.fromAction(() -> profileProductDao.delete(profileProduct))
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

    public void deleteAll() {
        Completable.fromAction(() -> profileProductDao.deleteAll())
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
    // endregion

    // region firestore
    public Task<Void> save(ProfileProduct profileProduct) {
        return profileProductService.save(profileProduct);
    }

    public Task<QuerySnapshot> sync() {
        return profileProductService.sync();
    }

    private void addListener() {
        if (listenerRegistration == null) {
            listenerRegistration = profileProductService.getReference().addSnapshotListener((queryDocumentSnapshots, e) -> {
                if (e != null) {
                    Log.e(TAG, e.getMessage());
                } else if (queryDocumentSnapshots != null) {
                    for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()) {
                        DocumentSnapshot documentSnapshot = dc.getDocument();
                        ProfileProduct profileProduct = documentSnapshot.toObject(ProfileProduct.class);
                        switch (dc.getType()) {
                            case ADDED:
                                insert(profileProduct);
                                break;
                            case MODIFIED:
                                insert(profileProduct);
                                break;
                            case REMOVED:
                                delete(profileProduct);
                                break;
                        }
                    }
                }
            });
        }
    }
    // endregion

    private void initDagger(Application application) {
        RepositoryComponent component = DaggerRepositoryComponent.builder()
                .application(application)
                .build();
        component.inject(this);
    }
}
