package com.example.mitrais.onestopclick.view.edit_music;

import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.mitrais.onestopclick.Constant;
import com.example.mitrais.onestopclick.R;
import com.example.mitrais.onestopclick.adapter.CommentAdapter;
import com.example.mitrais.onestopclick.custom_view.CustomImageView;
import com.example.mitrais.onestopclick.custom_view.CustomMusicView;
import com.example.mitrais.onestopclick.model.Comment;
import com.example.mitrais.onestopclick.model.Product;
import com.example.mitrais.onestopclick.model.Profile;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;
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
    private Profile profile;
    private String productId;
    private Product product;
    private boolean isAdmin;
    private ArrayAdapter<CharSequence> genreAdapter;
    private CommentAdapter commentAdapter;

    @Inject
    EditMusicViewModel viewModel;

    @BindView(R.id.img_thumbnail)
    CustomImageView imgThumbnail;

    @BindView(R.id.txt_title)
    TextInputLayout txtTitle;

    @BindView(R.id.txt_artist)
    TextInputLayout txtArtist;

    @BindView(R.id.txt_description)
    TextInputLayout txtDescription;

    @BindView(R.id.btn_save)
    AppCompatButton btnSave;

    @BindView(R.id.sp_genre)
    Spinner spGenre;

    @BindView(R.id.txt_price)
    TextInputLayout txtPrice;

    @BindView(R.id.rating_bar)
    RatingBar ratingBar;

    @BindView(R.id.music_view)
    CustomMusicView musicView;

    @BindView(R.id.btn_upload_music)
    AppCompatButton btnUploadMusic;

    @BindView(R.id.txt_comment)
    TextInputLayout txtComment;

    @BindView(R.id.rec_comments)
    RecyclerView recComments;

    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_music);
        ButterKnife.bind(this);
        initDagger();
        initSpinner();
        initRecyclerView();

        if (getIntent() != null) {
            productId = getIntent().getStringExtra(Constant.EXTRA_PRODUCT_ID);
            isAdmin = getIntent().getBooleanExtra(Constant.EXTRA_IS_ADMIN, false);
            viewModel.sync(productId);
            viewModel.getCommentReference(productId).addSnapshotListener((queryDocumentSnapshots, e) -> {
                if (queryDocumentSnapshots != null) {
                    for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()) {
                        DocumentSnapshot documentSnapshot = dc.getDocument();
                        Comment comment = documentSnapshot.toObject(Comment.class);
                        comment.setProductId(productId);

                        switch (dc.getType()) {
                            case ADDED: {
                                viewModel.insertComment(comment);
                                break;
                            }
                            case MODIFIED: {
                                viewModel.insertComment(comment);
                                break;
                            }
                            case REMOVED: {
                                viewModel.deleteComment(comment);
                                break;
                            }
                        }
                    }
                }
            });
            viewModel.syncProfiles();
            observeProfile();
            observeProduct(productId);
            observeComments(productId);

            if (!isAdmin) {
                ratingBar.setVisibility(View.VISIBLE);
                txtTitle.getEditText().setEnabled(false);
                txtArtist.getEditText().setEnabled(false);
                txtDescription.getEditText().setEnabled(false);
                txtPrice.setEnabled(false);
                spGenre.setEnabled(false);
                btnSave.setVisibility(View.GONE);
                btnUploadMusic.setVisibility(View.GONE);
            }
        }

        ratingBar.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> {
            String email = viewModel.getUser().getEmail();
            rateProduct(product, email, rating);
        });
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

    @OnClick({R.id.btn_add_comment, R.id.btn_save, R.id.btn_upload_music, R.id.img_thumbnail})
    void onButtonClicked(View view) {
        if (isSaveProductInProgress())
            Toasty.info(this, getString(R.string.save_product_in_progress), Toast.LENGTH_SHORT).show();
        else if (isUploadInProgress())
            Toasty.info(this, getString(R.string.upload_in_progress), Toast.LENGTH_SHORT).show();
        else {
            switch (view.getId()) {
                case R.id.btn_add_comment: {
                    if (isCommentValid()) {
                        hideSoftKeyboard();
                        Comment comment = new Comment();
                        comment.setContent(txtComment.getEditText().getText().toString().trim());
                        comment.setDate(new Date());
                        comment.setEmail(profile.getEmail());

                        showProgressBar();

                        viewModel.addComment(productId, comment)
                                .addOnCompleteListener(task -> hideProgressBar())
                                .addOnSuccessListener(documentReference -> {
                                    txtComment.getEditText().setText("");
                                })
                                .addOnFailureListener(e -> Toast.makeText(this, getString(R.string.failed_to_add_comment), Toast.LENGTH_SHORT).show());
                    }
                    break;
                }
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

    private void observeProfile() {
        String email = viewModel.getUser().getEmail();
        viewModel.getProfile(email).observe(this, profile -> {
            this.profile = profile;
        });
    }

    private void observeProduct(String id) {
        viewModel.getProductById(id).observe(this, product -> {
            if (product != null) {
                bindProduct(product);
            } else
                Toasty.error(this, getString(R.string.product_not_found), Toast.LENGTH_SHORT).show();
        });
    }

    private void observeComments(String productId) {
        viewModel.getCommentsByProductId(productId).observe(this, comments -> {
            if (comments != null) {
                // sort comments by date
                Collections.sort(comments, (o1, o2) -> o2.getDate().compareTo(o1.getDate()));
                commentAdapter.submitList(comments);
            }
        });
    }

    private void bindProduct(Product product) {
        this.product = product;
        setTitle(product.getTitle());
        if (!product.getThumbnailUri().isEmpty())
            imgThumbnail.loadImageUri(Uri.parse(product.getThumbnailUri()));

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
        txtPrice.getEditText().setText(Float.toString(product.getPrice()));

        if (product.getRating() != null) {
            String email = viewModel.getUser().getEmail();
            float rating = product.getRating().get(email) != null ? product.getRating().get(email) : 0f;
            ratingBar.setRating(rating);
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
        float price = Float.parseFloat(txtPrice.getEditText().getText().toString().trim());

        Product product = new Product();
        product.setId(id);
        product.setTitle(title);
        product.setArtist(artist);
        product.setType(Constant.PRODUCT_TYPE_MUSIC);
        product.setDescription(description);
        product.setGenre(spGenre.getSelectedItem().toString());
        product.setPrice(price);
        product.setThumbnailUri(thumbnailUri.toString());
        product.setMusicUri(musicUri.toString());

        saveProductTask = viewModel.saveProduct(product)
                .addOnCompleteListener(task -> hideProgressBar())
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, getString(R.string.product_saved), Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toasty.error(this, getString(R.string.failed_to_save_product), Toast.LENGTH_SHORT).show();
                    Log.e(TAG, e.getMessage());
                });
    }

    private void rateProduct(Product product, String email, float rate) {
        HashMap<String, Float> rating;
        if (product.getRating() == null)
            rating = new HashMap<>();
        else
            rating = product.getRating();

        rating.put(email, rate);
        viewModel.saveRating(product.getId(), rating)
                .addOnFailureListener(e -> Toast.makeText(EditMusicActivity.this, getString(R.string.failed_to_rate_product), Toast.LENGTH_SHORT).show());
    }

    /**
     * upload thumbnail file then save thumbnail uri in product
     *
     * @param id  product id
     * @param uri thumbnail uri
     */
    private void uploadThumbnail(String id, Uri uri) {
        imgThumbnail.showProgressBar();
        String filename = id + Constant.NAME_EXT_THUMBNAIL + getFileExtension(uri);
        UploadTask = viewModel.uploadThumbnail(uri, filename)
                .addOnSuccessListener(uri1 -> saveProductTask = viewModel.saveThumbnailUri(id, uri1)
                        .addOnCompleteListener(task -> imgThumbnail.hideProgressBar())
                        .addOnSuccessListener(aVoid -> Toasty.success(this, getString(R.string.thumbnail_saved), Toast.LENGTH_SHORT).show())
                        .addOnFailureListener(e -> {
                            Log.e(TAG, getString(R.string.failed_to_upload_thumbnail));
                            Toasty.error(this, e.getMessage(), Toast.LENGTH_LONG).show();
                        }))
                .addOnFailureListener(e -> {
                    imgThumbnail.hideProgressBar();
                    Log.e(TAG, getString(R.string.failed_to_upload_thumbnail));
                    Toasty.error(this, e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }


    /**
     * @param id  product id
     * @param uri music uri
     */
    private void uploadMusic(String id, Uri uri) {
        musicView.showProgressBar();
        String filename = id + Constant.NAME_EXT_MUSIC + getFileExtension(uri);
        UploadTask = viewModel.uploadMusic(uri, filename)
                .addOnSuccessListener(uri1 ->
                        saveProductTask = viewModel.saveMusicUri(id, uri1)
                                .addOnCompleteListener(task -> musicView.hideProgressBar())
                                .addOnSuccessListener(aVoid ->
                                        Toasty.success(this, getString(R.string.music_uploaded), Toast.LENGTH_SHORT).show())
                                .addOnFailureListener(e -> {
                                    Log.e(TAG, e.getMessage());
                                    Toasty.error(this, getString(R.string.failed_to_upload_music), Toast.LENGTH_LONG).show();
                                }))
                .addOnFailureListener(e -> {
                    musicView.hideProgressBar();
                    Log.e(TAG, e.getMessage());
                    Toasty.error(this, getString(R.string.failed_to_upload_music), Toast.LENGTH_LONG).show();
                });
    }

    /**
     * @param uri music uri
     */
    private void prepareMusicPlayer(Uri uri) {
        musicView.showProgressBar();

        // prepare music player
        TrackSelector trackSelector = new DefaultTrackSelector();
        musicPlayer = ExoPlayerFactory.newSimpleInstance(this, trackSelector);
        musicPlayer.addListener(new Player.DefaultEventListener() {
            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                super.onPlayerStateChanged(playWhenReady, playbackState);
                if (playbackState == Player.STATE_READY) {
                    musicView.hideProgressBar();
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

    private void initRecyclerView() {
        commentAdapter = new CommentAdapter();
        recComments.setHasFixedSize(true);
        recComments.setAdapter(commentAdapter);
        recComments.setLayoutManager(new LinearLayoutManager(this));
    }

    private boolean isTitleValid() {
        String title = txtTitle.getEditText().getText().toString().trim();
        if (title.isEmpty()) {
            txtTitle.setError(getString(R.string.title_cant_be_empty));
            return false;
        }
        txtTitle.setError("");
        return true;
    }

    private boolean isArtistValid() {
        String artist = txtArtist.getEditText().getText().toString().trim();
        if (artist.isEmpty()) {
            txtArtist.setError(getString(R.string.artist_cant_be_empty));
            return false;
        }
        txtArtist.setError("");
        return true;
    }

    private boolean isDescriptionValid() {
        String description = txtDescription.getEditText().getText().toString().trim();
        if (description.isEmpty()) {
            txtDescription.setError(getString(R.string.description_cant_be_empty));
            return false;
        }
        txtDescription.setError("");
        return true;
    }

    private boolean isCommentValid() {
        String comment = txtComment.getEditText().getText().toString().trim();
        if (comment.isEmpty()) {
            txtComment.setError(getString(R.string.comment_cant_be_empty));
            return false;
        }
        txtComment.setError("");
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
