package com.example.mitrais.onestopclick.view.edit_music;

import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.mitrais.onestopclick.Constant;
import com.example.mitrais.onestopclick.R;
import com.example.mitrais.onestopclick.model.Product;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.android.gms.tasks.Task;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;
import io.supercharge.shimmerlayout.ShimmerLayout;
import maes.tech.intentanim.CustomIntent;

public class EditMusicActivity extends AppCompatActivity {
    private static final String TAG = "EditMusicActivity";
    private static final int REQUEST_CHOOSE_THUMBNAIL = 1;
    private static final int REQUEST_CHOOSE_MUSIC = 2;
    private Task<Uri> UploadTask;
    private Task<Void> saveProductTask;
    private Uri thumbnailUri = Uri.parse("");
    private Uri musicUri = Uri.parse("");
    private ExoPlayer musicPlayer;
    private String productId;
    private ArrayAdapter<CharSequence> genreAdapter;

    @Inject
    EditMusicViewModel viewModel;

    @BindView(R.id.shimmer_layout)
    ShimmerLayout shimmerLayout;

    @BindView(R.id.img_thumbnail)
    ImageView imgThumbnail;

    @BindView(R.id.txt_title)
    TextInputLayout txtTitle;

    @BindView(R.id.txt_artist)
    TextInputLayout txtArtist;

    @BindView(R.id.txt_description)
    TextInputLayout txtDescription;

    @BindView(R.id.sp_genre)
    Spinner spGenre;

