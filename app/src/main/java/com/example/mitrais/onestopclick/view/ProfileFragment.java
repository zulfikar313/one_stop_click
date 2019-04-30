package com.example.mitrais.onestopclick.view;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
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
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;
import io.supercharge.shimmerlayout.ShimmerLayout;

public class ProfileFragment extends Fragment {
    private static final String TAG = "ProfileFragment";
    private static final int REQUEST_CHOOSE_IMAGE = 1;
    private Context context;
    private Uri imgUri;
    private Task<Uri> uploadTask;
    private Task<Void> saveProfileTask;
    private String email;

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

    /**
     * @return ProfileFragment new instance
     */
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

    @OnClick(R.id.btn_save_profile)
    void onSaveProfileButtonClicked() {
        if (isAddressValid()) {
            if (isUploadInProgress() || isSaveProfileInProgress()) {
                if (context != null)
                    Toasty.info(context, getString(R.string.save_profile_is_in_progress), Toast.LENGTH_SHORT).show();
            } else {
                saveProfile();
            }

        }
    }

    @OnClick(R.id.img_profile)
    void onProfileImageClicked() {
        if (isUploadInProgress() || isSaveProfileInProgress()) {
            if (context != null)
                Toasty.info(context, getString(R.string.save_profile_is_in_progress), Toast.LENGTH_SHORT).show();
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
            imgUri = data.getData();
            uploadProfileImage();
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
     * save profile data
     */
    private void saveProfile() {
        showProgressBar();
        String address = txtAddress.getEditText().getText().toString().trim();
        Profile profile = new Profile();
        profile.setEmail(viewModel.getUser().getEmail());
        profile.setAddress(address);
        saveProfileTask = viewModel.saveProfile(profile)
                .addOnCompleteListener(task -> hideProgressBar())
                .addOnSuccessListener(aVoid -> {
                    imgProfile.setVisibility(View.VISIBLE);
                    if (context != null)
                        Toasty.success(context, getString(R.string.profile_has_been_saved), Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    hideProgressBar();
                    if (context != null)
                        Toasty.error(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e(TAG, e.getMessage());
                });
    }

    /**
     * upload profile image
     */
    private void uploadProfileImage() {
        showProgressBar();
        String filename = email + "." + getFileExtension(imgUri);
        uploadTask = viewModel.uploadProfileImage(imgUri, filename)
                .addOnSuccessListener(uri -> {
                    saveProfileTask = viewModel.saveProfileImageUri(email, uri)
                            .addOnCompleteListener(task -> hideProgressBar())
                            .addOnSuccessListener(aVoid -> {
                                if (context != null)
                                    Toasty.success(context, getString(R.string.image_has_been_saved), Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> {
                                Log.e(TAG, getString(R.string.error_failed_to_upload_product_image));
                                if (context != null)
                                    Toasty.error(context, e.getMessage(), Toast.LENGTH_LONG).show();
                            });
                })
                .addOnFailureListener(e -> {
                    hideProgressBar();
                    Log.e(TAG, getString(R.string.error_failed_to_upload_product_image));
                    if (context != null)
                        Toasty.error(context, e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    /**
     * observe profile data
     */
    private void observeProfile() {
        viewModel.getProfileByEmail(viewModel.getUser().getEmail()).observe(this, profile -> {
            if (profile != null) {
                bindProfile(profile);
                imgProfile.setVisibility(View.VISIBLE);
            }
        });
    }

    /**
     * bind profile data to view
     */
    private void bindProfile(Profile profile) {
        txtEmail.setText(profile.getEmail());
        txtAddress.getEditText().setText(profile.getAddress());
        if (profile.getImageUri() != null && !profile.getImageUri().isEmpty()) {
            imgUri = Uri.parse(profile.getImageUri());
            shimmerLayout.startShimmerAnimation();
            Picasso.get().load(imgUri).placeholder(R.drawable.skeleton).into(imgProfile, new Callback() {
                @Override
                public void onSuccess() {
                    shimmerLayout.stopShimmerAnimation();
                }

                @Override
                public void onError(Exception e) {
                    shimmerLayout.stopShimmerAnimation();
                    if (context != null)
                        Toasty.error(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e(TAG, e.getMessage());
                }
            });
        }
    }

    private void openImageFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, REQUEST_CHOOSE_IMAGE);
    }

    /**
     * @param uri file uri
     * @return file extension
     */
    private String getFileExtension(Uri uri) {
        ContentResolver resolver = context.getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(resolver.getType(uri));
    }

    /**
     * @return true if address valid
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
     * @return true if upload in progress
     */
    private boolean isUploadInProgress() {
        return uploadTask != null && !uploadTask.isComplete();
    }

    /**
     * @return true if save profile in progress
     */
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
