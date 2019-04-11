package com.example.mitrais.onestopclick.view;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.mitrais.onestopclick.Constant;
import com.example.mitrais.onestopclick.R;
import com.example.mitrais.onestopclick.dagger.component.DaggerProductDetailActivityComponent;
import com.example.mitrais.onestopclick.dagger.component.ProductDetailActivityComponent;
import com.example.mitrais.onestopclick.model.Product;
import com.example.mitrais.onestopclick.viewmodel.ProductDetailViewModel;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.squareup.picasso.Picasso;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;

public class ProductDetailActivity extends AppCompatActivity {
    private static final int REQUEST_CHOOSE_IMAGE = 1;
    private boolean isProductObserved = false;
    private String productId;
    private Product product;
    private Task<Uri> saveImageTask;
    private Task<Void> saveImageDataTask;
    private Task<Void> saveProductDetailsTask;
    private Task<DocumentReference> addImageDataTask;
    private Task<DocumentReference> addProductDetailsTask;

    @Inject
    ProductDetailViewModel viewModel;

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

        rbBook.setChecked(true); // check book by default
        productId = getIntent().getStringExtra(Constant.EXTRA_PRODUCT_ID);
        observeProduct(productId);
    }

    @Override
    public void onBackPressed() {
        if (isSaveImageInProgress() || isSaveImageDataInProgress())
            Toasty.info(this, getString(R.string.save_image_is_in_progress), Toast.LENGTH_SHORT).show();
        else if (isSaveProductDetailsInProgress())
            Toasty.info(this, getString(R.string.save_product_is_in_progress), Toast.LENGTH_SHORT).show();
        else
            super.onBackPressed();
    }

    @OnClick(R.id.img_thumbnail)
    void onThumbnailImageClicked() {
        if (isSaveImageInProgress() || isSaveImageDataInProgress())
            Toasty.info(this, getString(R.string.save_image_is_in_progress), Toast.LENGTH_SHORT).show();
        else if (isSaveProductDetailsInProgress())
            Toasty.info(this, getString(R.string.save_product_is_in_progress), Toast.LENGTH_SHORT).show();
        else
            openImageFileChooser();
    }

    @OnClick(R.id.btn_save)
    void onSaveButtonClicked() {
        switch (rgType.getCheckedRadioButtonId()) {
            case R.id.rb_book: {
                if (isAuthorValid() & isTitleValid() & isDescriptionValid())
                    saveProductDetails();
                break;
            }
            case R.id.rb_music: {
                if (isArtistValid() & isTitleValid() & isDescriptionValid())
                    saveProductDetails();
                break;
            }
            case R.id.rb_movie: {
                if (isDirectorValid() & isTitleValid() & isDescriptionValid())
                    saveProductDetails();
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
            Picasso.get().load(uri).placeholder(R.drawable.ic_launcher_background).into(imgThumbnail);
            saveProductImage(uri);
        }
    }

    // initialize dagger injection
    private void initDagger() {
        ProductDetailActivityComponent component = DaggerProductDetailActivityComponent.builder()
                .productDetailActivity(this)
                .build();
        component.inject(this);
    }

    // observe product if product id exist
    private void observeProduct(String productId) {
        if (!productId.isEmpty() && !isProductObserved) {
            isProductObserved = true;
            viewModel.getProductById(productId).observe(this, product -> {
                if (product != null) {
                    this.product = product;
                    bindView(product);
                } else {
                    Toasty.error(this, getString(R.string.error_product_not_found), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    // bind data to view
    private void bindView(Product product) {
        if (!product.getThumbnailUri().isEmpty())
            Picasso.get().load(product.getThumbnailUri()).placeholder(R.drawable.ic_launcher_background).into(imgThumbnail);

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

    // save product image
    private void saveProductImage(Uri imageUri) {
        progressBar.setVisibility(View.VISIBLE);

        String fileName;
        if (product == null || product.getThumbnailFileName().isEmpty()) {
            fileName = System.currentTimeMillis() + "." + getFileExtension(imageUri);
        } else {
            fileName = product.getThumbnailFileName();
        }

        saveImageTask = viewModel.uploadProductImage(imageUri, fileName)
                .addOnSuccessListener(uri -> {

                    Product product = new Product();
                    product.setThumbnailUri(uri.toString());
                    product.setThumbnailFileName(fileName);

                    if (!productId.isEmpty()) { // product already exist
                        product.setId(productId);
                        saveImageDataTask = viewModel.setProductImage(product)
                                .addOnCompleteListener(task -> progressBar.setVisibility(View.INVISIBLE))
                                .addOnSuccessListener(aVoid -> Toasty.success(ProductDetailActivity.this, getString(R.string.image_has_been_saved), Toast.LENGTH_SHORT).show())
                                .addOnFailureListener(e -> Toasty.error(ProductDetailActivity.this, e.toString(), Toast.LENGTH_LONG).show());
                    } else {
                        addImageDataTask = viewModel.addProductImage(product)
                                .addOnCompleteListener(task -> progressBar.setVisibility(View.INVISIBLE))
                                .addOnSuccessListener(documentReference -> {
                                    // set productId to recently made product
                                    documentReference.addSnapshotListener((documentSnapshot, e) -> {
                                        if (documentSnapshot != null) {
                                            productId = documentSnapshot.getId();
                                            observeProduct(productId);
                                        }
                                    });

                                    Toasty.success(ProductDetailActivity.this, getString(R.string.image_has_been_saved), Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> Toasty.error(this, e.getMessage(), Toast.LENGTH_LONG).show());
                    }

                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.INVISIBLE);
                    Toasty.error(this, e.toString(), Toast.LENGTH_SHORT).show();
                });
    }

    // save product details
    private void saveProductDetails() {
        if (isSaveImageInProgress() || isSaveImageDataInProgress() || isAddImageDataInProgress())
            Toasty.info(this, getString(R.string.save_image_is_in_progress), Toast.LENGTH_SHORT).show();
        else if (isSaveProductDetailsInProgress() || isAddProductDetailsInProgress())
            Toasty.info(this, getString(R.string.save_product_is_in_progress), Toast.LENGTH_SHORT).show();
        else {
            progressBar.setVisibility(View.VISIBLE);

            String title = txtTitle.getEditText().getText().toString().trim();
            String author = txtAuthor.getEditText().getText().toString().trim();
            String artist = txtArtist.getEditText().getText().toString().trim();
            String director = txtDirector.getEditText().getText().toString().trim();
            String description = txtDescription.getEditText().getText().toString().trim();

            Product product = new Product();
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

            if (!productId.isEmpty()) { // save existing product
                product.setId(productId);

                saveProductDetailsTask = viewModel.setProductDetails(product)
                        .addOnCompleteListener(task -> progressBar.setVisibility(View.INVISIBLE))
                        .addOnSuccessListener(aVoid -> Toasty.success(ProductDetailActivity.this, getString(R.string.product_has_been_saved), Toast.LENGTH_SHORT).show())
                        .addOnFailureListener(e -> Toasty.error(this, e.getMessage(), Toast.LENGTH_LONG).show());
            } else { // add new product
                addProductDetailsTask = viewModel.addProductDetails(product)
                        .addOnCompleteListener(task ->
                                progressBar.setVisibility(View.INVISIBLE))
                        .addOnSuccessListener(documentReference -> {
                            documentReference.addSnapshotListener((documentSnapshot, e) -> {
                                productId = documentSnapshot.getId();
                                observeProduct(productId);
                            });
                            Toasty.success(ProductDetailActivity.this, getString(R.string.product_has_been_saved), Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> Toasty.error(this, e.getMessage(), Toast.LENGTH_LONG).show());
            }
        }
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
        ContentResolver resolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(resolver.getType(uri));
    }

    // return true if title valid
    private boolean isTitleValid() {
        String title = txtTitle.getEditText().getText().toString().trim();
        if (title.isEmpty()) {
            txtTitle.setError(getString(R.string.error_empty_title));
            return false;
        }
        txtTitle.setError("");
        return true;
    }

    // return true if author valid
    private boolean isAuthorValid() {
        String author = txtAuthor.getEditText().getText().toString().trim();
        if (author.isEmpty()) {
            txtAuthor.setError(getString(R.string.error_empty_author));
            return false;
        }
        txtAuthor.setError("");
        return true;
    }

    // return true if artist valid
    private boolean isArtistValid() {
        String artist = txtArtist.getEditText().getText().toString().trim();
        if (artist.isEmpty()) {
            txtArtist.setError(getString(R.string.error_empty_artist));
            return false;
        }
        txtArtist.setError("");
        return true;
    }

    // return true if director valid
    private boolean isDirectorValid() {
        String director = txtDirector.getEditText().getText().toString().trim();
        if (director.isEmpty()) {
            txtDirector.setError(getString(R.string.error_empty_director));
            return false;
        }
        txtDirector.setError("");
        return true;
    }

    // return true if description valid
    private boolean isDescriptionValid() {
        String description = txtDescription.getEditText().getText().toString().trim();
        if (description.isEmpty()) {
            txtDescription.setError(getString(R.string.error_empty_description));
            return false;
        }
        txtDescription.setError("");
        return true;
    }

    // return true if save image in progress
    private boolean isSaveImageInProgress() {
        return saveImageTask != null && !saveImageTask.isComplete();
    }

    // return true if save image data in progress
    private boolean isSaveImageDataInProgress() {
        return saveImageDataTask != null && !saveImageDataTask.isComplete();
    }

    // return true if save product details data in progress
    private boolean isSaveProductDetailsInProgress() {
        return saveProductDetailsTask != null && !saveProductDetailsTask.isComplete();
    }

    // return true if add image data in progress
    private boolean isAddImageDataInProgress() {
        return addImageDataTask != null && !addImageDataTask.isComplete();
    }

    // return true if add product details data in progress
    private boolean isAddProductDetailsInProgress() {
        return addProductDetailsTask != null && !addProductDetailsTask.isComplete();
    }

    // set view for book product
    private void setBookView() {
        txtAuthor.setVisibility(View.VISIBLE);
        txtArtist.setVisibility(View.GONE);
        txtDirector.setVisibility(View.GONE);
    }

    // set view for music product
    private void setMusicView() {
        txtAuthor.setVisibility(View.GONE);
        txtArtist.setVisibility(View.VISIBLE);
        txtDirector.setVisibility(View.GONE);
    }

    // set view for movie product
    private void setMovieView() {
        txtAuthor.setVisibility(View.GONE);
        txtArtist.setVisibility(View.GONE);
        txtDirector.setVisibility(View.VISIBLE);
    }
}
