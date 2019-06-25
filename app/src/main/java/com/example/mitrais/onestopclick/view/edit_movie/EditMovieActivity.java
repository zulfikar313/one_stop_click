package com.example.mitrais.onestopclick.view.edit_movie;

import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mitrais.onestopclick.Constant;
import com.example.mitrais.onestopclick.R;
import com.example.mitrais.onestopclick.custom_view.CustomImageView;
import com.example.mitrais.onestopclick.custom_view.CustomVideoView;
import com.example.mitrais.onestopclick.model.Product;
import com.example.mitrais.onestopclick.view.play_movie.PlayMovieActivity;
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

import java.util.HashMap;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;
import maes.tech.intentanim.CustomIntent;

public class EditMovieActivity extends AppCompatActivity {
    private static final String TAG = "EditMovieActivity";
    private static final int REQUEST_CHOOSE_THUMBNAIL = 1;
    private static final int REQUEST_CHOOSE_TRAILER = 2;
    private static final int REQUEST_CHOOSE_MOVIE = 3;
    private Task<Uri> UploadTask;
    private Task<Void> saveProductTask;
    private Uri thumbnailUri = Uri.parse("");
    private Uri trailerUri = Uri.parse("");
    private Uri movieUri = Uri.parse("");
    private ExoPlayer trailerPlayer;
    private String productId;
    private Product product;
    private boolean isAdmin;
    private ArrayAdapter<CharSequence> genreAdapter;

    @Inject
    EditMovieViewModel viewModel;

    @BindView(R.id.img_thumbnail)
    CustomImageView imgThumbnail;

    @BindView(R.id.txt_title)
    TextInputLayout txtTitle;

    @BindView(R.id.txt_director)
    TextInputLayout txtDirector;

    @BindView(R.id.txt_description)
    TextInputLayout txtDescription;

    @BindView(R.id.sp_genre)
    Spinner spGenre;

    @BindView(R.id.txt_price)
    TextInputLayout txtPrice;

    @BindView(R.id.rating_bar)
    RatingBar ratingBar;

    @BindView(R.id.btn_save)
    AppCompatButton btnSave;

    @BindView(R.id.trailer_view)
    CustomVideoView trailerView;

    @BindView(R.id.btn_upload_trailer)
    AppCompatButton btnUploadTrailer;

    @BindView(R.id.txt_movie_file_not_found)
    TextView txtMovieFileNotFound;

    @BindView(R.id.btn_upload_movie)
    AppCompatButton btnUploadMovie;

