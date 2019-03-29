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

        // initialize dagger injection
        SplashActivityComponent component = DaggerSplashActivityComponent.builder()
                .splashActivity(this)
                .build();
        component.inject(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        user = viewModel.getCurrentUser();
        if (user != null && user.isEmailVerified()) // check if user is logged in and verified
            goToMainPage();
        else {
            boolean isLoggedOnOnce = App.prefs.getBoolean(Constant.PREF_IS_LOGGED_ON_ONCE, false);
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

    // go to login page
    private void goToLoginPage() {
        progressBar.setVisibility(View.VISIBLE);
        new Handler().postDelayed(() -> {
            finish();
            Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
            startActivity(intent);
            CustomIntent.customType(SplashActivity.this, Constant.ANIMATION_FADEIN_TO_FADEOUT);
            progressBar.setVisibility(View.INVISIBLE);
        }, Constant.PROGRESS_DELAY);

    }

    // go to main page
    private void goToMainPage() {
        progressBar.setVisibility(View.VISIBLE);
        viewModel.syncData(user)
                .addOnCompleteListener(task -> {
                    finish();
                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(intent);
                    CustomIntent.customType(SplashActivity.this, Constant.ANIMATION_FADEIN_TO_FADEOUT);
                    progressBar.setVisibility(View.INVISIBLE);
                });
    }

    // go to registration page
    private void goToRegistrationPage() {
        progressBar.setVisibility(View.VISIBLE);
        new Handler().postDelayed(() -> {
            finish();
            Intent intent = new Intent(SplashActivity.this, RegistrationActivity.class);
            startActivity(intent);
            CustomIntent.customType(SplashActivity.this, Constant.ANIMATION_FADEIN_TO_FADEOUT);
            progressBar.setVisibility(View.INVISIBLE);
        }, Constant.PROGRESS_DELAY);
    }
}
