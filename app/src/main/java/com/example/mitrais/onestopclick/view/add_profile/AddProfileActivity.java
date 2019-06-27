package com.example.mitrais.onestopclick.view.add_profile;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mitrais.onestopclick.App;
import com.example.mitrais.onestopclick.Constant;
import com.example.mitrais.onestopclick.R;
import com.example.mitrais.onestopclick.custom_view.CustomImageView;
import com.example.mitrais.onestopclick.model.Profile;
import com.example.mitrais.onestopclick.view.main.MainActivity;
import com.google.android.gms.tasks.Task;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;
import maes.tech.intentanim.CustomIntent;

public class AddProfileActivity extends AppCompatActivity {
    private static final String TAG = "AddProfileActivity";
    private static final int REQUEST_CHOOSE_IMAGE = 1;
    private Uri profileImgUri = Uri.parse("");
    //    private Task<Uri> uploadTask;
    private Task<Void> saveProfileTask;
    private String email;
    private int adminCounter = 0;

    @Inject
    AddProfileViewModel viewModel;

//    @BindView(R.id.img_profile)
//    CustomImageView imgProfile;

    @BindView(R.id.txt_email)
    TextView txtEmail;

    @BindView(R.id.txt_address)
    TextInputLayout txtAddress;

    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    @BindView(R.id.admin_input_container)
    ConstraintLayout adminInputContainer;

    @BindView(R.id.sw_admin_access)
    Switch swAdminAccess;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_profile);
        setTitle(getString(R.string.profile));
        ButterKnife.bind(this);
        initDagger();
        if (viewModel.getUser() != null) {
            email = viewModel.getUser().getEmail();
            txtEmail.setText(email);
        }
    }

    @OnClick(R.id.img_admin_access)
    void onAdminAccessClicked() {
        adminCounter++;
        if (adminCounter == 7) {
            adminCounter = 0;
            adminInputContainer.setVisibility(View.VISIBLE);
        }
    }

    @OnClick(R.id.btn_save_profile)
    void onSaveProfileButtonClicked() {
        if (isAddressValid()) {
            if (isSaveProfileInProgress()) {
                Toasty.info(this, getString(R.string.save_profile_in_progress), Toast.LENGTH_SHORT).show();
            } else {
                saveProfile();
            }
        }
    }
//
//    @OnClick(R.id.img_profile)
//    void onProfileImageClicked() {
//        if (isUploadInProgress() || isSaveProfileInProgress()) {
//            Toasty.info(this, getString(R.string.save_profile_in_progress), Toast.LENGTH_SHORT).show();
//        } else if (!App.isOnline()) {
//            Toasty.info(this, getString(R.string.internet_required), Toast.LENGTH_SHORT).show();
//        } else
//            openImageFileChooser();
//    }
//
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == REQUEST_CHOOSE_IMAGE && resultCode == Activity.RESULT_OK
//                && data != null && data.getData() != null) {
//            profileImgUri = data.getData();
//            uploadProfileImage();
//        }
//    }

    // region private methods
    private void initDagger() {
        AddProfileActivityComponent component = DaggerAddProfileActivityComponent.builder()
                .addProfileActivity(this)
                .build();
        component.inject(this);
    }

    private void saveProfile() {
        showProgressBar();
        String address = txtAddress.getEditText().getText().toString().trim();
        Profile profile = new Profile();
        profile.setEmail(viewModel.getUser().getEmail());
        profile.setAddress(address);
        profile.setImageUri(profileImgUri.toString());
        profile.setAdmin(swAdminAccess.isChecked());
        saveProfileTask = viewModel.saveProfile(profile)
                .addOnCompleteListener(task -> hideProgressBar())
                .addOnSuccessListener(aVoid -> {
//                    imgProfile.setVisibility(View.VISIBLE);
                    Toasty.success(this, getString(R.string.profile_saved), Toast.LENGTH_SHORT).show();
                    goToMainPage();
                })
                .addOnFailureListener(e -> {
                    hideProgressBar();
                    Toasty.error(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e(TAG, e.getMessage());
                });
    }
//
//    private void uploadProfileImage() {
//        imgProfile.showProgressBar();
//        String filename = email + "." + getFileExtension(profileImgUri);
//        uploadTask = viewModel.uploadProfileImage(profileImgUri, filename)
//                .addOnSuccessListener(uri -> {
//                    Profile profile = new Profile();
//                    profile.setEmail(email);
//                    profile.setImageUri(uri.toString());
//
//                    saveProfileTask = viewModel.saveProfileImageUri(profile)
//                            .addOnCompleteListener(task -> imgProfile.hideProgressBar())
//                            .addOnSuccessListener(aVoid -> {
//                                Toasty.success(this, getString(R.string.thumbnail_saved), Toast.LENGTH_SHORT).show();
//                            })
//                            .addOnFailureListener(e -> {
//                                Log.e(TAG, getString(R.string.failed_to_upload_thumbnail));
//                                Toasty.error(this, e.getMessage(), Toast.LENGTH_LONG).show();
//                            });
//                })
//                .addOnFailureListener(e -> {
//                    imgProfile.hideProgressBar();
//                    Log.e(TAG, getString(R.string.failed_to_upload_thumbnail));
//                    Toasty.error(this, e.getMessage(), Toast.LENGTH_LONG).show();
//                });
//    }

    private void openImageFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, REQUEST_CHOOSE_IMAGE);
    }

    private String getFileExtension(Uri uri) {
        ContentResolver resolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(resolver.getType(uri));
    }

    private void goToMainPage() {
        finish();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        CustomIntent.customType(this, Constant.ANIMATION_FADEIN_TO_FADEOUT);
    }

    private boolean isAddressValid() {
        String address = txtAddress.getEditText().getText().toString().trim();
        if (address.isEmpty()) {
            txtAddress.setError(getString(R.string.address_cant_be_empty));
            return false;
        }
        txtAddress.setError("");
        return true;
    }

//    private boolean isUploadInProgress() {
//        return uploadTask != null && !uploadTask.isComplete();
//    }

    private boolean isSaveProfileInProgress() {
        return saveProfileTask != null && !saveProfileTask.isComplete();
    }

    private void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgressBar() {
        progressBar.setVisibility(View.INVISIBLE);
    }
    // endregion
}
