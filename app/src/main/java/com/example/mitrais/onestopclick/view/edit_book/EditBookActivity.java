package com.example.mitrais.onestopclick.view.edit_book;

import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.CardView;
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
import android.widget.TextView;
import android.widget.Toast;

import com.example.mitrais.onestopclick.Constant;
import com.example.mitrais.onestopclick.R;
import com.example.mitrais.onestopclick.adapter.CommentAdapter;
import com.example.mitrais.onestopclick.custom_view.CustomImageView;
import com.example.mitrais.onestopclick.model.Comment;
import com.example.mitrais.onestopclick.model.Product;
import com.example.mitrais.onestopclick.model.Profile;
import com.example.mitrais.onestopclick.view.read_book.ReadBookActivity;
import com.google.android.gms.tasks.Task;

import java.util.Date;
import java.util.HashMap;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;
import maes.tech.intentanim.CustomIntent;

public class EditBookActivity extends AppCompatActivity {
    private static final String TAG = "EditBookActivity";
    private static final int REQUEST_CHOOSE_THUMBNAIL = 1;
    private static final int REQUEST_CHOOSE_BOOK = 2;
    private Task<Uri> UploadTask;
    private Task<Void> saveProductTask;
    private Uri thumbnailUri = Uri.parse("");
    private Uri bookUri = Uri.parse("");
    private Profile profile;
    private String productId;
    private Product product;
    private boolean isAdmin;
    private ArrayAdapter<CharSequence> genreAdapter;
    private CommentAdapter commentAdapter;

    @Inject
    EditBookViewModel viewModel;

    @BindView(R.id.img_thumbnail)
    CustomImageView imgThumbnail;

    @BindView(R.id.txt_title)
    TextInputLayout txtTitle;

    @BindView(R.id.txt_author)
    TextInputLayout txtAuthor;

    @BindView(R.id.sp_genre)
    Spinner spGenre;

    @BindView(R.id.txt_price)
    TextInputLayout txtPrice;

    @BindView(R.id.rating_bar)
    RatingBar ratingBar;

    @BindView(R.id.txt_comment)
    TextInputLayout txtComment;

    @BindView(R.id.txt_description)
    TextInputLayout txtDescription;

    @BindView(R.id.btn_save)
    AppCompatButton btnSave;

    @BindView(R.id.empty_file_card)
    CardView emptyFileCard;

    @BindView(R.id.book_file_card)
    CardView bookFileCard;

    @BindView(R.id.txt_book_filename)
    TextView txtBookFilename;

    @BindView(R.id.btn_read_book)
    AppCompatButton btnReadBook;

    @BindView(R.id.btn_edit_book)
    AppCompatButton btnEditBook;

