package com.example.mitrais.onestopclick.view.product_detail;

import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mitrais.onestopclick.Constant;
import com.example.mitrais.onestopclick.R;
import com.example.mitrais.onestopclick.model.Product;
import com.example.mitrais.onestopclick.view.read_book.ReadBookActivity;
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
import com.google.firebase.firestore.DocumentReference;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;
import io.supercharge.shimmerlayout.ShimmerLayout;
import maes.tech.intentanim.CustomIntent;

public class ProductDetailActivity extends AppCompatActivity {
    private static final String TAG = "ProductDetailActivity";
    private static final int REQUEST_CHOOSE_IMAGE = 1;
    private static final int REQUEST_CHOOSE_BOOK = 2;
    private static final int REQUEST_CHOOSE_MUSIC = 3;
    private static final int REQUEST_CHOOSE_TRAILER = 4;
    private String productId;
    private Task<Uri> UploadTask;
    private Task<Void> saveProductTask;
    private Task<DocumentReference> addProductTask;
    private Uri thumbnailUri = Uri.parse("");
    private Uri bookUri = Uri.parse("");
    private Uri trailerUri = Uri.parse("");
    private Uri musicUri = Uri.parse("");
    private ExoPlayer musicPlayer;
    private Boolean isMusicPlaying;

