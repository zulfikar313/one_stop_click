package com.example.mitrais.onestopclick.view;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mitrais.onestopclick.R;
import com.example.mitrais.onestopclick.dagger.component.DaggerProfileFragmentComponent;
import com.example.mitrais.onestopclick.dagger.component.ProfileFragmentComponent;
import com.example.mitrais.onestopclick.model.Profile;
import com.example.mitrais.onestopclick.viewmodel.ProfileViewModel;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.squareup.picasso.Picasso;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;

/**
 * ProfileFragment handle profile page logic
 */
public class ProfileFragment extends Fragment {
    public static final int REQUEST_CHOOSE_IMAGE = 1;
    private FirebaseUser user;
    private Uri uri;
    private Task<Uri> profileImageUploadTask;
    private Task<Void> updateUserTask;
    private Task<Void> setProfileTask;
    private Profile profile;

    @Inject
    ProfileViewModel viewModel;

    @BindView(R.id.img_profile)
    ImageView imgProfile;

    @BindView(R.id.txt_email)
    TextView txtEmail;

    @BindView(R.id.txt_display_name)
    TextInputLayout txtDisplayName;

    @BindView(R.id.txt_address)
    TextInputLayout txtAddress;

    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().setTitle(getString(R.string.profile));
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        ButterKnife.bind(this, view);
        initDagger();

        user = viewModel.getUser();
        if (user != null) {
            // bind user
            txtEmail.setText(user.getEmail());
            txtDisplayName.getEditText().setText(user.getDisplayName());

            observeProfile();
        }

