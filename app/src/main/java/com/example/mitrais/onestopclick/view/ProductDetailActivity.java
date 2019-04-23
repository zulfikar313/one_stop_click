package com.example.mitrais.onestopclick.view;

import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.mitrais.onestopclick.App;
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

/**
 * ProductDetailActivity handle product detail page logic
 */
public class ProductDetailActivity extends AppCompatActivity {
    private static final String TAG = "ProductDetailActivity";
    private static final int REQUEST_CHOOSE_IMAGE = 1;
    private String productId;
    private String productThumbnailUri;
    private Product product;
    private Task<Uri> saveImageTask;
    private Task<Void> saveProductTask;
    private Task<DocumentReference> addProductTask;

    @Inject
    ProductDetailViewModel viewModel;

    @BindView(R.id.shimmer_layout)
    ShimmerLayout shimmerLayout;

    @BindView(R.id.img_thumbnail)
    ImageView imgThumbnail;

    @BindView(R.id.txt_title)
    TextInputLayout txtTitle;

    @BindView(R.id.txt_author)
    TextInputLayout txtAuthor;

    @BindView(R.id.txt_artist)
    TextInputLayout txtArtist;

    @BindView(R.id.txt_director)
    TextInputLayout txtDirector;

    @BindView(R.id.txt_description)
    TextInputLayout txtDescription;

    @BindView(R.id.rg_type)
    RadioGroup rgType;

    @BindView(R.id.rb_book)
    RadioButton rbBook;

    @BindView(R.id.rb_music)
    RadioButton rbMusic;

