package com.example.mitrais.onestopclick.view.splash;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.mitrais.onestopclick.Constant;
import com.example.mitrais.onestopclick.R;
import com.example.mitrais.onestopclick.model.Profile;
import com.example.mitrais.onestopclick.view.add_profile.AddProfileActivity;
import com.example.mitrais.onestopclick.view.login.LoginActivity;
import com.example.mitrais.onestopclick.view.main.MainActivity;
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
                .addOnSuccessListener(documentSnapshot -> {
                    Profile profile = documentSnapshot.toObject(Profile.class);
                    if (profile != null) {
                        // go to main page
                        finish();
                        Intent intent = new Intent(this, MainActivity.class);
                        startActivity(intent);
                        CustomIntent.customType(this, Constant.ANIMATION_FADEIN_TO_FADEOUT);
                    } else {
                        // go to add profile page
                        finish();
                        Intent intent = new Intent(this, AddProfileActivity.class);
                        startActivity(intent);
                        CustomIntent.customType(this, Constant.ANIMATION_FADEIN_TO_FADEOUT);
                    }
                });
    }

    private void initDagger() {
        SplashActivityComponent component = DaggerSplashActivityComponent.builder()
                .splashActivity(this)
                .build();
        component.inject(this);
    }

}
