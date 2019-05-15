package com.example.mitrais.onestopclick.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatDialogFragment;

import com.example.mitrais.onestopclick.R;

/**
 * dialog informing user to check email to reset password
 */
public class CheckEmailToResetPasswordDialog extends AppCompatDialogFragment {
    private DialogListener listener;

    public interface DialogListener {
        void onOkClicked();
    }

    public void setListener(DialogListener listener) {
        this.listener = listener;
    }

    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setTitle(getString(R.string.attention))
                .setMessage(getString(R.string.message_check_email_to_reset_password))
                .setNegativeButton(getString(R.string.ok), (dialog, which) -> {
                    if (listener != null)
                        listener.onOkClicked();
                });
        return builder.create();
    }
}
