package com.example.mitrais.onestopclick.view.registration;

import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.mitrais.onestopclick.Constant;
import com.example.mitrais.onestopclick.R;
import com.example.mitrais.onestopclick.dialog.CheckEmailForVerificationDialog;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;
import maes.tech.intentanim.CustomIntent;

public class RegistrationActivity extends AppCompatActivity {
    private static final String TAG = "RegistrationActivity";
    private FirebaseUser user;
    private Task<AuthResult> registerTask;
    private Task<Void> sendEmailTask;

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
        initDagger();
    }

    @Override
    public void finish() {
        super.finish();
        CustomIntent.customType(this, Constant.ANIMATION_FADEIN_TO_FADEOUT);
    }

    @OnClick(R.id.btn_register)
    void onRegisterButtonClicked() {
        if (isEmailValid() & isPasswordValid() & isConfirmPasswordValid()) {
            if (isRegistrationInProgress() || isSendEmailInProgress())
                Toasty.info(this, getString(R.string.registration_is_in_progress), Toast.LENGTH_SHORT).show();
            else {
                String email = txtEmail.getEditText().getText().toString().trim();
                String password = txtPassword.getEditText().getText().toString().trim();
                register(email, password);
            }
        }
    }

    // region private methods

    /**
     * initialize dagger injection
     */
    private void initDagger() {
        RegistrationActivityComponent component = DaggerRegistrationActivityComponent.builder()
                .registrationActivity(this)
                .build();
        component.inject(this);
    }

    /**
     * register new user
     *
     * @param email    user email address
     * @param password user password
     */
    private void register(String email, String password) {
        showProgressBar();
        registerTask = viewModel.register(email, password)
                .addOnCompleteListener(task -> hideProgressBar())
                .addOnSuccessListener(authResult -> {
                    user = viewModel.getUser();
                    sendVerificationEmail(user);
                })
                .addOnFailureListener(e -> Toasty.error(this, e.getMessage(), Toast.LENGTH_LONG).show());
    }

    /**
     * @return true if email valid
     */
    private boolean isEmailValid() {
        String email = txtEmail.getEditText().getText().toString().trim();
        if (email.isEmpty()) {
            txtEmail.setError(getString(R.string.email_cant_be_empty));
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            txtEmail.setError(getString(R.string.email_is_not_valid));
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
     * @return true if confirm password valid
     */
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

    /**
     * show dialog indicating that email has been sent
     * to user email address
     */
    private void showCheckEmailForVerificationDialog() {
        CheckEmailForVerificationDialog dialog = new CheckEmailForVerificationDialog();
        dialog.setCancelable(false);
        dialog.setListener(this::onBackPressed);
        dialog.show(getSupportFragmentManager(), TAG);
    }

    /**
     * send verification email to user email address
     *
     * @param user logged in user
     */
    private void sendVerificationEmail(FirebaseUser user) {
        sendEmailTask = viewModel.sendVerificationEmail(user)
                .addOnSuccessListener(aVoid -> showCheckEmailForVerificationDialog())
                .addOnFailureListener(e -> Toasty.error(this, e.getMessage(), Toast.LENGTH_LONG).show());
    }

    /**
     * @return true if registratino in progress
     */
    private boolean isRegistrationInProgress() {
        return registerTask != null && !registerTask.isComplete();
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