    @BindView(R.id.music_view)
    PlayerView musicView;

    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_music);
        ButterKnife.bind(this);
        initDagger();
        initSpinner();

        if (getIntent() != null) {
            productId = getIntent().getStringExtra(Constant.EXTRA_PRODUCT_ID);
            observeProduct(productId);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        CustomIntent.customType(this, Constant.ANIMATION_FADEIN_TO_FADEOUT);
    }

    @Override
    public void onBackPressed() {
        if (isSaveProductInProgress())
            Toasty.info(this, getString(R.string.save_product_in_progress), Toast.LENGTH_SHORT).show();
        else if (isUploadInProgress())
            Toasty.info(this, getString(R.string.upload_in_progress), Toast.LENGTH_SHORT).show();
        else
            super.onBackPressed();
    }

    @OnClick({R.id.btn_save, R.id.btn_upload_music, R.id.img_thumbnail})
    void onButtonClicked(View view) {
        if (isSaveProductInProgress())
            Toasty.info(this, getString(R.string.save_product_in_progress), Toast.LENGTH_SHORT).show();
        else if (isUploadInProgress())
            Toasty.info(this, getString(R.string.upload_in_progress), Toast.LENGTH_SHORT).show();
        else {
            switch (view.getId()) {
                case R.id.btn_save:
                    if (isArtistValid() & isTitleValid() & isDescriptionValid())
                        saveProduct(productId);
                    break;
                case R.id.btn_upload_music:
                    openMusicFileChooser();
                    break;
                default: // img_thumbnail clicked
                    openThumbnailFileChooser();
                    break;
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CHOOSE_THUMBNAIL && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            thumbnailUri = data.getData();
            uploadThumbnail(productId, thumbnailUri);
        }

        if (requestCode == REQUEST_CHOOSE_MUSIC && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            musicUri = data.getData();
            uploadMusic(productId, musicUri);
        }
    }

    private void observeProduct(String id) {
        viewModel.getProductById(id).observe(this, product -> {
            if (product != null) {
                bindProduct(product);
            } else
                Toasty.error(this, getString(R.string.product_not_found), Toast.LENGTH_SHORT).show();
        });
    }

    private void bindProduct(Product product) {
        if (!product.getThumbnailUri().isEmpty()) {
            thumbnailUri = Uri.parse(product.getThumbnailUri());
            shimmerLayout.startShimmerAnimation();
            Picasso.get().load(product.getThumbnailUri()).placeholder(R.drawable.skeleton).into(imgThumbnail, new Callback() {
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

        txtTitle.getEditText().setText(product.getTitle());
        txtArtist.getEditText().setText(product.getArtist());

        if (product.getMusicUri() != null && !product.getMusicUri().isEmpty()) {
            musicUri = Uri.parse(product.getMusicUri());
            prepareMusicPlayer(musicUri);
        }

        txtDescription.getEditText().setText(product.getDescription());
        if (product.getGenre() != null && !product.getGenre().isEmpty()) {
            int position = genreAdapter.getPosition(product.getGenre());
            spGenre.setSelection(position);
        }
    }

    /**
     * @param id product id
     */
    private void saveProduct(String id) {
        showProgressBar();
        hideSoftKeyboard();
        String title = txtTitle.getEditText().getText().toString().trim();
        String artist = txtArtist.getEditText().getText().toString().trim();
        String description = txtDescription.getEditText().getText().toString().trim();

        Product product = new Product();
        product.setId(id);
        product.setTitle(title);
        product.setArtist(artist);
        product.setType(Constant.PRODUCT_TYPE_MUSIC);
        product.setDescription(description);
        product.setGenre(spGenre.getSelectedItem().toString());
        product.setThumbnailUri(thumbnailUri.toString());
        product.setMusicUri(musicUri.toString());

        saveProductTask = viewModel.saveProduct(product)
                .addOnCompleteListener(task -> hideProgressBar())
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, getString(R.string.product_has_been_saved), Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toasty.error(this, getString(R.string.failed_to_save_product), Toast.LENGTH_SHORT).show();
                    Log.e(TAG, e.getMessage());
                });
    }

    /**
     * upload thumbnail file then save thumbnail uri in product
     *
     * @param id  product id
     * @param uri thumbnail uri
     */
    private void uploadThumbnail(String id, Uri uri) {
        showProgressBar();
        String filename = id + Constant.NAME_EXT_THUMBNAIL + getFileExtension(uri);
        UploadTask = viewModel.uploadThumbnail(uri, filename)
                .addOnSuccessListener(uri1 -> saveProductTask = viewModel.saveThumbnailUri(id, uri1)
                        .addOnCompleteListener(task -> hideProgressBar())
                        .addOnSuccessListener(aVoid -> Toasty.success(this, getString(R.string.image_has_been_saved), Toast.LENGTH_SHORT).show())
                        .addOnFailureListener(e -> {
                            Log.e(TAG, getString(R.string.failed_to_upload_thumbnail));
                            Toasty.error(this, e.getMessage(), Toast.LENGTH_LONG).show();
                        }))
                .addOnFailureListener(e -> {
                    hideProgressBar();
                    Log.e(TAG, getString(R.string.failed_to_upload_thumbnail));
                    Toasty.error(this, e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }


    /**
     * @param id  product id
     * @param uri music uri
     */
    private void uploadMusic(String id, Uri uri) {
        showProgressBar();
        String filename = id + Constant.NAME_EXT_MUSIC + getFileExtension(uri);
        UploadTask = viewModel.uploadMusic(uri, filename)
                .addOnSuccessListener(uri1 ->
                        saveProductTask = viewModel.saveMusicUri(id, uri1)
                                .addOnCompleteListener(task -> hideProgressBar())
                                .addOnSuccessListener(aVoid ->
                                        Toasty.success(this, getString(R.string.music_uploaded), Toast.LENGTH_SHORT).show())
                                .addOnFailureListener(e -> {
                                    Log.e(TAG, e.getMessage());
                                    Toasty.error(this, getString(R.string.failed_to_upload_music), Toast.LENGTH_LONG).show();
                                }))
                .addOnFailureListener(e -> {
                    hideProgressBar();
                    Log.e(TAG, e.getMessage());
                    Toasty.error(this, getString(R.string.failed_to_upload_music), Toast.LENGTH_LONG).show();
                });
    }

    /**
     * @param uri music uri
     */
    private void prepareMusicPlayer(Uri uri) {
        showProgressBar();

        // prepare music player
        TrackSelector trackSelector = new DefaultTrackSelector();
        musicPlayer = ExoPlayerFactory.newSimpleInstance(this, trackSelector);
        musicPlayer.addListener(new Player.DefaultEventListener() {
            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                super.onPlayerStateChanged(playWhenReady, playbackState);
                if (playbackState == Player.STATE_READY) {
                    hideProgressBar();
                }
            }
        });

        // prepare music data source
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this, Util.getUserAgent(this, getPackageName()));
        MediaSource musicSource = new ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(uri);

        // configure music player
        musicPlayer.prepare(musicSource);
        musicView.setPlayer(musicPlayer);
    }

    private void openThumbnailFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, REQUEST_CHOOSE_THUMBNAIL);
    }

    private void openMusicFileChooser() {
        Intent intent = new Intent();
        intent.setType("audio/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, REQUEST_CHOOSE_MUSIC);
    }

    private String getFileExtension(Uri uri) {
        ContentResolver resolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(resolver.getType(uri));
    }

    /**
     * initialize dagger injection
     */
    private void initDagger() {
        EditMusicActivityComponent component = DaggerEditMusicActivityComponent.builder()
                .editMusicActivity(this)
                .build();
        component.inject(this);
    }

    /**
     * initialize genre spinner
     */
    private void initSpinner() {
        genreAdapter = ArrayAdapter.createFromResource(this, R.array.music_genre, R.layout.genre_spinner_item);
        genreAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spGenre.setAdapter(genreAdapter);
    }

    private boolean isTitleValid() {
        String title = txtTitle.getEditText().getText().toString().trim();
        if (title.isEmpty()) {
            txtTitle.setError(getString(R.string.error_empty_title));
            return false;
        }
        txtTitle.setError("");
        return true;
    }

    private boolean isArtistValid() {
        String artist = txtArtist.getEditText().getText().toString().trim();
        if (artist.isEmpty()) {
            txtArtist.setError(getString(R.string.error_empty_artist));
            return false;
        }
        txtArtist.setError("");
        return true;
    }

    private boolean isDescriptionValid() {
        String description = txtDescription.getEditText().getText().toString().trim();
        if (description.isEmpty()) {
            txtDescription.setError(getString(R.string.error_empty_description));
            return false;
        }
        txtDescription.setError("");
        return true;
    }

    private boolean isSaveProductInProgress() {
        return saveProductTask != null && !saveProductTask.isComplete();
    }

    private boolean isUploadInProgress() {
        return UploadTask != null && !UploadTask.isComplete();
    }

    private void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgressBar() {
        progressBar.setVisibility(View.INVISIBLE);
    }

    private void hideSoftKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Service.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(txtTitle.getWindowToken(), 0);
    }
}
