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
import com.example.mitrais.onestopclick.dagger.component.DaggerForgotPasswordActivityComponent;
import com.example.mitrais.onestopclick.dagger.component.ForgotPasswordActivityComponent;
import com.example.mitrais.onestopclick.view.dialog.CheckEmailToResetPasswordDialog;
import com.example.mitrais.onestopclick.viewmodel.ForgotPasswordViewModel;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;
import maes.tech.intentanim.CustomIntent;

public class ForgotPasswordActivity extends AppCompatActivity implements ForgotPasswordViewModel.ResultListener {
    private static final String TAG = "ForgotPasswordActivity";
    private FirebaseUser user;
    private Task<Void> sendPasswordResetEmailTask;

    @Inject
    ForgotPasswordViewModel viewModel;

    @BindView(R.id.txt_email)
    TextInputLayout txtEmail;

    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        setTitle(getString(R.string.forgot_password));
        ButterKnife.bind(this);

        // Initialize dagger injection
        ForgotPasswordActivityComponent component = DaggerForgotPasswordActivityComponent.builder()
                .forgotPasswordActivity(this)
                .build();
        component.inject(this);

        viewModel.setListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        CustomIntent.customType(this, Constant.ANIMATION_FADEIN_TO_FADEOUT);
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }


    @OnClick(R.id.btn_reset_password)
    void onResetPasswordButtonClicked() {
        if (isEmailValid()) {
            if (isSendPasswordResetEmailInProgress())
                Toasty.info(this, getString(R.string.send_reset_password_is_in_progress), Toast.LENGTH_SHORT).show();
            else {
                String email = Objects.requireNonNull(txtEmail.getEditText(), getString(R.string.error_no_edit_text)).getText().toString().trim();
                sendPasswordResetEmail(email);
            }

        }
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
    // go to main page
    private void goToMainPage() {
        finish();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        CustomIntent.customType(this, Constant.ANIMATION_FADEIN_TO_FADEOUT);
    }

    // send password reset email
    private void sendPasswordResetEmail(String email) {
        progressBar.setVisibility(View.VISIBLE);
        sendPasswordResetEmailTask = viewModel.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> progressBar.setVisibility(View.INVISIBLE))
                .addOnSuccessListener(aVoid -> {
                    showCheckEmailToResetPasswordDialog();
                })
                .addOnFailureListener(e -> {
                    Toasty.error(this, e.getMessage(), Toast.LENGTH_LONG).show();
                    Log.i(TAG, e.toString());
                });
    }

    // show check email to reset password dialog
    private void showCheckEmailToResetPasswordDialog() {
        CheckEmailToResetPasswordDialog dialog = new CheckEmailToResetPasswordDialog();
        dialog.setCancelable(false);
        dialog.setListener(this::onBackPressed);
        dialog.show(getSupportFragmentManager(), TAG);
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

    // return true if send password reset email in progress
    private boolean isSendPasswordResetEmailInProgress() {
        return sendPasswordResetEmailTask != null && !sendPasswordResetEmailTask.isComplete();
    }
    //endregion


}
