package com.example.mitrais.onestopclick.view.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;

import com.example.mitrais.onestopclick.R;

public class CheckEmailForVerificationDialog extends AppCompatDialogFragment {
    private DialogListener listener;

    public interface DialogListener {
        void onOkClicked();
    }

    public void setListener(DialogListener listener) {
        this.listener = listener;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setTitle(getString(R.string.attention))
                .setMessage(getString(R.string.message_check_email_for_verification))
                .setNegativeButton(getString(R.string.ok), (dialog, which) -> {
                    if (listener != null)
                        listener.onOkClicked();
                });
        return builder.create();
    }
}