        return view;
    }

    @OnClick(R.id.btn_save_profile)
    void onSaveProfileButtonClicked() {
        if (isAddressValid() & isDisplayNameValid()) {
            if (isProfileImageUploadInProgress() || isUpdateUserInProgress() || isSaveProfileInProgress())
                Toasty.info(getActivity(), getString(R.string.save_profile_is_in_progress), Toast.LENGTH_SHORT).show();
            else {
                String displayname = txtDisplayName.getEditText().getText().toString().trim();
                String address = txtAddress.getEditText().getText().toString().trim();
                saveProfileDetails(displayname, address);
            }

        }
    }

    @OnClick(R.id.img_profile)
    void onProfileImageClicked() {
        if (isProfileImageUploadInProgress() || isUpdateUserInProgress() || isSaveProfileInProgress())
            Toasty.info(getActivity(), getString(R.string.save_profile_is_in_progress), Toast.LENGTH_SHORT).show();
        else
            openImageFileChooser();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CHOOSE_IMAGE && resultCode == getActivity().RESULT_OK
                && data != null && data.getData() != null) {
            uri = data.getData();
            Picasso.get().load(uri).placeholder(R.drawable.ic_launcher_background).into(imgProfile);
            uploadProfileImage(uri);
        }
    }

    // region private methods

    /**
     * initialize dagger injection
     */
    private void initDagger() {
        ProfileFragmentComponent component = DaggerProfileFragmentComponent.builder()
                .profileFragment(this)
                .build();
        component.inject(this);
    }

    /**
     * save profile image
     *
     * @param imageUri profile image uri
     */
    private void uploadProfileImage(Uri imageUri) {
        showProgressBar();

        String filename;
        if (profile == null || profile.getProfileImageFilename().isEmpty()) {
            filename = System.currentTimeMillis() + "." + getFileExtension(imageUri);
        } else {
            filename = profile.getProfileImageFilename();
        }

        /* upload image file */
        profileImageUploadTask = viewModel.uploadProfileImage(imageUri, filename)
                .addOnSuccessListener(uri -> {
                    /* save user data */
                    updateUserTask = viewModel.setUser(uri)
                            .addOnSuccessListener(aVoid -> {
                                /* save profile data */
                                Profile profile = new Profile(user.getEmail(), uri.toString(), filename, "");
                                setProfileTask = viewModel.setProfileImage(profile)
                                        .addOnSuccessListener(aVoid1 -> {
                                            hideProgressBar();
                                            Toasty.success(getActivity(), getString(R.string.profile_update_success), Toast.LENGTH_SHORT).show();
                                        })
                                        .addOnFailureListener(e -> {
                                            /* add new profile if profile doesn't exist */
                                            if (((FirebaseFirestoreException) e).getCode() == FirebaseFirestoreException.Code.NOT_FOUND) {
                                                setProfileTask = viewModel.addProfile(profile)
                                                        .addOnCompleteListener(task -> hideProgressBar())
                                                        .addOnSuccessListener(aVoid12 -> Toasty.success(getActivity(), getString(R.string.profile_update_success), Toast.LENGTH_SHORT).show())
                                                        .addOnFailureListener(e1 -> Toasty.error(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show());
                                            } else
                                                Toasty.error(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                                        });
                            })
                            .addOnFailureListener(e -> {
                                hideProgressBar();
                                Toasty.error(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                            });
                })
                .addOnFailureListener(e -> {
                    hideProgressBar();
                    Toasty.error(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    /**
     * observe profile data
     */
    private void observeProfile() {
        viewModel.getProfileByEmail(user.getEmail()).observe(this, profile -> {
            this.profile = profile;
            if (profile != null) {
                /* bind profile */
                txtAddress.getEditText().setText(profile.getAddress());
                String uri = profile.getProfileImageUri();
                if (!uri.isEmpty())
                    Picasso.get().load(uri).placeholder(R.drawable.ic_launcher_background).into(imgProfile);
            } else {
                txtAddress.getEditText().setText("");
                imgProfile.setImageResource(R.drawable.ic_launcher_background);
            }
        });
    }

    /**
     * save profile details
     */
    private void saveProfileDetails(String displayname, String address) {
        showProgressBar();

        /* save user */
        updateUserTask = viewModel.setUser(displayname)
                .addOnSuccessListener(aVoid -> {
                    /* save profile */
                    Profile profile = new Profile(user.getEmail(), "", "", address);
                    setProfileTask = viewModel.setProfileDetails(profile)
                            .addOnSuccessListener(aVoid1 -> {
                                hideProgressBar();
                                Toasty.success(getActivity(), getString(R.string.profile_update_success), Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> {
                                /* add profile in case profile not exist */
                                if (((FirebaseFirestoreException) e).getCode() == FirebaseFirestoreException.Code.NOT_FOUND) {
                                    setProfileTask = viewModel.addProfile(profile)
                                            .addOnCompleteListener(task -> hideProgressBar())
                                            .addOnSuccessListener(aVoid12 -> Toasty.success(getActivity(), getString(R.string.profile_update_success), Toast.LENGTH_SHORT).show())
                                            .addOnFailureListener(e1 -> Toasty.error(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show());
                                } else
                                    Toasty.error(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                            });
                })
                .addOnFailureListener(e -> {
                    hideProgressBar();
                    Toasty.error(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    /**
     * open image file chooser
     */
    private void openImageFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, REQUEST_CHOOSE_IMAGE);
    }

    /**
     * get file extension from uri
     *
     * @param uri file uri
     * @return file extension
     */
    private String getFileExtension(Uri uri) {
        ContentResolver resolver = getActivity().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(resolver.getType(uri));
    }

    /**
     * returns true if address valid
     *
     * @return address validation
     */
    private boolean isAddressValid() {
        String address = txtAddress.getEditText().getText().toString().trim();
        if (address.isEmpty()) {
            txtAddress.setError(getString(R.string.error_empty_address));
            return false;
        }
        txtAddress.setError("");
        return true;
    }

    /**
     * returns true if display name valid
     *
     * @return display name validation
     */
    private boolean isDisplayNameValid() {
        String displayName = txtDisplayName.getEditText().getText().toString().trim();
        if (displayName.isEmpty()) {
            txtDisplayName.setError("Display name can't be empty");
            return false;
        }
        txtDisplayName.setError("");
        return true;
    }

    /**
     * returns true if profile image upload in progrees
     *
     * @return profile image upload progress status
     */
    private boolean isProfileImageUploadInProgress() {
        return profileImageUploadTask != null && !profileImageUploadTask.isComplete();
    }

    /**
     * returns true if update user in progress
     *
     * @return update user progress status
     */
    private boolean isUpdateUserInProgress() {
        return updateUserTask != null && !updateUserTask.isComplete();
    }

    /**
     * returns true if save profile in progress
     *
     * @return save profile progress status
     */
    private boolean isSaveProfileInProgress() {
        return setProfileTask != null && !setProfileTask.isComplete();
    }

    /**
     * set progress bar visible
     */
    private void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    /**
     * set progress bar invisible
     */
    private void hideProgressBar() {
        progressBar.setVisibility(View.INVISIBLE);
    }
    // endregion
}
