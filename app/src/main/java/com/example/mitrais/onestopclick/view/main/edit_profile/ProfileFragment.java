package com.example.mitrais.onestopclick.view.main.edit_profile;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mitrais.onestopclick.App;
import com.example.mitrais.onestopclick.R;
import com.example.mitrais.onestopclick.custom_view.CustomImageView;
import com.example.mitrais.onestopclick.model.Profile;
import com.google.android.gms.tasks.Task;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;

public class ProfileFragment extends Fragment {
    private static final String TAG = "ProfileFragment";
    private static final int REQUEST_CHOOSE_IMAGE = 1;
    private Context context;
    private Profile profile;
    private Uri profileImgUri = Uri.parse("");
    private Task<Uri> uploadTask;
    private Task<Void> saveProfileTask;
    private String email;
    private int adminCounter = 0;

    @Inject
    ProfileViewModel viewModel;

    @BindView(R.id.img_profile)
    CustomImageView imgProfile;

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

    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        ButterKnife.bind(this, view);
        initDagger();
        if (viewModel.getUser() != null) {
            email = viewModel.getUser().getEmail();
            txtEmail.setText(email);
            observeProfile();
        }

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.context = null;
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
            if (isUploadInProgress() || isSaveProfileInProgress()) {
                if (context != null)
                    Toasty.info(context, getString(R.string.save_profile_in_progress), Toast.LENGTH_SHORT).show();
            } else {
                saveProfile();
            }

        }
    }

    @OnClick(R.id.img_profile)
    void onProfileImageClicked() {
        if (isUploadInProgress() || isSaveProfileInProgress()) {
            if (context != null)
                Toasty.info(context, getString(R.string.save_profile_in_progress), Toast.LENGTH_SHORT).show();
        } else if (!App.isOnline()) {
            if (context != null)
                Toasty.info(context, getString(R.string.internet_required), Toast.LENGTH_SHORT).show();
        } else
            openImageFileChooser();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CHOOSE_IMAGE && resultCode == Activity.RESULT_OK
                && data != null && data.getData() != null) {
            profileImgUri = data.getData();
            uploadProfileImage();
        }
    }

    // region private methods
    private void initDagger() {
        ProfileFragmentComponent component = DaggerProfileFragmentComponent.builder()
                .profileFragment(this)
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
                    imgProfile.setVisibility(View.VISIBLE);
                    if (context != null)
                        Toasty.success(context, getString(R.string.profile_saved), Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    hideProgressBar();
                    if (context != null)
                        Toasty.error(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e(TAG, e.getMessage());
                });
    }

    private void uploadProfileImage() {
        imgProfile.showProgressBar();
        String filename = email + "." + getFileExtension(profileImgUri);
        uploadTask = viewModel.uploadProfileImage(profileImgUri, filename)
                .addOnSuccessListener(uri -> {
                    Profile profile = new Profile();
                    profile.setEmail(email);
                    profile.setImageUri(uri.toString());

                    saveProfileTask = viewModel.saveProfileImageUri(profile)
                            .addOnCompleteListener(task -> imgProfile.hideProgressBar())
                            .addOnSuccessListener(aVoid -> {
                                if (context != null)
                                    Toasty.success(context, getString(R.string.thumbnail_saved), Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> {
                                Log.e(TAG, getString(R.string.failed_to_upload_thumbnail));
                                if (context != null)
                                    Toasty.error(context, e.getMessage(), Toast.LENGTH_LONG).show();
                            });
                })
                .addOnFailureListener(e -> {
                    imgProfile.hideProgressBar();
                    Log.e(TAG, getString(R.string.failed_to_upload_thumbnail));
                    if (context != null)
                        Toasty.error(context, e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    private void observeProfile() {
        viewModel.getProfileByEmail(viewModel.getUser().getEmail()).observe(this, profile -> {
            if (profile != null) {
                this.profile = profile;
                bindProfile(profile);
                imgProfile.setVisibility(View.VISIBLE);
                adminInputContainer.setVisibility(profile.isAdmin() ? View.VISIBLE : View.GONE);
            }
        });
    }

    private void bindProfile(Profile profile) {
        txtEmail.setText(profile.getEmail());
        txtAddress.getEditText().setText(profile.getAddress());
        if (profile.getImageUri() != null && !profile.getImageUri().isEmpty()) {
            imgProfile.loadImageUri(Uri.parse(profile.getImageUri()));
        }
        swAdminAccess.setChecked(profile.isAdmin());
    }

    private void openImageFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, REQUEST_CHOOSE_IMAGE);
    }

    private String getFileExtension(Uri uri) {
        ContentResolver resolver = context.getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(resolver.getType(uri));
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

    private boolean isUploadInProgress() {
        return uploadTask != null && !uploadTask.isComplete();
    }

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
