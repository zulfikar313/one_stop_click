package com.example.mitrais.onestopclick.view;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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

public class ProfileFragment extends Fragment {
    public static final int REQUEST_CHOOSE_IMAGE = 1;
    private FirebaseUser user;
    private Uri uri;
    private Task<Uri> profileImageUploadTask;
    private Task<Void> updateUserTask;
    private Task<Void> saveProfileTask;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().setTitle(getString(R.string.profile));
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        ButterKnife.bind(this, view);
        initDagger();
        bindData();

        return view;
    }

    @OnClick(R.id.btn_save_profile)
    void onSaveProfileButtonClicked() {
        if (isAddressValid() & isDisplayNameValid()) {
            if (isProfileImageUploadInProgress() || isUpdateUserInProgress() || isSaveProfileInProgress())
                Toasty.info(getActivity(), getString(R.string.save_profile_is_in_progress), Toast.LENGTH_SHORT).show();
            else
                saveProfile();
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
            saveProfileImage(uri);
        }
    }

    // save profile image
    private void saveProfileImage(Uri imageUri) {
        progressBar.setVisibility(View.VISIBLE);

        String fileName;
        if (profile == null || profile.getProfileImageFileName().isEmpty()) {
            fileName = System.currentTimeMillis() + "." + getFileExtension(imageUri);
        } else {
            fileName = profile.getProfileImageFileName();
        }
        profileImageUploadTask = viewModel.saveProfileImage(imageUri, fileName)
                .addOnSuccessListener(uri -> {
                    // save user
                    updateUserTask = viewModel.saveUser(uri)
                            .addOnSuccessListener(aVoid -> {
                                // save profile
                                Profile profile = new Profile(user.getEmail(), uri.toString(), fileName, "");
                                saveProfileTask = viewModel.saveProfileImageData(profile)
                                        .addOnSuccessListener(aVoid1 -> {
                                            progressBar.setVisibility(View.INVISIBLE);
                                            Toasty.success(getActivity(), getString(R.string.profile_update_success), Toast.LENGTH_SHORT).show();
                                        })
                                        .addOnFailureListener(e -> {
                                            // add profile if profile not exist yet
                                            if (((FirebaseFirestoreException) e).getCode() == FirebaseFirestoreException.Code.NOT_FOUND) {
                                                saveProfileTask = viewModel.addProfile(profile)
                                                        .addOnCompleteListener(task -> {
                                                            progressBar.setVisibility(View.INVISIBLE);
                                                        })
                                                        .addOnSuccessListener(aVoid12 -> Toasty.success(getActivity(), getString(R.string.profile_update_success), Toast.LENGTH_SHORT).show())
                                                        .addOnFailureListener(e1 -> {
                                                            Toasty.error(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                                                        });
                                            } else
                                                Toasty.error(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                                        });
                            })
                            .addOnFailureListener(e -> {
                                progressBar.setVisibility(View.INVISIBLE);
                                Toasty.error(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                            });
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.INVISIBLE);
                    Toasty.error(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    // region private methods
    // initialize dagger injection
    private void initDagger() {
        ProfileFragmentComponent component = DaggerProfileFragmentComponent.builder()
                .profileFragment(this)
                .build();
        component.inject(this);
    }

    // bind data to view
    private void bindData() {
        user = viewModel.getUser();
        if (user != null) {
            // bind user
            txtEmail.setText(user.getEmail());
            txtDisplayName.getEditText().setText(user.getDisplayName());

            viewModel.getProfileByEmail(user.getEmail()).observe(this, profile -> {
                this.profile = profile;
                if (profile != null) {
                    // bind profile
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
    }

    // save profile
    private void saveProfile() {
        progressBar.setVisibility(View.VISIBLE);

        String displayName = txtDisplayName.getEditText().getText().toString().trim();
        String address = txtAddress.getEditText().getText().toString().trim();

        // save user
        updateUserTask = viewModel.saveUser(displayName)
                .addOnSuccessListener(aVoid -> {
                    // save profile
                    Profile profile = new Profile(user.getEmail(), "", "", address);
                    saveProfileTask = viewModel.saveProfile(profile)
                            .addOnSuccessListener(aVoid1 -> {
                                progressBar.setVisibility(View.INVISIBLE);
                                Toasty.success(getActivity(), getString(R.string.profile_update_success), Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> {
                                // add profile if profile not exist yet
                                if (((FirebaseFirestoreException) e).getCode() == FirebaseFirestoreException.Code.NOT_FOUND) {
                                    saveProfileTask = viewModel.addProfile(profile)
                                            .addOnCompleteListener(task -> {
                                                progressBar.setVisibility(View.INVISIBLE);
                                            })
                                            .addOnSuccessListener(aVoid12 -> Toasty.success(getActivity(), getString(R.string.profile_update_success), Toast.LENGTH_SHORT).show())
                                            .addOnFailureListener(e1 -> {
                                                Toasty.error(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                                            });
                                } else
                                    Toasty.error(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                            });
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.INVISIBLE);
                    Toasty.error(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    // open image file chooser
    private void openImageFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, REQUEST_CHOOSE_IMAGE);
    }

    // get file extension from uri
    private String getFileExtension(Uri uri) {
        ContentResolver resolver = getActivity().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(resolver.getType(uri));
    }

    // return true if address valid
    private boolean isAddressValid() {
        String address = txtAddress.getEditText().getText().toString().trim();
        if (address.isEmpty()) {
            txtAddress.setError(getString(R.string.error_empty_address));
            return false;
        }
        txtAddress.setError("");
        return true;
    }

    // returnt true if display name valid
    private boolean isDisplayNameValid() {
        String displayName = txtDisplayName.getEditText().getText().toString().trim();
        if (displayName.isEmpty()) {
            txtDisplayName.setError("Display name can't be empty");
            return false;
        }
        txtDisplayName.setError("");
        return true;
    }

    // return true if profile image upload in progress
    private boolean isProfileImageUploadInProgress() {
        return profileImageUploadTask != null && !profileImageUploadTask.isComplete();
    }

    // return true if update user in progress
    private boolean isUpdateUserInProgress() {
        return updateUserTask != null && !updateUserTask.isComplete();
    }

    // return true if save profile in progress
    private boolean isSaveProfileInProgress() {
        return saveProfileTask != null && !saveProfileTask.isComplete();
    }
    // endregion
}
