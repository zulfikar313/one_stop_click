package com.example.mitrais.onestopclick.view;

import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.mitrais.onestopclick.Constant;
import com.example.mitrais.onestopclick.R;
import com.example.mitrais.onestopclick.dagger.component.DaggerRegistrationActivityComponent;
import com.example.mitrais.onestopclick.dagger.component.RegistrationActivityComponent;
import com.example.mitrais.onestopclick.view.dialog.CheckEmailForVerificationDialog;
import com.example.mitrais.onestopclick.viewmodel.RegistrationViewModel;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;
import maes.tech.intentanim.CustomIntent;

public class RegistrationActivity extends AppCompatActivity implements RegistrationViewModel.ResultListener {
    private static final String TAG = "RegistrationActivity";
    private FirebaseUser user;
    private Task<AuthResult> registerTask;
    private Task<Void> sendVerificationEmailTask;

    @Inject
    RegistrationViewModel viewModel;

    @BindView(R.id.txt_email)
    TextInputLayout txtEmail;

    @BindView(R.id.txt_password)
    TextInputLayout txtPassword;

    @BindView(R.id.txt_confirm_password)
    TextInputLayout txtConfirmPassword;

    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        setTitle(getString(R.string.registration));
        ButterKnife.bind(this);

        // Initialize dagger injection
        RegistrationActivityComponent component = DaggerRegistrationActivityComponent.builder()
                .registrationActivity(this)
                .build();
        component.inject(this);
    }

    @Override
    public void finish() {
        super.finish();
        CustomIntent.customType(this, Constant.ANIMATION_FADEIN_TO_FADEOUT);
    }

    @Override
    protected void onStart() {
        super.onStart();
        user = viewModel.getCurrentUser();
        if (user != null && user.isEmailVerified())
            goToMainPage();
    }

    @OnClick(R.id.btn_register)
    void onRegisterButtonClicked() {
        if (isEmailValid() & isPasswordValid() & isConfirmPasswordValid()) {
            if (isRegistrationInProgress())
                Toasty.info(this, getString(R.string.registration_is_in_progress), Toast.LENGTH_SHORT).show();
            else
                doRegistration();
        }
    }

    @OnClick(R.id.txt_login)
    void onLoginTextClicked() {
        goToLoginPage();
    }

    @Override
    public void onLoginSuccess() {
        user = viewModel.getCurrentUser();
        if (user != null && user.isEmailVerified())
            goToMainPage();
        else
            Toasty.error(this, getString(R.string.error_unverified_email), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onLoginFailed(Exception e) {
        Toasty.error(this, e.getMessage(), Toast.LENGTH_LONG).show();
    }

    // region private methods
    // Register new user using email and password
    private void doRegistration() {
        progressBar.setVisibility(View.VISIBLE);
        String email = txtEmail.getEditText().getText().toString().trim();
        String password = txtPassword.getEditText().getText().toString().trim();
        registerTask = viewModel.register(email, password)
                .addOnCompleteListener(task -> progressBar.setVisibility(View.INVISIBLE))
                .addOnSuccessListener(authResult -> {
                    user = viewModel.getCurrentUser();
                    sendVerificationEmail(user);
                })
                .addOnFailureListener(e -> {
                    Toasty.error(this, e.getMessage(), Toast.LENGTH_LONG).show();
                    Log.e(TAG, e.toString());
                });
    }

    // return true if email valid
    private boolean isEmailValid() {
        String email = txtEmail.getEditText().getText().toString().trim();
        if (email.isEmpty()) {
            txtEmail.setError(getString(R.string.error_empty_email));
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            txtEmail.setError(getString(R.string.error_invalid_email));
            return false;
        }
        txtEmail.setError("");
        return true;
    }

    // return true if password valid
    private boolean isPasswordValid() {
        String password = txtPassword.getEditText().getText().toString().trim();
        if (password.isEmpty()) {
            txtPassword.setError(getString(R.string.error_empty_password));
            return false;
        }
        txtPassword.setError("");
        return true;
    }

    // return true if confirm password valid
    private boolean isConfirmPasswordValid() {
        String password = txtPassword.getEditText().getText().toString().trim();
        String confirmPassword = txtConfirmPassword.getEditText().getText().toString().trim();
        if (confirmPassword.isEmpty()) {
            txtConfirmPassword.setError(getString(R.string.error_empty_confirm_password));
            return false;
        } else if (!confirmPassword.equals(password)) {
            txtConfirmPassword.setError(getString(R.string.error_passwords_not_matched));
            return false;
        }
        txtConfirmPassword.setError("");
        return true;
    }

    // show check email for verification dialog
    private void showCheckEmailForVerificationDialog() {
        CheckEmailForVerificationDialog dialog = new CheckEmailForVerificationDialog();
        dialog.setCancelable(false);
        dialog.setListener(this::goToLoginPage);
        dialog.show(getSupportFragmentManager(), TAG);
    }

    // send verification email to user email address
    private void sendVerificationEmail(FirebaseUser user) {
        sendVerificationEmailTask = viewModel.sendVerificationEmail(user)
                .addOnSuccessListener(aVoid -> showCheckEmailForVerificationDialog())
                .addOnFailureListener(e -> Toasty.error(this, e.getMessage(), Toast.LENGTH_LONG).show());
    }

    // go to login page
    private void goToLoginPage() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        CustomIntent.customType(this, Constant.ANIMATION_FADEIN_TO_FADEOUT);
    }

    // go to main page
    private void goToMainPage() {
        finish();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        CustomIntent.customType(this, Constant.ANIMATION_FADEIN_TO_FADEOUT);
    }

    // return true if registration in progress
    private boolean isRegistrationInProgress() {
        return registerTask != null && !registerTask.isComplete();
    }
    //endregion
}
