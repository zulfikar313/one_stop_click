package com.example.mitrais.onestopclick.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Patterns;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.mitrais.onestopclick.App;
import com.example.mitrais.onestopclick.Constant;
import com.example.mitrais.onestopclick.R;
import com.example.mitrais.onestopclick.dagger.component.DaggerLoginActivityComponent;
import com.example.mitrais.onestopclick.dagger.component.LoginActivityComponent;
import com.example.mitrais.onestopclick.view.dialog.EmailNotVerifiedDialog;
import com.example.mitrais.onestopclick.viewmodel.LoginViewModel;
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
    private Task<Void> sendEmailTask;
    private Task<DocumentSnapshot> syncDataTask;

    @Inject
    LoginViewModel viewModel;

    @BindView(R.id.txt_email)
    TextInputLayout txtEmail;

    @BindView(R.id.txt_password)
    TextInputLayout txtPassword;

    @BindView(R.id.cb_remember_me)
    CheckBox cbRememberMe;

    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setTitle(getString(R.string.login));
        ButterKnife.bind(this);
        initDagger();

        /* set remember me based on preferences */
        cbRememberMe.setChecked(App.prefs.getBoolean(Constant.PREF_IS_REMEMBER_ME_ENABLED));

        /* set last logged in email if any */
        txtEmail.getEditText().setText(App.prefs.getString(Constant.PREF_LAST_LOGGED_IN_EMAIL));
    }

    @OnClick(R.id.btn_login)
    void onLoginButtonClicked() {
        if (isEmailValid() & isPasswordValid()) {
            if (isLoginInProgress() || isSyncDataInProgress()) {
                Toasty.info(this, getString(R.string.login_process_is_running), Toast.LENGTH_SHORT).show();
            } else {
                /* do login process */
                String email = txtEmail.getEditText().getText().toString().trim();
                String password = txtPassword.getEditText().getText().toString().trim();
                login(email, password);
            }
        }
    }

    @OnClick(R.id.txt_register)
    void onRegisterTextClicked() {
        if (isLoginInProgress() || isSyncDataInProgress()) {
            Toasty.info(this, getString(R.string.login_process_is_running), Toast.LENGTH_SHORT).show();
        } else
            goToRegistrationScreen();
    }

    @OnClick(R.id.txt_forgot_password)
    void onForgotPasswordTextClicked() {
        if (isLoginInProgress() || isSyncDataInProgress()) {
            Toasty.info(this, getString(R.string.login_process_is_running), Toast.LENGTH_SHORT).show();
        } else
            goToForgotPasswordScreen();
    }

    @OnClick(R.id.cb_remember_me)
    void onRememberMeCheckboxClicked() {
        App.prefs.putBoolean(Constant.PREF_IS_REMEMBER_ME_ENABLED, cbRememberMe.isChecked());
    }

    // region private methods

    /**
     * initialize dagger injection
     */
    private void initDagger() {
        LoginActivityComponent component = DaggerLoginActivityComponent.builder()
                .loginActivity(this)
                .build();
        component.inject(this);
    }

    /**
     * do login process
     *
     * @param email    user email address
     * @param password user password
     */
    private void login(String email, String password) {
        showProgressBar();
        loginTask = viewModel.login(email, password)
                .addOnCompleteListener(task -> hideProgressBar())
                .addOnSuccessListener(authResult -> {
                    user = viewModel.getCurrentUser();
                    if (!user.isEmailVerified()) {
                        showEmailNotVerifiedDialog();
                    } else {
                        /* save last logged in email if remember me enabled */
                        if (App.prefs.getBoolean(Constant.PREF_IS_REMEMBER_ME_ENABLED))
                            App.prefs.putString(Constant.PREF_LAST_LOGGED_IN_EMAIL, email);
                        else
                            App.prefs.putString(Constant.PREF_LAST_LOGGED_IN_EMAIL, "");

                        goToMainScreen();
                    }
                })
                .addOnFailureListener(e -> Toasty.error(this, e.getMessage(), Toasty.LENGTH_SHORT).show());
    }

    /**
     * @param user logged in user
     */
    private void sendVerificationEmail(FirebaseUser user) {
        showProgressBar();
        sendEmailTask = viewModel.sendVerificationEmail(user)
                .addOnCompleteListener(task -> hideProgressBar())
                .addOnSuccessListener(aVoid -> Toasty.success(this, getString(R.string.message_verification_email_sent), Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toasty.error(this, e.getMessage(), Toast.LENGTH_LONG).show());
    }

    private void goToRegistrationScreen() {
        startActivity(new Intent(this, RegistrationActivity.class));
        CustomIntent.customType(this, Constant.ANIMATION_FADEIN_TO_FADEOUT);
    }

    /**
     * sync user data then go to main screen
     */
    private void goToMainScreen() {
        showProgressBar();
        syncDataTask = viewModel.syncData(user)
                .addOnCompleteListener(task -> {
                    hideProgressBar();
                    finish();
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    CustomIntent.customType(LoginActivity.this, Constant.ANIMATION_FADEIN_TO_FADEOUT);
                });
    }

    private void goToForgotPasswordScreen() {
        startActivity(new Intent(this, ForgotPasswordActivity.class));
        CustomIntent.customType(this, Constant.ANIMATION_FADEIN_TO_FADEOUT);
    }

    /**
     * @return true if email valid
     */
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

    /**
     * @return true if password valid
     */
    private boolean isPasswordValid() {
        String password = txtPassword.getEditText().getText().toString().trim();
        if (password.isEmpty()) {
            txtPassword.setError(getString(R.string.error_empty_password));
            return false;
        }
        txtPassword.setError("");
        return true;
    }

    /**
     * show dialog indicating email not verified
     */
    private void showEmailNotVerifiedDialog() {
        EmailNotVerifiedDialog dialog = new EmailNotVerifiedDialog();
        dialog.setCancelable(false);
        dialog.setListener(() -> {
            if (isSendEmailInProgress())
                Toasty.info(this, getString(R.string.send_verification_email_is_in_progress), Toast.LENGTH_SHORT).show();
            else
                sendVerificationEmail(user);

        });
        dialog.show(getSupportFragmentManager(), TAG);
    }

    /**
     * @return true if login in progress
     */
    private boolean isLoginInProgress() {
        return loginTask != null && !loginTask.isComplete();
    }

    /**
     * @return true if sync data in progress
     */
    private boolean isSyncDataInProgress() {
        return syncDataTask != null && !syncDataTask.isComplete();
    }

    /**
     * @return true if send email in progress
     */
    private boolean isSendEmailInProgress() {
        return sendEmailTask != null && !sendEmailTask.isComplete();
    }

    private void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgressBar() {
        progressBar.setVisibility(View.INVISIBLE);
    }
    //endregion
}
