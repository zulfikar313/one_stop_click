package com.example.mitrais.onestopclick.view.forgot_password;

import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.mitrais.onestopclick.Constant;
import com.example.mitrais.onestopclick.R;
import com.example.mitrais.onestopclick.dialog.CheckEmailToResetPasswordDialog;
import com.google.android.gms.tasks.Task;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;
import maes.tech.intentanim.CustomIntent;

public class ForgotPasswordActivity extends AppCompatActivity {
    private static final String TAG = "ForgotPasswordActivity";
    private Task<Void> sendEmailTask;

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
        initDagger();
    }

    @Override
    protected void onPause() {
        super.onPause();
        CustomIntent.customType(this, Constant.ANIMATION_FADEIN_TO_FADEOUT);
    }

    @OnClick(R.id.btn_reset_password)
    void onResetPasswordButtonClicked() {
        if (isEmailValid()) {
            if (isSendEmailInProgress())
                Toasty.info(this, getString(R.string.send_reset_password_in_progress), Toast.LENGTH_SHORT).show();
            else {
                String email = txtEmail.getEditText().getText().toString().trim();
                sendPasswordResetEmail(email);
            }
        }
    }

    // region private methods

    /**
     * initialize dagger injection
     */
    private void initDagger() {
        ForgotPasswordActivityComponent component = DaggerForgotPasswordActivityComponent.builder()
                .forgotPasswordActivity(this)
                .build();
        component.inject(this);
    }

    /**
     * send password reset email to user email address
     *
     * @param email email address
     */
    private void sendPasswordResetEmail(String email) {
        showProgressBar();
        sendEmailTask = viewModel.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> hideProgressBar())
                .addOnSuccessListener(aVoid -> {
                    showCheckEmailToResetPasswordDialog();
                })
                .addOnFailureListener(e -> Toasty.error(this, e.getMessage(), Toast.LENGTH_LONG).show());
    }

    private void showCheckEmailToResetPasswordDialog() {
        CheckEmailToResetPasswordDialog dialog = new CheckEmailToResetPasswordDialog();
        dialog.setCancelable(false);
        dialog.setListener(this::onBackPressed);
        dialog.show(getSupportFragmentManager(), TAG);
    }

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