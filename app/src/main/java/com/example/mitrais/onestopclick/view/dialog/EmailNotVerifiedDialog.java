package com.example.mitrais.onestopclick.view.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;

import com.example.mitrais.onestopclick.R;

public class EmailNotVerifiedDialog extends AppCompatDialogFragment {
    private DialogListener listener;

    public interface DialogListener {
        void onVerifyClicked();
    }

    public void setListener(DialogListener listener) {
        this.listener = listener;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setTitle(getString(R.string.error))
                .setMessage(getString(R.string.message_not_verified_email))
                .setNegativeButton(getString(R.string.no), (dialog, which) -> {

                })
                .setPositiveButton(getString(R.string.verify), (dialog, which) -> {
                    if (listener != null)
                        listener.onVerifyClicked();
                });
        return builder.create();
    }
}