    @BindView(R.id.btn_play_movie)
    AppCompatButton btnPlayMovie;

    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_movie);
        ButterKnife.bind(this);
        initDagger();
        initSpinner();

        if (getIntent() != null) {
            productId = getIntent().getStringExtra(Constant.EXTRA_PRODUCT_ID);
            isAdmin = getIntent().getBooleanExtra(Constant.EXTRA_IS_ADMIN, false);
            observeProduct(productId);
        }

        if (!isAdmin) {
            txtTitle.getEditText().setEnabled(false);
            txtDirector.getEditText().setEnabled(false);
            txtDescription.getEditText().setEnabled(false);
            spGenre.setEnabled(false);
            btnSave.setVisibility(View.GONE);
            btnUploadTrailer.setVisibility(View.GONE);
            btnUploadMovie.setVisibility(View.GONE);
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

    @OnClick({R.id.btn_save, R.id.btn_upload_trailer, R.id.img_thumbnail, R.id.btn_upload_movie, R.id.btn_play_movie})
    void onButtonClicked(View view) {
        if (isSaveProductInProgress())
            Toasty.info(this, getString(R.string.save_product_in_progress), Toast.LENGTH_SHORT).show();
        else if (isUploadInProgress())
            Toasty.info(this, getString(R.string.upload_in_progress), Toast.LENGTH_SHORT).show();
        else {
            switch (view.getId()) {
                case R.id.btn_save:
                    if (isDirectorValid() & isTitleValid() & isDescriptionValid())
                        saveProduct(productId);
                    break;
                case R.id.btn_upload_trailer:
                    openTrailerFileChooser();
                    break;
                case R.id.btn_upload_movie:
                    openMovieFileChooser();
                    break;
                case R.id.btn_play_movie:
                    goToPlayMoviePage(movieUri);
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

        if (requestCode == REQUEST_CHOOSE_TRAILER && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            trailerUri = data.getData();
            uploadTrailer(productId, trailerUri);
        }

        if (requestCode == REQUEST_CHOOSE_MOVIE && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            movieUri = data.getData();
            uploadMovie(productId, movieUri);
        }
    }

    private void observeProduct(String id) {
        viewModel.getProductById(id).observe(this, product -> {
            if (product != null)
                bindProduct(product);
            else
                Toasty.error(this, getString(R.string.product_not_found), Toast.LENGTH_SHORT).show();
        });
    }

    private void bindProduct(Product product) {
        this.product = product;
        setTitle(product.getTitle());
        if (!product.getThumbnailUri().isEmpty()) {
            imgThumbnail.loadImageUri(Uri.parse(product.getThumbnailUri()));
        }

        txtTitle.getEditText().setText(product.getTitle());
        txtDirector.getEditText().setText(product.getDirector());

        if (product.getTrailerUri() != null && !product.getTrailerUri().isEmpty()) {
            trailerUri = Uri.parse(product.getTrailerUri());
            prepareTrailerPlayer(trailerUri);
        }

        if (product.getMovieUri() != null && !product.getMovieUri().isEmpty()) {
            movieUri = Uri.parse(product.getMovieUri());
            txtMovieFileNotFound.setVisibility(View.GONE);
            btnUploadMovie.setText(getString(R.string.reupload_movie));
            btnPlayMovie.setVisibility(View.VISIBLE);
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
        String director = txtDirector.getEditText().getText().toString().trim();
        String description = txtDescription.getEditText().getText().toString().trim();
        float price = Float.parseFloat(txtPrice.getEditText().getText().toString().trim());

        Product product = new Product();
        product.setId(id);
        product.setTitle(title);
        product.setDirector(director);
        product.setType(Constant.PRODUCT_TYPE_MOVIE);
        product.setDescription(description);
        product.setGenre(spGenre.getSelectedItem().toString());
        product.setPrice(price);
        product.setThumbnailUri(thumbnailUri.toString());
        product.setTrailerUri(trailerUri.toString());

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
                .addOnFailureListener(e -> Toast.makeText(EditMovieActivity.this, getString(R.string.failed_to_rate_product), Toast.LENGTH_SHORT).show());
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
     * @param uri trailer uri
     */
    private void uploadTrailer(String id, Uri uri) {
        trailerView.showProgressBar();

        String filename = id + Constant.NAME_EXT_TRAILER + getFileExtension(uri);
        UploadTask = viewModel.uploadTrailer(uri, filename)
                .addOnSuccessListener(uri1 ->
                        saveProductTask = viewModel.saveTrailerUri(id, uri1)
                                .addOnCompleteListener(task -> trailerView.hideProgressBar())
                                .addOnSuccessListener(aVoid -> Toasty.success(this, getString(R.string.trailer_uploaded), Toast.LENGTH_SHORT).show())
                                .addOnFailureListener(e -> {
                                    Toasty.error(this, getString(R.string.failed_to_upload_trailer), Toast.LENGTH_LONG).show();
                                    Log.e(TAG, e.getMessage());
                                }))
                .addOnFailureListener(e -> {
                    trailerView.hideProgressBar();
                    Toasty.error(this, getString(R.string.failed_to_upload_trailer), Toast.LENGTH_LONG).show();
                    Log.e(TAG, e.getMessage());
                });
    }

    /**
     * @param id  product id
     * @param uri trailer uri
     */
    private void uploadMovie(String id, Uri uri) {
        showProgressBar();

        String filename = id + Constant.NAME_EXT_MOVIE + getFileExtension(uri);
        UploadTask = viewModel.uploadMovie(uri, filename)
                .addOnSuccessListener(uri1 ->
                        saveProductTask = viewModel.saveMovieUri(id, uri1)
                                .addOnCompleteListener(task -> hideProgressBar())
                                .addOnSuccessListener(aVoid -> Toasty.success(this, getString(R.string.movie_uploaded), Toast.LENGTH_SHORT).show())
                                .addOnFailureListener(e -> {
                                    Toasty.error(this, getString(R.string.failed_to_upload_movie), Toast.LENGTH_LONG).show();
                                    Log.e(TAG, e.getMessage());
                                }))
                .addOnFailureListener(e -> {
                    hideProgressBar();
                    Toasty.error(this, getString(R.string.failed_to_upload_movie), Toast.LENGTH_LONG).show();
                    Log.e(TAG, e.getMessage());
                });
    }

    /**
     * @param uri trailer uri
     */
    private void prepareTrailerPlayer(Uri uri) {
        trailerView.showProgressBar();

        // prepare video player
        TrackSelector trackSelector = new DefaultTrackSelector();
        trailerPlayer = ExoPlayerFactory.newSimpleInstance(this, trackSelector);
        trailerPlayer.addListener(new Player.DefaultEventListener() {
            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                super.onPlayerStateChanged(playWhenReady, playbackState);
                if (playbackState == Player.STATE_READY) {
                    trailerView.hideProgressBar();
                }
            }
        });

        // prepare video source
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this, Util.getUserAgent(this, getPackageName()));
        MediaSource videoSource = new ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(uri);

        // configure video player
        trailerPlayer.prepare(videoSource);
        trailerView.setPlayer(trailerPlayer);
    }

    private void goToPlayMoviePage(Uri movieUri) {
        Intent intent = new Intent(this, PlayMovieActivity.class);
        intent.putExtra(Constant.EXTRA_MOVIE_URI, movieUri.toString());
        startActivity(intent);
    }

    private void openThumbnailFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, REQUEST_CHOOSE_THUMBNAIL);
    }

    private void openTrailerFileChooser() {
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, REQUEST_CHOOSE_TRAILER);
    }

    private void openMovieFileChooser() {
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, REQUEST_CHOOSE_MOVIE);
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
        EditMovieActivityComponent component = DaggerEditMovieActivityComponent.builder()
                .editMovieActivity(this)
                .build();
        component.inject(this);
    }

    /**
     * initialize genre spinner
     */
    private void initSpinner() {
        genreAdapter = ArrayAdapter.createFromResource(this, R.array.movie_or_book_genre, R.layout.genre_spinner_item);
        genreAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spGenre.setAdapter(genreAdapter);
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

    private boolean isDirectorValid() {
        String director = txtDirector.getEditText().getText().toString().trim();
        if (director.isEmpty()) {
            txtDirector.setError(getString(R.string.director_cant_be_empty));
            return false;
        }
        txtDirector.setError("");
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
