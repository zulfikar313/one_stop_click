package com.example.mitrais.onestopclick.view;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.mitrais.onestopclick.Constant;
import com.example.mitrais.onestopclick.R;
import com.example.mitrais.onestopclick.dagger.component.DaggerSplashActivityComponent;
import com.example.mitrais.onestopclick.dagger.component.SplashActivityComponent;
import com.example.mitrais.onestopclick.viewmodel.SplashViewModel;
import com.google.firebase.auth.FirebaseUser;

import javax.inject.Inject;

import butterknife.ButterKnife;
import maes.tech.intentanim.CustomIntent;

public class SplashActivity extends AppCompatActivity {
    private FirebaseUser user;

    @Inject
    SplashViewModel viewModel;

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
        if (user != null && user.isEmailVerified())
            goToMainScreen(); /* auto login */
        else {
            goToLoginScreen();
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

    private void goToLoginScreen() {
        new Handler().postDelayed(() -> {
            finish();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            CustomIntent.customType(SplashActivity.this, Constant.ANIMATION_FADEIN_TO_FADEOUT);
        }, Constant.PROGRESS_DELAY);
    }

    private void goToMainScreen() {
        viewModel.syncData(user)
                .addOnCompleteListener(task -> {
                    finish();
                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(intent);
                    CustomIntent.customType(SplashActivity.this, Constant.ANIMATION_FADEIN_TO_FADEOUT);
                });
    }
}
