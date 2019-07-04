package com.example.mitrais.onestopclick.view.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.mitrais.onestopclick.App;
import com.example.mitrais.onestopclick.Constant;
import com.example.mitrais.onestopclick.R;
import com.example.mitrais.onestopclick.model.Profile;
import com.example.mitrais.onestopclick.view.add_profile.AddProfileActivity;
import com.example.mitrais.onestopclick.view.forgot_password.ForgotPasswordActivity;
import com.example.mitrais.onestopclick.view.main.MainActivity;
import com.example.mitrais.onestopclick.view.registration.RegistrationActivity;
import com.example.mitrais.onestopclick.dialog.EmailNotVerifiedDialog;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
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
    private static final int REQUEST_GOOGLE_ACOUNT = 1;
    private GoogleSignInClient googleSignInClient;
    private FirebaseUser user;
    private Task<AuthResult> loginTask;
    private Task<Void> sendEmailTask;
    private Task<DocumentSnapshot> syncProfileTask;

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
        initGoogleSigninClient();

        // set remember me based on preferences
        cbRememberMe.setChecked(App.prefs.getBoolean(Constant.PREF_IS_REMEMBER_ME_ENABLED));

        // set last logged in email if any
        txtEmail.getEditText().setText(App.prefs.getString(Constant.PREF_LAST_LOGGED_IN_EMAIL));
    }

    @OnClick(R.id.btn_login)
    void onLoginButtonClicked() {
        if (isEmailValid() & isPasswordValid()) {
            if (isLoginInProgress() || isSyncDataInProgress()) {
                Toasty.info(this, getString(R.string.login_in_progress), Toast.LENGTH_SHORT).show();
            } else {
                login();
            }
        }
    }

    @OnClick(R.id.btn_google_sign_in)
    void onGoogleSignInButtonClicked() {
        if (isLoginInProgress() || isSyncDataInProgress()) {
            Toasty.info(this, getString(R.string.login_in_progress), Toast.LENGTH_SHORT).show();
        } else {
            openGoogleAccountChooser();
        }
    }

    @OnClick(R.id.txt_register)
    void onRegisterTextClicked() {
        if (isLoginInProgress() || isSyncDataInProgress()) {
            Toasty.info(this, getString(R.string.login_in_progress), Toast.LENGTH_SHORT).show();
        } else
            goToRegistrationPage();
    }

    @OnClick(R.id.txt_forgot_password)
    void onForgotPasswordTextClicked() {
        if (isLoginInProgress() || isSyncDataInProgress()) {
            Toasty.info(this, getString(R.string.login_in_progress), Toast.LENGTH_SHORT).show();
        } else
            goToForgotPasswordPage();
    }

    @OnClick(R.id.cb_remember_me)
    void onRememberMeCheckboxClicked() {
        App.prefs.putBoolean(Constant.PREF_IS_REMEMBER_ME_ENABLED, cbRememberMe.isChecked());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUEST_GOOGLE_ACOUNT && data != null) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                googleSignIn(account);
            } catch (ApiException e) {
                Log.e(TAG, e.getMessage());
                Toasty.error(this, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        } else {
            Toasty.error(this, getString(R.string.unknown_error), Toast.LENGTH_LONG).show();
        }
    }

    // region private methods
    private void initDagger() {
        LoginActivityComponent component = DaggerLoginActivityComponent.builder()
                .loginActivity(this)
                .build();
        component.inject(this);
    }

    private void initGoogleSigninClient() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(Constant.WEB_CLIENT_ID)
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void googleSignIn(GoogleSignInAccount account) {
        showProgressBar();
        loginTask = viewModel.googleSignIn(account)
                .addOnCompleteListener(task -> hideProgressBar())
                .addOnSuccessListener(task -> {
                    user = viewModel.getUser();
                    goToMainPage();
                })
                .addOnFailureListener(e -> Toasty.error(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void login() {
        showProgressBar();

        String email = txtEmail.getEditText().getText().toString().trim();
        String password = txtPassword.getEditText().getText().toString().trim();

        loginTask = viewModel.login(email, password)
                .addOnCompleteListener(task -> hideProgressBar())
                .addOnSuccessListener(authResult -> {
                    user = viewModel.getUser();
                    if (!user.isEmailVerified()) {
                        showEmailNotVerifiedDialog();
                    } else {
                        // saveProduct last logged in email if remember me enabled
                        if (App.prefs.getBoolean(Constant.PREF_IS_REMEMBER_ME_ENABLED))
                            App.prefs.putString(Constant.PREF_LAST_LOGGED_IN_EMAIL, email);
                        else
                            App.prefs.putString(Constant.PREF_LAST_LOGGED_IN_EMAIL, "");

                        goToMainPage();
                    }
                })
                .addOnFailureListener(e -> Toasty.error(this, e.getMessage(), Toasty.LENGTH_SHORT).show());
    }

    private void sendVerificationEmail(FirebaseUser user) {
        showProgressBar();
        sendEmailTask = viewModel.sendVerificationEmail(user)
                .addOnCompleteListener(task -> hideProgressBar())
                .addOnSuccessListener(aVoid -> Toasty.success(this, getString(R.string.message_verification_email_sent), Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toasty.error(this, e.getMessage(), Toast.LENGTH_LONG).show());
    }

    private void goToRegistrationPage() {
        startActivity(new Intent(this, RegistrationActivity.class));
        CustomIntent.customType(this, Constant.ANIMATION_FADEIN_TO_FADEOUT);
    }

    private void goToMainPage() {
        showProgressBar();
        syncProfileTask = viewModel.syncProfile(user)
                .addOnSuccessListener(documentSnapshot -> {
                    hideProgressBar();
                    Profile profile = documentSnapshot.toObject(Profile.class);
                    if (profile != null) {
                        // go to main page
                        finish();
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        CustomIntent.customType(LoginActivity.this, Constant.ANIMATION_FADEIN_TO_FADEOUT);
                    } else {
                        // go to addProduct profile page
                        finish();
                        Intent intent = new Intent(LoginActivity.this, AddProfileActivity.class);
                        startActivity(intent);
                        CustomIntent.customType(LoginActivity.this, Constant.ANIMATION_FADEIN_TO_FADEOUT);
                    }
                });
    }

    private void goToForgotPasswordPage() {
        startActivity(new Intent(this, ForgotPasswordActivity.class));
        CustomIntent.customType(this, Constant.ANIMATION_FADEIN_TO_FADEOUT);
    }

    private void openGoogleAccountChooser() {
        googleSignInClient.signOut().addOnCompleteListener(task -> {
            Intent intent = googleSignInClient.getSignInIntent();
            startActivityForResult(intent, REQUEST_GOOGLE_ACOUNT);
        });
    }

    private boolean isEmailValid() {
        String email = txtEmail.getEditText().getText().toString().trim();
        if (email.isEmpty()) {
            txtEmail.setError(getString(R.string.email_cant_be_empty));
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            txtEmail.setError(getString(R.string.email_not_valid));
            return false;
        }
        txtEmail.setError("");
        return true;
    }

    private boolean isPasswordValid() {
        String password = txtPassword.getEditText().getText().toString().trim();
        if (password.isEmpty()) {
            txtPassword.setError(getString(R.string.password_cant_be_empty));
            return false;
        }
        txtPassword.setError("");
        return true;
    }

    private void showEmailNotVerifiedDialog() {
        EmailNotVerifiedDialog dialog = new EmailNotVerifiedDialog();
        dialog.setCancelable(false);
        dialog.setListener(() -> {
            if (isSendEmailInProgress())
                Toasty.info(this, getString(R.string.send_verification_email_in_progress), Toast.LENGTH_SHORT).show();
            else
                sendVerificationEmail(user);

        });
        dialog.show(getSupportFragmentManager(), TAG);
    }

    private boolean isLoginInProgress() {
        return loginTask != null && !loginTask.isComplete();
    }

    private boolean isSyncDataInProgress() {
        return syncProfileTask != null && !syncProfileTask.isComplete();
    }

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
