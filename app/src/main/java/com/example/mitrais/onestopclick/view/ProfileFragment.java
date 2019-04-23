package com.example.mitrais.onestopclick.view;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mitrais.onestopclick.App;
import com.example.mitrais.onestopclick.R;
import com.example.mitrais.onestopclick.dagger.component.DaggerProfileFragmentComponent;
import com.example.mitrais.onestopclick.dagger.component.ProfileFragmentComponent;
import com.example.mitrais.onestopclick.model.Profile;
import com.example.mitrais.onestopclick.viewmodel.ProfileViewModel;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;
import io.supercharge.shimmerlayout.ShimmerLayout;

/**
 * ProfileFragment handle profile page logic
 */
public class ProfileFragment extends Fragment {
    private static final String TAG = "ProfileFragment";
    private static final int REQUEST_CHOOSE_IMAGE = 1;
    private FirebaseUser user;
    private String profileImageUri;
    private Task<Uri> profileImageUploadTask;
    private Task<Void> saveProfileTask;
    private Profile profile;

    @Inject
    ProfileViewModel viewModel;

    @BindView(R.id.shimmer_layout)
    ShimmerLayout shimmerLayout;

    @BindView(R.id.img_profile)
    ImageView imgProfile;

    @BindView(R.id.txt_email)
    TextView txtEmail;

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
            observeProfile();
        }

        return view;
    }

    @OnClick(R.id.btn_save_profile)
    void onSaveProfileButtonClicked() {
        if (isAddressValid() & isProfileImageValid()) {
            if (isProfileImageUploadInProgress() || isSaveProfileInProgress())
                Toasty.info(getActivity(), getString(R.string.save_profile_is_in_progress), Toast.LENGTH_SHORT).show();
            else {
                saveProfile(Uri.parse(profileImageUri));
            }

        }
    }

    @OnClick(R.id.img_profile)
    void onProfileImageClicked() {
        if (isProfileImageUploadInProgress() || isSaveProfileInProgress())
            Toasty.info(getActivity(), getString(R.string.save_profile_is_in_progress), Toast.LENGTH_SHORT).show();
        else if (!App.isOnline())
            Toasty.info(getActivity(), getString(R.string.internet_required), Toast.LENGTH_SHORT).show();
        else
            openImageFileChooser();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CHOOSE_IMAGE && resultCode == Activity.RESULT_OK
                && data != null && data.getData() != null) {
            profileImageUri = data.getData().toString();
            shimmerLayout.startShimmerAnimation();
            Picasso.get().load(profileImageUri).placeholder(R.drawable.skeleton).into(imgProfile, new Callback() {
                @Override
                public void onSuccess() {
                    shimmerLayout.stopShimmerAnimation();
                }

                @Override
                public void onError(Exception e) {
                    shimmerLayout.stopShimmerAnimation();
                    Log.e(TAG, e.toString());
                }
            });
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
     * @param imageUri profile image profileImageUri
     */
    private void saveProfile(Uri imageUri) {
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
                    /* save profile data */
                    profileImageUri = uri.toString();
                    String address = txtAddress.getEditText().getText().toString().trim();
                    Profile profile = new Profile(user.getEmail(), uri.toString(), filename, address);
                    saveProfileTask = viewModel.saveProfile(profile)
                            .addOnCompleteListener(task -> hideProgressBar())
                            .addOnSuccessListener(aVoid1 -> Toasty.success(getActivity(), getString(R.string.profile_update_success), Toast.LENGTH_SHORT).show())
                            .addOnFailureListener(e -> Toasty.error(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show());
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
                profileImageUri = profile.getProfileImageUri();
                if (!profileImageUri.isEmpty()) {
                    shimmerLayout.startShimmerAnimation();
                    Picasso.get().load(profileImageUri).placeholder(R.drawable.skeleton).into(imgProfile, new Callback() {
                        @Override
                        public void onSuccess() {
                            shimmerLayout.stopShimmerAnimation();
                        }

                        @Override
                        public void onError(Exception e) {
                            shimmerLayout.stopShimmerAnimation();
                            Log.e(TAG, e.toString());
                        }
                    });
                }

            } else {
                txtAddress.getEditText().setText("");
                imgProfile.setImageResource(R.drawable.ic_launcher_background);
            }
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
     * get file extension from profileImageUri
     *
     * @param uri file profileImageUri
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
     * @return true if profile image valid
     */
    private boolean isProfileImageValid() {
        if (profileImageUri == null || profileImageUri.isEmpty()) {
            Toasty.error(getActivity(), getString(R.string.error_invalid_profile_image), Toast.LENGTH_SHORT).show();
            return false;
        }
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
     * returns true if save profile in progress
     *
     * @return save profile progress status
     */
    private boolean isSaveProfileInProgress() {
        return saveProfileTask != null && !saveProfileTask.isComplete();
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
