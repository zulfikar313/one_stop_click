package com.example.mitrais.onestopclick.model.repository;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.util.Log;

import com.example.mitrais.onestopclick.dagger.component.DaggerRepositoryComponent;
import com.example.mitrais.onestopclick.dagger.component.RepositoryComponent;
import com.example.mitrais.onestopclick.model.ProfileProduct;
import com.example.mitrais.onestopclick.model.room.ProfileProductDao;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class ProfileProductRepository {
    private static final String TAG = "ProfileProductRepo";
    @Inject
    ProfileProductDao profileProductDao;

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
