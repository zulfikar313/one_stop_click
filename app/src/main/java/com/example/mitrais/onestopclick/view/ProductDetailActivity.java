package com.example.mitrais.onestopclick.view;

import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
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
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.mitrais.onestopclick.Constant;
import com.example.mitrais.onestopclick.R;
import com.example.mitrais.onestopclick.dagger.component.DaggerProductDetailActivityComponent;
import com.example.mitrais.onestopclick.dagger.component.ProductDetailActivityComponent;
import com.example.mitrais.onestopclick.model.Product;
import com.example.mitrais.onestopclick.viewmodel.ProductDetailViewModel;
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
    private static final int REQUEST_CHOOSE_MUSIC = 2;
    private static final int REQUEST_CHOOSE_TRAILER = 3;
    private String productId;
    private Task<Uri> uploadImageTask;
    private Task<Uri> uploadTrailerTask;
    private Task<Void> saveProductTask;
    private Task<DocumentReference> addProductTask;
    private Uri thumbnailUri;
    private Uri trailerUri;
    private Uri musicUri;
    private MediaPlayer musicPlayer;

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
    LinearLayout musicEditContainer;

    @BindView(R.id.img_music_sheet)
    ImageView imgMusicSheet;

    @BindView(R.id.btn_play)
    ImageButton btnPlay;

    @BindView(R.id.movie_edit_container)
    LinearLayout movieEditContainer;

    @BindView(R.id.txt_director)
    TextInputLayout txtDirector;

    @BindView(R.id.trailer_container)
    FrameLayout trailerContainer;

    @BindView(R.id.vid_trailer)
    VideoView vidTrailer;

    @BindView(R.id.btn_upload_trailer)
    AppCompatButton btnSetTrailer;

    @BindView(R.id.txt_description)
    TextInputLayout txtDescription;

    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

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
            Toasty.info(this, getString(R.string.save_product_is_in_progress), Toast.LENGTH_SHORT).show();
        else if (isUploadThumbnailInProgress())
            Toasty.info(this, getString(R.string.upload_thumbnail_is_in_progress), Toast.LENGTH_SHORT).show();
        else if (isUploadTrailerInProgress())
            Toasty.info(this, getString(R.string.upload_trailer_is_in_progress), Toast.LENGTH_SHORT).show();
        else {
            if (musicPlayer != null && musicPlayer.isPlaying())
                musicPlayer.stop();

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
            Toasty.info(this, getString(R.string.save_product_is_in_progress), Toast.LENGTH_SHORT).show();
        else if (isUploadThumbnailInProgress())
            Toasty.info(this, getString(R.string.upload_thumbnail_is_in_progress), Toast.LENGTH_SHORT).show();
        else if (isUploadTrailerInProgress())
            Toasty.info(this, getString(R.string.upload_trailer_is_in_progress), Toast.LENGTH_SHORT).show();
        else
            openImageFileChooser();
    }

    @OnClick(R.id.btn_save)
    void onSaveButtonClicked() {
        if (isSaveProductInProgress() || isAddProductInProgress())
            Toasty.info(this, getString(R.string.save_product_is_in_progress), Toast.LENGTH_SHORT).show();
        else if (isUploadTrailerInProgress())
            Toasty.info(this, getString(R.string.upload_trailer_is_in_progress), Toast.LENGTH_SHORT).show();
        else if (isUploadThumbnailInProgress())
            Toasty.info(this, getString(R.string.upload_thumbnail_is_in_progress), Toast.LENGTH_SHORT).show();
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
        if (musicPlayer.isPlaying()) {
            musicPlayer.pause();
            btnPlay.setBackgroundResource(R.drawable.ic_play_arrow);
        } else {
            musicPlayer.start();
            btnPlay.setBackgroundResource(R.drawable.ic_pause);
        }
    }

    @OnClick(R.id.btn_upload_music)
    void onUploadMusicButtonClicked() {
        if (isSaveProductInProgress() || isAddProductInProgress())
            Toasty.info(this, getString(R.string.save_product_is_in_progress), Toast.LENGTH_SHORT).show();
        else if (isUploadThumbnailInProgress())
            Toasty.info(this, getString(R.string.upload_thumbnail_is_in_progress), Toast.LENGTH_SHORT).show();
        else
            openMusicFileChooser();
    }


    @OnClick(R.id.btn_upload_trailer)
    void onUploadTrailerButtonClicked() {
        if (isSaveProductInProgress() || isAddProductInProgress())
            Toasty.info(this, getString(R.string.save_product_is_in_progress), Toast.LENGTH_SHORT).show();
        else if (isUploadTrailerInProgress())
            Toasty.info(this, getString(R.string.upload_trailer_is_in_progress), Toast.LENGTH_SHORT).show();
        else if (isUploadThumbnailInProgress())
            Toasty.info(this, getString(R.string.upload_thumbnail_is_in_progress), Toast.LENGTH_SHORT).show();
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
            Uri uri = data.getData();
            thumbnailUri = uri;
            uploadThumbnail();
        }

        if (requestCode == REQUEST_CHOOSE_MUSIC && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            musicUri = data.getData();
            musicPlayer = MediaPlayer.create(this, musicUri);
            musicPlayer.setVolume(0.5f, 0.5f);
            musicPlayer.setLooping(true);
            btnPlay.setVisibility(View.VISIBLE);
            uploadMusic();
        }

        if (requestCode == REQUEST_CHOOSE_TRAILER && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            trailerUri = data.getData();
            vidTrailer.setVideoURI(data.getData());
            vidTrailer.setMediaController(new MediaController(this));
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

    /**
     * @param id product id
     */
    private void observeProduct(String id) {
        viewModel.getProductById(id).observe(this, product -> {
            if (product != null) {
                bindProduct(product);
            } else
                Toasty.error(this, getString(R.string.error_product_not_found), Toast.LENGTH_SHORT).show();
        });
    }

    /**
     * bind product data to view
     *
     * @param product product objects
     */
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
                rbMusic.setEnabled(false);
                rbMovie.setEnabled(false);
                break;
            }
            case Constant.PRODUCT_TYPE_MUSIC: {
                rbMusic.setChecked(true);
                rbBook.setEnabled(false);
                rbMovie.setEnabled(false);

                if (product.getMusicUri() != null && !product.getMusicUri().isEmpty()) {
//                    new AsyncTask<Void, Void, Void>() {
//
//                        @Override
//                        protected Void doInBackground(Void... voids) {
//                            musicUri = Uri.parse(product.getMusicUri());
//                            musicPlayer = MediaPlayer.create(ProductDetailActivity.this, musicUri);
//                            musicPlayer.setVolume(0.5f, 0.5f);
//                            musicPlayer.setLooping(true);
//                            btnPlay.setVisibility(View.VISIBLE);
//                            return null;
//                        }
//                    }.execute();
                }
                break;
            }
            case Constant.PRODUCT_TYPE_MOVIE: {
                rbMovie.setChecked(true);
                rbMusic.setEnabled(false);
                rbBook.setEnabled(false);

                if (product.getTrailerUri() != null && !product.getTrailerUri().isEmpty()) {
                    trailerUri = Uri.parse(product.getTrailerUri());
                    vidTrailer.setVideoURI(Uri.parse(product.getTrailerUri()));
                    vidTrailer.setMediaController(new MediaController(this));
                    vidTrailer.start();
                } else {
                    trailerContainer.setBackground(getDrawable(R.drawable.skeleton));
                }

                break;
            }
            default: {
                break;
            }
        }
        txtDescription.getEditText().setText(product.getDescription());
    }

    /**
     * save book product
     */
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
                        Log.e(TAG, getString(R.string.error_failed_to_save_product));
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
                        Log.e(TAG, getString(R.string.error_failed_to_save_product));
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
                        Log.e(TAG, getString(R.string.error_failed_to_save_product));
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
                        Log.e(TAG, getString(R.string.error_failed_to_save_product));
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
                        Log.e(TAG, getString(R.string.error_failed_to_save_product));
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
                        Log.e(TAG, getString(R.string.error_failed_to_save_product));
                    });
        }
    }

    /**
     * upload thumbnail
     */
    private void uploadThumbnail() {
        showProgressBar();
        String filename = productId + "thmb." + getFileExtension(thumbnailUri);
        uploadImageTask = viewModel.uploadThumbnail(thumbnailUri, filename)
                .addOnSuccessListener(uri -> {
                    saveProductTask = viewModel.saveThumbnailUri(productId, uri)
                            .addOnCompleteListener(task -> hideProgressBar())
                            .addOnSuccessListener(aVoid -> Toasty.success(ProductDetailActivity.this, getString(R.string.image_has_been_saved), Toast.LENGTH_SHORT).show())
                            .addOnFailureListener(e -> {
                                Log.e(TAG, getString(R.string.error_failed_to_upload_product_image));
                                Toasty.error(ProductDetailActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                            });
                })
                .addOnFailureListener(e -> {
                    hideProgressBar();
                    Log.e(TAG, getString(R.string.error_failed_to_upload_product_image));
                    Toasty.error(ProductDetailActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    /**
     * upload movie trailer
     */
    private void uploadMusic() {
        showProgressBar();
        String filename = productId + "msc." + getFileExtension(musicUri);
        uploadTrailerTask = viewModel.uploadMusic(musicUri, filename)
                .addOnSuccessListener(uri ->
                        saveProductTask = viewModel.saveProductMusic(productId, uri)
                                .addOnCompleteListener(task -> hideProgressBar())
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
        showProgressBar();
        String filename = productId + "tr1." + getFileExtension(trailerUri);
        uploadTrailerTask = viewModel.uploadTrailer(trailerUri, filename)
                .addOnSuccessListener(uri1 ->
                        saveProductTask = viewModel.saveProductTrailer(productId, uri1)
                                .addOnCompleteListener(task -> hideProgressBar())
                                .addOnSuccessListener(aVoid ->
                                {
                                    Toasty.success(ProductDetailActivity.this, getString(R.string.trailer_uploaded), Toast.LENGTH_SHORT).show();
                                    vidTrailer.start();
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

    private void openImageFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, REQUEST_CHOOSE_IMAGE);
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

    /**
     * @param uri image uri
     * @return file extension based on uri
     */
    private String getFileExtension(Uri uri) {
        ContentResolver resolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(resolver.getType(uri));
    }

    /**
     * @return true if title valid
     */
    private boolean isTitleValid() {
        String title = txtTitle.getEditText().getText().toString().trim();
        if (title.isEmpty()) {
            txtTitle.setError(getString(R.string.error_empty_title));
            return false;
        }
        txtTitle.setError("");
        return true;
    }

    /**
     * @return true if author valid
     */
    private boolean isAuthorValid() {
        String author = txtAuthor.getEditText().getText().toString().trim();
        if (author.isEmpty()) {
            txtAuthor.setError(getString(R.string.error_empty_author));
            return false;
        }
        txtAuthor.setError("");
        return true;
    }

    /**
     * @return true if artist valid
     */
    private boolean isArtistValid() {
        String artist = txtArtist.getEditText().getText().toString().trim();
        if (artist.isEmpty()) {
            txtArtist.setError(getString(R.string.error_empty_artist));
            return false;
        }
        txtArtist.setError("");
        return true;
    }

    /**
     * @return true if director valid
     */
    private boolean isDirectorValid() {
        String director = txtDirector.getEditText().getText().toString().trim();
        if (director.isEmpty()) {
            txtDirector.setError(getString(R.string.error_empty_director));
            return false;
        }
        txtDirector.setError("");
        return true;
    }

    /**
     * @return true if description valid
     */
    private boolean isDescriptionValid() {
        String description = txtDescription.getEditText().getText().toString().trim();
        if (description.isEmpty()) {
            txtDescription.setError(getString(R.string.error_empty_description));
            return false;
        }
        txtDescription.setError("");
        return true;
    }

    /**
     * @return true if save task in progress
     */
    private boolean isSaveProductInProgress() {
        return saveProductTask != null && !saveProductTask.isComplete();
    }

    /**
     * @return true if add product in progress
     */
    private boolean isAddProductInProgress() {
        return addProductTask != null && !addProductTask.isComplete();
    }


    /**
     * @return true if upload thumbnail in progress
     */
    private boolean isUploadThumbnailInProgress() {
        return uploadImageTask != null && !uploadImageTask.isComplete();
    }

    /**
     * @return true if upload trailer in progress
     */
    private boolean isUploadTrailerInProgress() {
        return uploadTrailerTask != null && !uploadTrailerTask.isComplete();
    }

    /**
     * show book exclusive view
     */
    private void setBookView() {
        txtAuthor.setVisibility(View.VISIBLE);
        txtArtist.setVisibility(View.GONE);
        movieContainer.setVisibility(View.GONE);
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
