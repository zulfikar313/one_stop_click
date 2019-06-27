package com.example.mitrais.onestopclick.model.repository;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.util.Log;

import com.example.mitrais.onestopclick.dagger.component.DaggerRepositoryComponent;
import com.example.mitrais.onestopclick.dagger.component.RepositoryComponent;
import com.example.mitrais.onestopclick.model.Comment;
import com.example.mitrais.onestopclick.model.room.CommentDao;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class CommentRepository {
    private static final String TAG = "CommentRepository";

    @Inject
    CommentDao commentDao;

    public CommentRepository(Application application) {
        initDagger(application);
    }

    private void insert(Comment comment) {
        Completable.fromAction(() -> commentDao.insert(comment))
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

    private void delete(Comment comment) {
        Completable.fromAction(() -> commentDao.delete(comment))
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

    public LiveData<List<Comment>> getByProductId(String productId) {
        return commentDao.getByProductId(productId);
    }

    private void initDagger(Application application) {
        RepositoryComponent component = DaggerRepositoryComponent.builder()
                .application(application)
                .build();
        component.inject(this);
    }
}