    @BindView(R.id.rec_comments)
    RecyclerView recComments;

    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_book);
        ButterKnife.bind(this);
        initDagger();
        initSpinner();
        initRecyclerView();

        if (getIntent() != null) {
            productId = getIntent().getStringExtra(Constant.EXTRA_PRODUCT_ID);
            isAdmin = getIntent().getBooleanExtra(Constant.EXTRA_IS_ADMIN, false);
            viewModel.sync(productId);
            viewModel.syncComments(productId);
            viewModel.syncProfiles();
            observeProfile();
            observeProduct(productId);
            observeComments(productId);

            if (!isAdmin) {
                ratingBar.setVisibility(View.VISIBLE);
                txtTitle.getEditText().setEnabled(false);
                txtAuthor.getEditText().setEnabled(false);
                txtDescription.getEditText().setEnabled(false);
                txtPrice.setEnabled(false);
                spGenre.setEnabled(false);
                btnSave.setVisibility(View.GONE);
                emptyFileCard.setVisibility(View.GONE);
                bookFileCard.setVisibility(View.VISIBLE);
                btnEditBook.setVisibility(View.GONE);
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

    @OnClick({R.id.btn_add_comment, R.id.btn_save, R.id.btn_read_book, R.id.btn_upload_book, R.id.btn_edit_book, R.id.img_thumbnail})
    void onButtonClicked(View view) {
        if (isSaveProductInProgress())
            Toasty.info(this, getString(R.string.save_product_in_progress), Toast.LENGTH_SHORT).show();
        else if (isUploadInProgress())
            Toasty.info(this, getString(R.string.upload_in_progress), Toast.LENGTH_SHORT).show();
        else {
            switch (view.getId()) {
                case R.id.btn_add_comment: {
                    if (isCommentValid()) {
                        Comment comment = new Comment();
                        comment.setContent(txtComment.getEditText().getText().toString().trim());
                        comment.setDate(new Date());
                        comment.setEmail(profile.getEmail());

                        showProgressBar();

                        viewModel.addComment(productId, comment)
                                .addOnCompleteListener(task -> hideProgressBar())
                                .addOnSuccessListener(documentReference -> {
                                    txtComment.getEditText().setText("");
                                    viewModel.syncComments(productId);
                                })
                                .addOnFailureListener(e -> Toast.makeText(EditBookActivity.this, getString(R.string.failed_to_add_comment), Toast.LENGTH_SHORT).show());
                    }
                    break;
                }
                case R.id.btn_save:
                    if (isAuthorValid() & isTitleValid() & isDescriptionValid())
                        saveProduct(productId);
                    break;
                case R.id.btn_read_book:
                    readBook(productId);
                    break;
                case R.id.btn_upload_book:
                    openBookFileChooser();
                    break;
                case R.id.btn_edit_book:
                    openBookFileChooser();
                    break;
                default: // img_thumbnail clicked
                    if (isAdmin)
                        openThumbnailFileChooser();
                    break;
            }
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
                commentAdapter.submitList(comments);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUEST_CHOOSE_THUMBNAIL && data != null && data.getData() != null) {
            thumbnailUri = data.getData();
            uploadThumbnail(productId, thumbnailUri);
        }

        if (requestCode == REQUEST_CHOOSE_BOOK && resultCode == RESULT_OK && data != null && data.getData() != null) {
            bookUri = data.getData();
            uploadBook(productId, bookUri);
        }
    }

    // region private methods
    private void bindProduct(Product product) {
        this.product = product;
        setTitle(product.getTitle());
        if (!product.getThumbnailUri().isEmpty())
            imgThumbnail.loadImageUri(Uri.parse(product.getThumbnailUri()));

        txtTitle.getEditText().setText(product.getTitle());
        txtAuthor.getEditText().setText(product.getAuthor());

        if (product.getBookUri() != null && !product.getBookUri().isEmpty()) {
            bookUri = Uri.parse(product.getBookUri());
            txtBookFilename.setText(product.getTitle());
            emptyFileCard.setVisibility(View.GONE);
            bookFileCard.setVisibility(View.VISIBLE);
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

    private void saveProduct(String id) {
        showProgressBar();
        hideSoftKeyboard();
        String title = txtTitle.getEditText().getText().toString().trim();
        String author = txtAuthor.getEditText().getText().toString().trim();
        String description = txtDescription.getEditText().getText().toString().trim();
        float price = Float.parseFloat(txtPrice.getEditText().getText().toString().trim());

        Product product = new Product();
        product.setId(id);
        product.setTitle(title);
        product.setAuthor(author);
        product.setType(Constant.PRODUCT_TYPE_BOOK);
        product.setDescription(description);
        product.setGenre(spGenre.getSelectedItem().toString());
        product.setPrice(price);
        product.setThumbnailUri(thumbnailUri.toString());
        product.setBookUri(bookUri.toString());

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
                .addOnFailureListener(e -> Toast.makeText(EditBookActivity.this, getString(R.string.failed_to_rate_product), Toast.LENGTH_SHORT).show());
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
     * upload book file then save book uri
     *
     * @param id  product id
     * @param uri book uri
     */
    private void uploadBook(String id, Uri uri) {
        showProgressBar();
        String filename = id + Constant.NAME_EXT_BOOK_PDF;
        UploadTask = viewModel.uploadBook(uri, filename)
                .addOnSuccessListener(uri1 ->
                        saveProductTask = viewModel.saveBookUri(id, uri1)
                                .addOnCompleteListener(task -> hideProgressBar())
                                .addOnSuccessListener(aVoid -> Toasty.success(this, getString(R.string.book_uploaded), Toast.LENGTH_SHORT).show())
                                .addOnFailureListener(e -> {
                                    Toasty.error(this, e.getMessage(), Toast.LENGTH_LONG).show();
                                    Log.e(TAG, e.getMessage());
                                }))
                .addOnFailureListener(e -> {
                    hideProgressBar();
                    Toasty.error(this, getString(R.string.failed_to_upload_book), Toast.LENGTH_LONG).show();
                    Log.e(TAG, e.getMessage());
                });
    }

    /**
     * download book file then read the book
     *
     * @param id product id
     */
    private void readBook(String id) {
        showProgressBar();
        Uri storageUri = Uri.parse(getFilesDir() + id + Constant.NAME_EXT_BOOK_PDF);
        String filename = id + Constant.NAME_EXT_BOOK_PDF;
        viewModel.downloadBook(storageUri, filename)
                .addOnCompleteListener(task -> hideProgressBar())
                .addOnSuccessListener(taskSnapshot -> {
                    Intent intent = new Intent(this, ReadBookActivity.class);
                    intent.putExtra(Constant.EXTRA_BOOK_URI, storageUri.toString());
                    startActivity(intent);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, getString(R.string.failed_to_download_book), Toast.LENGTH_LONG).show();
                    Log.e(TAG, e.getMessage());
                });
    }

    private void initDagger() {
        EditBookActivityComponent component = DaggerEditBookActivityComponent.builder()
                .editBookActivity(this)
                .build();
        component.inject(this);
    }

    private void initSpinner() {
        genreAdapter = ArrayAdapter.createFromResource(this, R.array.movie_or_book_genre, R.layout.genre_spinner_item);
        genreAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spGenre.setAdapter(genreAdapter);
    }

    private void initRecyclerView() {
        commentAdapter = new CommentAdapter();
        recComments.setHasFixedSize(true);
        recComments.setAdapter(commentAdapter);
        recComments.setLayoutManager(new LinearLayoutManager(this));
    }

    private void openThumbnailFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, REQUEST_CHOOSE_THUMBNAIL);
    }

    private void openBookFileChooser() {
        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, REQUEST_CHOOSE_BOOK);
    }

    private String getFileExtension(Uri uri) {
        ContentResolver resolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(resolver.getType(uri));
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

    private boolean isAuthorValid() {
        String author = txtAuthor.getEditText().getText().toString().trim();
        if (author.isEmpty()) {
            txtAuthor.setError(getString(R.string.author_cant_be_empty));
            return false;
        }
        txtAuthor.setError("");
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
    // endregion
}
