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
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

import javax.annotation.Nullable;
import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class ProfileProductRepository {
    private static final String TAG = "ProfileProductRepo";
    private ListenerRegistration profileProductListenerRegistration;

    @Inject
    ProfileProductDao profileProductDao;

    @Inject
    ProfileProductService profileProductService;

    public ProfileProductRepository(Application application) {
        initDagger(application);
    }

    public LiveData<List<ProfileProduct>> getAllProfileProducts() {
        return profileProductDao.getAllProfileProducts();
    }

    public void insertProfileProduct(ProfileProduct profileProduct) {
        Completable.fromAction(() -> profileProductDao.insertProfileProduct(profileProduct))
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

    public void deleteProfileProduct(ProfileProduct profileProduct) {
        Completable.fromAction(() -> profileProductDao.deleteProfileProduct(profileProduct))
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

    public Task<Void> saveProfileProduct(ProfileProduct profileProduct) {
        addProfileProductListener(profileProduct.getEmail());
        return profileProductService.saveProfileProduct(profileProduct);
    }

    public Task<QuerySnapshot> syncProfileProduct(String email) {
        addProfileProductListener(email);
        return profileProductService.syncProfileProduct(email);
    }

    /**
     * listen to profile product change
     *
     * @param email email address
     */
    private void addProfileProductListener(String email) {
        if (profileProductListenerRegistration == null) {
            profileProductListenerRegistration = profileProductService.getProfileProductRef(email).addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                    for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()) {
                        DocumentSnapshot documentSnapshot = dc.getDocument();
                        ProfileProduct profileProduct = documentSnapshot.toObject(ProfileProduct.class);
                        switch (dc.getType()) {
                            case ADDED:
                                insertProfileProduct(profileProduct);
                                break;
                            case MODIFIED:
                                insertProfileProduct(profileProduct);
                                break;
                            case REMOVED:
                                deleteProfileProduct(profileProduct);
                                break;
                        }
                    }
                }
            });
        }
    }

    /**
     * initialize dagger injection
     *
     * @param application for dao injection
     */
    private void initDagger(Application application) {
        RepositoryComponent component = DaggerRepositoryComponent.builder()
                .application(application)
                .build();
        component.inject(this);
    }
}