    @Inject
    ProductDetailViewModel viewModel;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.shimmer_layout)
    ShimmerLayout shimmerLayout;

    @BindView(R.id.img_thumbnail)
    ImageView imgThumbnail;

    @BindView(R.id.rg_type)
    RadioGroup rgType;

    @BindView(R.id.rb_book)
    RadioButton rbBook;

    @BindView(R.id.rb_music)
    RadioButton rbMusic;

    @BindView(R.id.rb_movie)
    RadioButton rbMovie;

    @BindView(R.id.txt_title)
    TextInputLayout txtTitle;

    @BindView(R.id.txt_author)
    TextInputLayout txtAuthor;

    @BindView(R.id.txt_artist)
    TextInputLayout txtArtist;

    @BindView(R.id.movie_container)
    LinearLayout movieContainer;

    @BindView(R.id.music_edit_container)
    ConstraintLayout musicEditContainer;

    @BindView(R.id.img_music_sheet)
    ImageView imgMusicSheet;

    @BindView(R.id.btn_play)
    ImageButton btnPlay;

    @BindView(R.id.txt_director)
    TextInputLayout txtDirector;

    @BindView(R.id.movie_edit_container)
    ConstraintLayout movieEditContainer;

    @BindView(R.id.trailer_view)
    PlayerView trailerView;

    @BindView(R.id.btn_upload_trailer)
    AppCompatButton btnSetTrailer;

    @BindView(R.id.book_edit_container)
    ConstraintLayout bookEditContainer;

    @BindView(R.id.book_not_found_view)
    LinearLayout bookNotFoundView;

    @BindView(R.id.txt_book_filename)
    TextView txtBookFilename;

    @BindView(R.id.book_found_view)
    LinearLayout bookFoundView;

    @BindView(R.id.txt_description)
    TextInputLayout txtDescription;

    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    @BindView(R.id.book_progress_bar)
    ProgressBar bookProgressBar;

    @BindView(R.id.music_progress_bar)
    ProgressBar musicProgressBar;

    @BindView(R.id.trailer_progress_bar)
    ProgressBar trailerProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);
        ButterKnife.bind(this);
        initDagger();
        initToolbar();
        productId = getIntent().getStringExtra(Constant.EXTRA_PRODUCT_ID);
        if (productId != null && !productId.isEmpty()) {
            imgThumbnail.setVisibility(View.VISIBLE);
            observeProduct(productId);
        }
    }

    @Override
    public void onBackPressed() {
        if (isSaveProductInProgress() || isAddProductInProgress())
            Toasty.info(this, getString(R.string.save_product_in_progress), Toast.LENGTH_SHORT).show();
        else if (isUploadInProgress())
            Toasty.info(this, getString(R.string.upload_in_progress), Toast.LENGTH_SHORT).show();
        else {
            if (musicPlayer != null && isMusicPlaying)
                musicPlayer.setPlayWhenReady(false);

            super.onBackPressed();
        }


    }

    @Override
    protected void onPause() {
        super.onPause();
        CustomIntent.customType(this, Constant.ANIMATION_FADEIN_TO_FADEOUT);
    }

    @OnClick(R.id.img_thumbnail)
    void onThumbnailImageClicked() {
        if (isSaveProductInProgress() || isAddProductInProgress())
            Toasty.info(this, getString(R.string.save_product_in_progress), Toast.LENGTH_SHORT).show();
        else if (isUploadInProgress())
            Toasty.info(this, getString(R.string.upload_in_progress), Toast.LENGTH_SHORT).show();
        else
            openImageFileChooser();
    }

    @OnClick(R.id.btn_save)
    void onSaveButtonClicked() {
        if (isSaveProductInProgress() || isAddProductInProgress())
            Toasty.info(this, getString(R.string.save_product_in_progress), Toast.LENGTH_SHORT).show();
        else if (isUploadInProgress())
            Toasty.info(this, getString(R.string.upload_in_progress), Toast.LENGTH_SHORT).show();
        else {
            hideSoftKeyboard();
            switch (rgType.getCheckedRadioButtonId()) {
                case R.id.rb_book: {
                    if (isAuthorValid() & isTitleValid() & isDescriptionValid())
                        saveBook();
                    break;
                }
                case R.id.rb_music: {
                    if (isArtistValid() & isTitleValid() & isDescriptionValid())
                        saveMusic();
                    break;
                }
                case R.id.rb_movie: {
                    if (isDirectorValid() & isTitleValid() & isDescriptionValid())
                        saveMovie();
                    break;
                }
            }
        }
    }

    @OnClick(R.id.btn_play)
    void onPlayButtonClicked() {
        if (musicPlayer != null) {
            if (!isMusicPlaying) {
                musicPlayer.setPlayWhenReady(true);
                btnPlay.setBackgroundResource(R.drawable.ic_pause);
            } else {
                musicPlayer.setPlayWhenReady(false);
                btnPlay.setBackgroundResource(R.drawable.ic_play_arrow);
            }
        }
    }

    @OnClick(R.id.btn_read_book)
    void onReadBookButtonClicked() {
        if (isSaveProductInProgress() || isAddProductInProgress())
            Toasty.info(this, getString(R.string.save_product_in_progress), Toast.LENGTH_SHORT).show();
        else if (isUploadInProgress())
            Toasty.info(this, getString(R.string.upload_in_progress), Toast.LENGTH_SHORT).show();
        else
            goToReadBookPage();
    }

    @OnClick(R.id.btn_upload_book)
    void onUploadBookButtonClicked() {
        if (isSaveProductInProgress() || isAddProductInProgress())
            Toasty.info(this, getString(R.string.save_product_in_progress), Toast.LENGTH_SHORT).show();
        else if (isUploadInProgress())
            Toasty.info(this, getString(R.string.upload_in_progress), Toast.LENGTH_SHORT).show();
        else
            openBookFileChooser();
    }

    @OnClick(R.id.btn_edit_book)
    void onEditBookButtonClicked() {
        if (isSaveProductInProgress() || isAddProductInProgress())
            Toasty.info(this, getString(R.string.save_product_in_progress), Toast.LENGTH_SHORT).show();
        else if (isUploadInProgress())
            Toasty.info(this, getString(R.string.upload_in_progress), Toast.LENGTH_SHORT).show();
        else
            openBookFileChooser();
    }

    @OnClick(R.id.btn_upload_music)
    void onUploadMusicButtonClicked() {
        if (isSaveProductInProgress() || isAddProductInProgress())
            Toasty.info(this, getString(R.string.save_product_in_progress), Toast.LENGTH_SHORT).show();
        else if (isUploadInProgress())
            Toasty.info(this, getString(R.string.upload_in_progress), Toast.LENGTH_SHORT).show();
        else
            openMusicFileChooser();
    }


    @OnClick(R.id.btn_upload_trailer)
    void onUploadTrailerButtonClicked() {
        if (isSaveProductInProgress() || isAddProductInProgress())
            Toasty.info(this, getString(R.string.save_product_in_progress), Toast.LENGTH_SHORT).show();
        else if (isUploadInProgress())
            Toasty.info(this, getString(R.string.upload_in_progress), Toast.LENGTH_SHORT).show();
        else
            openTrailerFileChooser();
    }

    @OnCheckedChanged({R.id.rb_book, R.id.rb_music, R.id.rb_movie})
    void onRadioButtonCheckChanged(CompoundButton button, boolean checked) {
        if (checked) {
            switch (button.getId()) {
                case R.id.rb_book: {
                    setBookView();
                    break;
                }
                case R.id.rb_music: {
                    setMusicView();
                    break;
                }
                case R.id.rb_movie: {
                    setMovieView();
                    break;
                }
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CHOOSE_IMAGE && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            thumbnailUri = data.getData();
            uploadThumbnail();
        }

        if (requestCode == REQUEST_CHOOSE_BOOK && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            bookUri = data.getData();
            uploadBook();
        }

        if (requestCode == REQUEST_CHOOSE_MUSIC && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            musicUri = data.getData();
            uploadMusic();
        }

        if (requestCode == REQUEST_CHOOSE_TRAILER && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            trailerUri = data.getData();
            uploadTrailer();
        }
    }

    /**
     * initialize dagger injection
     */
    private void initDagger() {
        ProductDetailActivityComponent component = DaggerProductDetailActivityComponent.builder()
                .productDetailActivity(this)
                .build();
        component.inject(this);
    }

    /**
     * initialize toolbar
     */
    private void initToolbar() {
        setSupportActionBar(toolbar);
        toolbar.setTitle(getString(R.string.edit_product));
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
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
        txtAuthor.getEditText().setText(product.getAuthor());
        txtArtist.getEditText().setText(product.getArtist());
        txtDirector.getEditText().setText(product.getDirector());

        switch (product.getType()) {
            case Constant.PRODUCT_TYPE_BOOK: {
                rbBook.setChecked(true);
                setBookView();
                rbMusic.setEnabled(false);
                rbMovie.setEnabled(false);

                if (product.getBookUri() != null && !product.getBookUri().isEmpty()) {
                    bookUri = Uri.parse(product.getBookUri());
                    txtBookFilename.setText(product.getTitle());
                    bookNotFoundView.setVisibility(View.GONE);
                    bookFoundView.setVisibility(View.VISIBLE);
                }

                break;
            }
            case Constant.PRODUCT_TYPE_MUSIC: {
                rbMusic.setChecked(true);
                rbBook.setEnabled(false);
                rbMovie.setEnabled(false);

                if (product.getMusicUri() != null && !product.getMusicUri().isEmpty()) {
                    musicUri = Uri.parse(product.getMusicUri());
                    prepareMusicPlayer(Uri.parse(product.getMusicUri()));
                }
                break;
            }
            case Constant.PRODUCT_TYPE_MOVIE: {
                rbMovie.setChecked(true);
                rbMusic.setEnabled(false);
                rbBook.setEnabled(false);

                if (product.getTrailerUri() != null && !product.getTrailerUri().isEmpty()) {
                    trailerUri = Uri.parse(product.getTrailerUri());
                    prepareTrailerPlayer(trailerUri);
                }

                break;
            }
            default: {
                break;
            }
        }
        txtDescription.getEditText().setText(product.getDescription());
    }

    private void saveBook() {
        showProgressBar();
        String title = txtTitle.getEditText().getText().toString().trim();
        String author = txtAuthor.getEditText().getText().toString().trim();
        String description = txtDescription.getEditText().getText().toString().trim();

        Product product = new Product();
        product.setTitle(title);
        product.setAuthor(author);
        product.setType(Constant.PRODUCT_TYPE_BOOK);
        product.setDescription(description);
        product.setThumbnailUri(thumbnailUri.toString());
        product.setBookUri(bookUri.toString());

        if (productId != null && !productId.isEmpty()) {
            product.setId(productId);
            saveProductTask = viewModel.saveProduct(product)
                    .addOnCompleteListener(task -> hideProgressBar())
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(ProductDetailActivity.this, getString(R.string.product_has_been_saved), Toast.LENGTH_SHORT).show();
                        onBackPressed();
                    })
                    .addOnFailureListener(e -> {
                        Toasty.error(ProductDetailActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e(TAG, getString(R.string.failed_to_save_product));
                    });
        } else { /* add new product*/
            addProductTask = viewModel.addProduct(product)
                    .addOnCompleteListener(task -> hideProgressBar())
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(ProductDetailActivity.this, getString(R.string.product_has_been_saved), Toast.LENGTH_SHORT).show();
                        onBackPressed();
                    })
                    .addOnFailureListener(e -> {
                        Toasty.error(ProductDetailActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e(TAG, getString(R.string.failed_to_save_product));
                    });
        }
    }

    /**
     * save music product
     */
    private void saveMusic() {
        showProgressBar();
        String title = txtTitle.getEditText().getText().toString().trim();
        String artist = txtArtist.getEditText().getText().toString().trim();
        String description = txtDescription.getEditText().getText().toString().trim();

        Product product = new Product();
        product.setTitle(title);
        product.setArtist(artist);
        product.setType(Constant.PRODUCT_TYPE_MUSIC);
        product.setDescription(description);
        product.setThumbnailUri(thumbnailUri.toString());
        product.setMusicUri(musicUri.toString());

        if (productId != null && !productId.isEmpty()) {
            product.setId(productId);
            saveProductTask = viewModel.saveProduct(product)
                    .addOnCompleteListener(task -> hideProgressBar())
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(ProductDetailActivity.this, getString(R.string.product_has_been_saved), Toast.LENGTH_SHORT).show();
                        onBackPressed();
                    })
                    .addOnFailureListener(e -> {
                        Toasty.error(ProductDetailActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e(TAG, getString(R.string.failed_to_save_product));
                    });
        } else {
            addProductTask = viewModel.addProduct(product)
                    .addOnCompleteListener(task -> hideProgressBar())
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(ProductDetailActivity.this, getString(R.string.product_has_been_saved), Toast.LENGTH_SHORT).show();
                        onBackPressed();
                    })
                    .addOnFailureListener(e -> {
                        Toasty.error(ProductDetailActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e(TAG, getString(R.string.failed_to_save_product));
                    });
        }
    }

    /**
     * save movie product
     */
    private void saveMovie() {
        showProgressBar();

        String title = txtTitle.getEditText().getText().toString().trim();
        String director = txtDirector.getEditText().getText().toString().trim();
        String description = txtDescription.getEditText().getText().toString().trim();

        Product product = new Product();
        product.setTitle(title);
        product.setDirector(director);
        product.setType(Constant.PRODUCT_TYPE_MOVIE);
        product.setDescription(description);
        product.setThumbnailUri(thumbnailUri.toString());
        product.setTrailerUri(trailerUri.toString());

        if (productId != null && !productId.isEmpty()) {
            product.setId(productId);
            saveProductTask = viewModel.saveProduct(product)
                    .addOnCompleteListener(task -> hideProgressBar())
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(ProductDetailActivity.this, getString(R.string.product_has_been_saved), Toast.LENGTH_SHORT).show();
                        onBackPressed();
                    })
                    .addOnFailureListener(e -> {
                        Toasty.error(ProductDetailActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e(TAG, getString(R.string.failed_to_save_product));
                    });
        } else {
            addProductTask = viewModel.addProduct(product)
                    .addOnCompleteListener(task -> hideProgressBar())
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(ProductDetailActivity.this, getString(R.string.product_has_been_saved), Toast.LENGTH_SHORT).show();
                        onBackPressed();
                    })
                    .addOnFailureListener(e -> {
                        Toasty.error(ProductDetailActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e(TAG, getString(R.string.failed_to_save_product));
                    });
        }
    }

    /**
     * upload thumbnail
     */
    private void uploadThumbnail() {
        showProgressBar();
        String filename = productId + "thmb." + getFileExtension(thumbnailUri);
        UploadTask = viewModel.uploadThumbnail(thumbnailUri, filename)
                .addOnSuccessListener(uri -> {
                    saveProductTask = viewModel.saveThumbnailUri(productId, uri)
                            .addOnCompleteListener(task -> hideProgressBar())
                            .addOnSuccessListener(aVoid -> Toasty.success(ProductDetailActivity.this, getString(R.string.image_has_been_saved), Toast.LENGTH_SHORT).show())
                            .addOnFailureListener(e -> {
                                Log.e(TAG, getString(R.string.failed_to_upload_thumbnail));
                                Toasty.error(ProductDetailActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                            });
                })
                .addOnFailureListener(e -> {
                    hideProgressBar();
                    Log.e(TAG, getString(R.string.failed_to_upload_thumbnail));
                    Toasty.error(ProductDetailActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    /**
     * upload book file
     */
    private void uploadBook() {
        bookProgressBar.setVisibility(View.VISIBLE);

        String filename = productId + "book." + getFileExtension(bookUri);
        UploadTask = viewModel.uploadBook(bookUri, filename)
                .addOnSuccessListener(uri ->
                        saveProductTask = viewModel.saveBookUri(productId, uri)
                                .addOnCompleteListener(task -> bookProgressBar.setVisibility(View.INVISIBLE))
                                .addOnSuccessListener(aVoid ->
                                {
                                    Toasty.success(ProductDetailActivity.this, getString(R.string.book_has_been_uploaded), Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> {
                                    Log.e(TAG, "Update product book uri failed");
                                    Toasty.error(ProductDetailActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                }))
                .addOnFailureListener(e -> {
                    hideProgressBar();
                    Log.e(TAG, "Upload product book failed");
                    Toasty.error(ProductDetailActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    /**
     * upload movie trailer
     */
    private void uploadMusic() {
        musicProgressBar.setVisibility(View.VISIBLE);
        btnPlay.setVisibility(View.GONE);

        String filename = productId + "msc." + getFileExtension(musicUri);
        UploadTask = viewModel.uploadMusic(musicUri, filename)
                .addOnSuccessListener(uri ->
                        saveProductTask = viewModel.saveProductMusic(productId, uri)
                                .addOnCompleteListener(task -> musicProgressBar.setVisibility(View.INVISIBLE))
                                .addOnSuccessListener(aVoid ->
                                {
                                    Toasty.success(ProductDetailActivity.this, getString(R.string.music_uploaded), Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> {
                                    Log.e(TAG, getString(R.string.error_failed_to_upload_music));
                                    Toasty.error(ProductDetailActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                }))
                .addOnFailureListener(e -> {
                    hideProgressBar();
                    Log.e(TAG, getString(R.string.error_failed_to_upload_music));
                    Toasty.error(ProductDetailActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    /**
     * upload movie trailer
     */
    private void uploadTrailer() {
        trailerProgressBar.setVisibility(View.VISIBLE);

        String filename = productId + "tr1." + getFileExtension(trailerUri);
        UploadTask = viewModel.uploadTrailer(trailerUri, filename)
                .addOnSuccessListener(uri1 ->
                        saveProductTask = viewModel.saveProductTrailer(productId, uri1)
                                .addOnCompleteListener(task -> trailerProgressBar.setVisibility(View.INVISIBLE))
                                .addOnSuccessListener(aVoid ->
                                {
                                    Toasty.success(ProductDetailActivity.this, getString(R.string.trailer_uploaded), Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> {
                                    Log.e(TAG, getString(R.string.error_failed_to_upload_trailer));
                                    Toasty.error(ProductDetailActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                }))
                .addOnFailureListener(e -> {
                    hideProgressBar();
                    Log.e(TAG, getString(R.string.error_failed_to_upload_trailer));
                    Toasty.error(ProductDetailActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    /**
     * @param uri music uri
     */
    private void prepareMusicPlayer(Uri uri) {
        musicProgressBar.setVisibility(View.VISIBLE);
        // prepare music player
        TrackSelector trackSelector = new DefaultTrackSelector();
        musicPlayer = ExoPlayerFactory.newSimpleInstance(this, trackSelector);
        musicPlayer.addListener(new Player.DefaultEventListener() {
            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                super.onPlayerStateChanged(playWhenReady, playbackState);
                isMusicPlaying = playWhenReady && playbackState == Player.STATE_READY;
                if (playbackState == Player.STATE_READY) {
                    musicProgressBar.setVisibility(View.INVISIBLE);
                    btnPlay.setVisibility(View.VISIBLE);
                }
            }
        });

        // prepare music data source
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this, Util.getUserAgent(this, getPackageName()));
        MediaSource musicSource = new ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(uri);

        // configure music player
        musicPlayer.prepare(musicSource);
    }

    private void prepareTrailerPlayer(Uri uri) {
        trailerProgressBar.setVisibility(View.VISIBLE);

        // prepare video player
        TrackSelector trackSelector = new DefaultTrackSelector();
        ExoPlayer videoPlayer = ExoPlayerFactory.newSimpleInstance(this, trackSelector);
        videoPlayer.addListener(new Player.DefaultEventListener() {
            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                super.onPlayerStateChanged(playWhenReady, playbackState);
                if (playbackState == Player.STATE_READY) {
                    trailerProgressBar.setVisibility(View.INVISIBLE);
                    trailerView.setVisibility(View.VISIBLE);
                }
            }
        });

        // prepare video source
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this, Util.getUserAgent(this, getPackageName()));
        MediaSource videoSource = new ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(uri);

        // configure video player
        videoPlayer.prepare(videoSource);
        trailerView.setPlayer(videoPlayer);
    }

    private void goToReadBookPage() {
        showProgressBar();
        Uri bookLocalUri = Uri.parse(getFilesDir() + productId + "book.pdf");
        String filename = productId + "book.pdf";
        viewModel.downloadBook(bookLocalUri, filename)
                .addOnCompleteListener(task -> hideProgressBar())
                .addOnSuccessListener(taskSnapshot -> {
                    Intent intent = new Intent(ProductDetailActivity.this, ReadBookActivity.class);
                    intent.putExtra(Constant.EXTRA_BOOK_URI, bookLocalUri.toString());
                    startActivity(intent);
                })
                .addOnFailureListener(e -> {
                    String errorMessage = getString(R.string.failed_to_download_book) + ": " + e.getMessage();
                    Toast.makeText(ProductDetailActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, errorMessage);
                });
    }

    private void openImageFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, REQUEST_CHOOSE_IMAGE);
    }

    private void openBookFileChooser() {
        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, REQUEST_CHOOSE_BOOK);
    }

    private void openMusicFileChooser() {
        Intent intent = new Intent();
        intent.setType("audio/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, REQUEST_CHOOSE_MUSIC);
    }

    private void openTrailerFileChooser() {
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, REQUEST_CHOOSE_TRAILER);
    }

    private String getFileExtension(Uri uri) {
        ContentResolver resolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(resolver.getType(uri));
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

    private boolean isAuthorValid() {
        String author = txtAuthor.getEditText().getText().toString().trim();
        if (author.isEmpty()) {
            txtAuthor.setError(getString(R.string.error_empty_author));
            return false;
        }
        txtAuthor.setError("");
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

    private boolean isDirectorValid() {
        String director = txtDirector.getEditText().getText().toString().trim();
        if (director.isEmpty()) {
            txtDirector.setError(getString(R.string.error_empty_director));
            return false;
        }
        txtDirector.setError("");
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

    private boolean isAddProductInProgress() {
        return addProductTask != null && !addProductTask.isComplete();
    }


    private boolean isUploadInProgress() {
        return UploadTask != null && !UploadTask.isComplete();
    }

    /**
     * show book exclusive view
     */
    private void setBookView() {
        txtAuthor.setVisibility(View.VISIBLE);
        txtArtist.setVisibility(View.GONE);
        movieContainer.setVisibility(View.GONE);
        if (productId != null && !productId.isEmpty())
            bookEditContainer.setVisibility(View.VISIBLE);
    }

    /**
     * show music exclusive views
     */
    private void setMusicView() {
        txtAuthor.setVisibility(View.GONE);
        txtArtist.setVisibility(View.VISIBLE);
        movieContainer.setVisibility(View.GONE);
        if (productId != null && !productId.isEmpty())
            musicEditContainer.setVisibility(View.VISIBLE);
    }

    /**
     * show movie exclusive views
     */
    private void setMovieView() {
        txtAuthor.setVisibility(View.GONE);
        txtArtist.setVisibility(View.GONE);
        movieContainer.setVisibility(View.VISIBLE);
        if (productId != null && !productId.isEmpty())
            movieEditContainer.setVisibility(View.VISIBLE);
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