    @BindView(R.id.rb_movie)
    RadioButton rbMovie;

    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);
        ButterKnife.bind(this);
        initDagger();

        rbBook.setChecked(true); /* check book by default */
        productId = getIntent().getStringExtra(Constant.EXTRA_PRODUCT_ID);
        observeProduct(productId);
    }

    @Override
    public void onBackPressed() {
        if (isSaveImageInProgress() || isSaveProductInProgress() || isAddProductInProgress())
            Toasty.info(this, getString(R.string.save_product_is_in_progress), Toast.LENGTH_SHORT).show();
        else
            super.onBackPressed();

    }

    @Override
    protected void onPause() {
        super.onPause();
        CustomIntent.customType(this, Constant.ANIMATION_FADEIN_TO_FADEOUT);
    }

    @OnClick(R.id.img_thumbnail)
    void onThumbnailImageClicked() {
        if (isSaveImageInProgress() || isSaveProductInProgress() || isAddProductInProgress())
            Toasty.info(this, getString(R.string.save_product_is_in_progress), Toast.LENGTH_SHORT).show();
        else if (!App.isOnline())
            Toasty.info(this, getString(R.string.internet_required), Toast.LENGTH_SHORT).show();
        else
            openImageFileChooser();
    }

    @OnClick(R.id.btn_save)
    void onSaveButtonClicked() {
        hideSoftKeyboard();
        switch (rgType.getCheckedRadioButtonId()) {
            case R.id.rb_book: {
                if (isAuthorValid() & isTitleValid() & isDescriptionValid() & isProductThumbnailValid())
                    saveProduct(Uri.parse(productThumbnailUri));
                break;
            }
            case R.id.rb_music: {
                if (isArtistValid() & isTitleValid() & isDescriptionValid() & isProductThumbnailValid())
                    saveProduct(Uri.parse(productThumbnailUri));
                break;
            }
            case R.id.rb_movie: {
                if (isDirectorValid() & isTitleValid() & isDescriptionValid() & isProductThumbnailValid())
                    saveProduct(Uri.parse(productThumbnailUri));
                break;
            }
        }
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

            /* load image */
            shimmerLayout.startShimmerAnimation();
            Picasso.get().load(uri).placeholder(R.drawable.skeleton).into(imgThumbnail, new Callback() {
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

            productThumbnailUri = uri.toString();
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
     * observe product live data
     *
     * @param id product id
     */
    private void observeProduct(String id) {
        if (!id.isEmpty()) {
            viewModel.getProductById(id).observe(this, product -> {
                if (product != null) {
                    this.product = product;
                    bindProduct(product);
                } else
                    Toasty.error(this, getString(R.string.error_product_not_found), Toast.LENGTH_SHORT).show();
            });
        }
    }

    /**
     * bind product data to view
     *
     * @param product product objects
     */
    private void bindProduct(Product product) {
        if (!product.getThumbnailUri().isEmpty()) {
            productThumbnailUri = product.getThumbnailUri();
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
                break;
            }
            case Constant.PRODUCT_TYPE_MUSIC: {
                rbMusic.setChecked(true);
                break;
            }
            case Constant.PRODUCT_TYPE_MOVIE: {
                rbMusic.setChecked(true);
                break;
            }
            default: {
                break;
            }
        }
        txtDescription.getEditText().setText(product.getDescription());
    }

    /**
     * upload product image to storage
     * update firebase user data with image data
     * update profile data with image data
     *
     * @param imageUri product image uri
     */
    private void saveProduct(Uri imageUri) {
        showProgressBar();

        String filename;
        if (product != null && !product.getThumbnailFilename().isEmpty()) {
            filename = product.getThumbnailFilename();
        } else {
            filename = System.currentTimeMillis() + "." + getFileExtension(imageUri);
        }

        saveImageTask = viewModel.uploadProductImage(imageUri, filename)
                .addOnSuccessListener(uri -> {
                    productThumbnailUri = uri.toString();

                    Product product = new Product();
                    product.setThumbnailUri(uri.toString());
                    product.setThumbnailFilename(filename);

                    String title = txtTitle.getEditText().getText().toString().trim();
                    String author = txtAuthor.getEditText().getText().toString().trim();
                    String artist = txtArtist.getEditText().getText().toString().trim();
                    String director = txtDirector.getEditText().getText().toString().trim();
                    String description = txtDescription.getEditText().getText().toString().trim();

                    product.setTitle(title);
                    switch (rgType.getCheckedRadioButtonId()) {
                        case R.id.rb_book: {
                            product.setType(Constant.PRODUCT_TYPE_BOOK);
                            product.setAuthor(author);
                            break;
                        }
                        case R.id.rb_music: {
                            product.setType(Constant.PRODUCT_TYPE_MUSIC);
                            product.setArtist(artist);
                            break;
                        }
                        case R.id.rb_movie: {
                            product.setType(Constant.PRODUCT_TYPE_MOVIE);
                            product.setDirector(director);
                            break;
                        }
                    }
                    product.setDescription(description);

                    if (!productId.isEmpty()) { /* indicating product already exist */
                        product.setId(productId);
                        saveProductTask = viewModel.saveProduct(product)
                                .addOnCompleteListener(task -> hideProgressBar())
                                .addOnSuccessListener(aVoid -> {
                                    Toasty.success(ProductDetailActivity.this, getString(R.string.product_has_been_saved), Toast.LENGTH_SHORT).show();
                                    onBackPressed();
                                })
                                .addOnFailureListener(e -> Toasty.error(ProductDetailActivity.this, e.toString(), Toast.LENGTH_LONG).show());
                    } else {
                        /* create new product */
                        addProductTask = viewModel.addProduct(product)
                                .addOnCompleteListener(task -> hideProgressBar())
                                .addOnSuccessListener(documentReference -> {
                                    Toasty.success(ProductDetailActivity.this, getString(R.string.image_has_been_saved), Toast.LENGTH_SHORT).show();
                                    onBackPressed();
                                })
                                .addOnFailureListener(e -> Toasty.error(this, e.getMessage(), Toast.LENGTH_LONG).show());
                    }

                })
                .addOnFailureListener(e -> {
                    hideProgressBar();
                    Toasty.error(this, e.toString(), Toast.LENGTH_SHORT).show();
                });
    }

    /**
     * open menu to choose image to replace product image
     */
    private void openImageFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, REQUEST_CHOOSE_IMAGE);
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
     * @return true if product thumbnail valid
     */
    private boolean isProductThumbnailValid() {
        if (productThumbnailUri == null || productThumbnailUri.isEmpty()) {
            Toasty.error(this, getString(R.string.error_invalid_product_image), Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    /**
     * @return true if save image task in progress
     */
    private boolean isSaveImageInProgress() {
        return saveImageTask != null && !saveImageTask.isComplete();
    }

    /**
     * @return true if save details task in progress
     */
    private boolean isSaveProductInProgress() {
        return saveProductTask != null && !saveProductTask.isComplete();
    }

    /**
     * @return true if add product task in progress
     */
    private boolean isAddProductInProgress() {
        return addProductTask != null && !addProductTask.isComplete();
    }

    /**
     * show author text input and remove artist and director text input
     */
    private void setBookView() {
        txtAuthor.setVisibility(View.VISIBLE);
        txtArtist.setVisibility(View.GONE);
        txtDirector.setVisibility(View.GONE);
    }

    /**
     * show artist text input and remove author and director text input
     */
    private void setMusicView() {
        txtAuthor.setVisibility(View.GONE);
        txtArtist.setVisibility(View.VISIBLE);
        txtDirector.setVisibility(View.GONE);
    }

    /**
     * show director text input and remove author and artist text input
     */
    private void setMovieView() {
        txtAuthor.setVisibility(View.GONE);
        txtArtist.setVisibility(View.GONE);
        txtDirector.setVisibility(View.VISIBLE);
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

    /**
     * hide soft keyboard
     */
    private void hideSoftKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Service.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(txtTitle.getWindowToken(), 0);
    }
}
