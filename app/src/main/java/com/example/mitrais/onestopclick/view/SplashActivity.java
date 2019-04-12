package com.example.mitrais.onestopclick.view;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import com.example.mitrais.onestopclick.App;
import com.example.mitrais.onestopclick.Constant;
import com.example.mitrais.onestopclick.R;
import com.example.mitrais.onestopclick.dagger.component.DaggerSplashActivityComponent;
import com.example.mitrais.onestopclick.dagger.component.SplashActivityComponent;
import com.example.mitrais.onestopclick.viewmodel.SplashViewModel;
import com.google.firebase.auth.FirebaseUser;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import maes.tech.intentanim.CustomIntent;

/**
 * SplashActivity handle splash page logic
 */
public class SplashActivity extends AppCompatActivity {
    private FirebaseUser user;

    @Inject
    SplashViewModel viewModel;

    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);
        initDagger();
    }

    @Override
    protected void onStart() {
        super.onStart();
        user = viewModel.getUser();

        /* handle auto login */
        if (user != null && user.isEmailVerified())
            goToMainPage();
        else {
            boolean isLoggedOnOnce = App.prefs.getBoolean(Constant.PREF_IS_LOGGED_ON_ONCE);
            if (isLoggedOnOnce)
                goToLoginPage();
            else
                goToRegistrationPage();
        }
    }

    @Override
    public void finish() {
        super.finish();
        CustomIntent.customType(this, Constant.ANIMATION_FADEIN_TO_FADEOUT);
    }

    /**
     * initialize dagger injection
     */
    private void initDagger() {
        SplashActivityComponent component = DaggerSplashActivityComponent.builder()
                .splashActivity(this)
                .build();
        component.inject(this);
    }

    /**
     * start LoginActivity
     */
    private void goToLoginPage() {
        showProgressBar();
        new Handler().postDelayed(() -> {
            hideProgressBar();

            finish();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            CustomIntent.customType(SplashActivity.this, Constant.ANIMATION_FADEIN_TO_FADEOUT);
        }, Constant.PROGRESS_DELAY);
    }

    /**
     * start MainActivity
     */
    private void goToMainPage() {
        showProgressBar();
        viewModel.syncData(user)
                .addOnCompleteListener(task -> {
                    hideProgressBar();

                    finish();
                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(intent);
                    CustomIntent.customType(SplashActivity.this, Constant.ANIMATION_FADEIN_TO_FADEOUT);
                });
    }

    /**
     * start RegistrationActivity
     */
    private void goToRegistrationPage() {
        showProgressBar();
        new Handler().postDelayed(() -> {
            hideProgressBar();

            finish();
            Intent intent = new Intent(SplashActivity.this, RegistrationActivity.class);
            startActivity(intent);
            CustomIntent.customType(SplashActivity.this, Constant.ANIMATION_FADEIN_TO_FADEOUT);
        }, Constant.PROGRESS_DELAY);
    }

    /**
     * set progress bar visible
     */
    private void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    /**
     * set progress bar invisible
     */
    private void hideProgressBar() {
        progressBar.setVisibility(View.INVISIBLE);
    }
}
