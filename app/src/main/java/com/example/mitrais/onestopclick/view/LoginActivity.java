package com.example.mitrais.onestopclick.view;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.mitrais.onestopclick.App;
import com.example.mitrais.onestopclick.Constant;
import com.example.mitrais.onestopclick.R;
import com.example.mitrais.onestopclick.dagger.component.DaggerLoginActivityComponent;
import com.example.mitrais.onestopclick.dagger.component.LoginActivityComponent;
import com.example.mitrais.onestopclick.view.dialog.EmailNotVerifiedDialog;
import com.example.mitrais.onestopclick.viewmodel.LoginViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;
import maes.tech.intentanim.CustomIntent;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private FirebaseUser user;
    private Task<AuthResult> loginTask;
    private Task<Void> sendVerificationEmailTask;
    private Task<DocumentSnapshot> syncDataTask;

    @Inject
    LoginViewModel viewModel;

    @BindView(R.id.txt_email)
    TextInputLayout txtEmail;

    @BindView(R.id.txt_password)
    TextInputLayout txtPassword;

    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setTitle(getString(R.string.login));
        ButterKnife.bind(this);

        // Initialize dagger
        LoginActivityComponent component = DaggerLoginActivityComponent.builder()
                .loginActivity(this)
                .build();
        component.inject(this);

        // Set last logged in email value if any
        txtEmail.getEditText().setText(App.prefs.getString(Constant.PREF_LAST_LOGGED_IN_EMAIL, ""));
    }

    @Override
    protected void onStart() {
        super.onStart();
        user = viewModel.getCurrentUser();
        if (user != null && user.isEmailVerified())
            goToMainPage();
    }

    @Override
    public void finish() {
        super.finish();
        CustomIntent.customType(this, Constant.ANIMATION_FADEIN_TO_FADEOUT);
    }

    @OnClick(R.id.btn_login)
    void onLoginButtonClicked() {
        if (isEmailValid() & isPasswordValid()) {
            if (isLoginInProgress() || isSyncDataInProgress()) {
                Toasty.info(this, getString(R.string.login_process_is_running), Toast.LENGTH_SHORT).show();
            } else
                doLogin();
        }
    }

    @OnClick(R.id.txt_register)
    void onRegisterTextClicked() {
        goToRegistrationPage();
    }

    @OnClick(R.id.txt_forgot_password)
    void onForgotPasswordTextClicked() {
        goToForgotPasswordPage();
    }

    // region private methods
    // login using email and password
    private void doLogin() {
        progressBar.setVisibility(View.VISIBLE);
        String email = txtEmail.getEditText().getText().toString().trim();
        String password = txtPassword.getEditText().getText().toString().trim();

        loginTask = viewModel.login(email, password)
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.INVISIBLE);
                })
                .addOnSuccessListener(authResult -> {
                    user = viewModel.getCurrentUser();
                    if (!user.isEmailVerified()) {
                        showEmailNotVerifiedDialog();
                    } else {
                        App.prefs.putBoolean(Constant.PREF_IS_LOGGED_ON_ONCE, true);
                        App.prefs.putString(Constant.PREF_LAST_LOGGED_IN_EMAIL, email);
                        goToMainPage();
                    }
                })
                .addOnFailureListener(e -> {
                    Toasty.error(this, e.getMessage(), Toasty.LENGTH_LONG).show();
                });
    }

    // send verification email
    private void sendVerificationEmail(FirebaseUser user) {
        progressBar.setVisibility(View.VISIBLE);
        sendVerificationEmailTask = viewModel.sendVerificationEmail(user)
                .addOnCompleteListener(task -> progressBar.setVisibility(View.INVISIBLE))
                .addOnSuccessListener(aVoid -> Toasty.success(this, getString(R.string.message_verification_email_sent), Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toasty.error(this, e.getMessage(), Toast.LENGTH_LONG).show());
    }

    // go to registration page
    private void goToRegistrationPage() {
        Intent intent = new Intent(this, RegistrationActivity.class);
        startActivity(intent);
        CustomIntent.customType(this, Constant.ANIMATION_FADEIN_TO_FADEOUT);
    }

    // go to main page
    private void goToMainPage() {
        progressBar.setVisibility(View.VISIBLE);
        syncDataTask = viewModel.syncData(user)
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.INVISIBLE);
                    finish();
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    CustomIntent.customType(LoginActivity.this, Constant.ANIMATION_FADEIN_TO_FADEOUT);
                });
    }

    // go to forgot password page
    private void goToForgotPasswordPage() {
        Intent intent = new Intent(this, ForgotPasswordActivity.class);
        startActivity(intent);
        CustomIntent.customType(this, Constant.ANIMATION_FADEIN_TO_FADEOUT);
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

    // show email not verified dialog
    private void showEmailNotVerifiedDialog() {
        EmailNotVerifiedDialog dialog = new EmailNotVerifiedDialog();
        dialog.setCancelable(false);
        dialog.setListener(() -> {
            if (isSendVerificationEmailInProgress())
                Toasty.info(this, getString(R.string.send_verification_email_is_in_progress), Toast.LENGTH_SHORT).show();
            else
                sendVerificationEmail(user);

        });
        dialog.show(getSupportFragmentManager(), TAG);
    }

    // return true if login in progress
    private boolean isLoginInProgress() {
        return loginTask != null && !loginTask.isComplete();
    }

    // return true if sync data in progress
    private boolean isSyncDataInProgress() {
        return syncDataTask != null && !syncDataTask.isComplete();
    }

    // return true if send verification email in progress
    private boolean isSendVerificationEmailInProgress() {
        return sendVerificationEmailTask != null && !sendVerificationEmailTask.isComplete();
    }
    //endregion
}
