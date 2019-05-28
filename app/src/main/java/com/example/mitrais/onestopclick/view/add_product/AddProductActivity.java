package com.example.mitrais.onestopclick.view.add_product;

import android.app.Service;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.mitrais.onestopclick.Constant;
import com.example.mitrais.onestopclick.R;
import com.example.mitrais.onestopclick.model.Product;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;

public class AddProductActivity extends AppCompatActivity {
    private static final String TAG = "AddProductActivity";
    private Task<DocumentReference> addProductTask;

    @Inject
    AddProductViewModel viewModel;

    @BindView(R.id.rg_type)
    RadioGroup rgType;

    @BindView(R.id.txt_title)
    TextInputLayout txtTitle;

    @BindView(R.id.txt_description)
    TextInputLayout txtDescription;

    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);
        setTitle(getString(R.string.title_add_product));
        ButterKnife.bind(this);
        initDagger();
    }

    @OnClick(R.id.btn_add)
    void onAddProductButtonClicked() {
        if (isAddProductInProgress())
            Toasty.info(this, getString(R.string.add_product_in_progress), Toast.LENGTH_SHORT).show();
        else {
            if (isTitleValid() & isDescriptionValid()) {
                addProduct();
                hideSoftKeyboard();
            }
        }
    }

    // region private methods
    private void addProduct() {
        showProgressBar();
        String title = txtTitle.getEditText().getText().toString().trim();
        String description = txtDescription.getEditText().getText().toString().trim();

        Product product = new Product();
        product.setTitle(title);
        product.setType(getProductType());
        product.setDescription(description);

        addProductTask = viewModel.addProduct(product)
                .addOnCompleteListener(task -> hideProgressBar())
                .addOnSuccessListener(documentReference -> {
                    Toasty.success(this, getString(R.string.product_added), Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toasty.error(this, e.getMessage(), Toast.LENGTH_LONG).show();
                    Log.e(TAG, getString(R.string.failed_to_add_product) + ": " + e.getMessage());
                });
    }

    /**
     * initialize dagger injection
     */
    private void initDagger() {
        AddProductActivityComponent component = DaggerAddProductActivityComponent.builder()
                .addProductActivity(this)
                .build();
        component.inject(this);
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

    private boolean isDescriptionValid() {
        String description = txtDescription.getEditText().getText().toString().trim();
        if (description.isEmpty()) {
            txtDescription.setError(getString(R.string.description_cant_be_empty));
            return false;
        }
        txtDescription.setError("");
        return true;
    }

    private boolean isAddProductInProgress() {
        return addProductTask != null && !addProductTask.isComplete();
    }

    private String getProductType() {
        switch (rgType.getCheckedRadioButtonId()) {
            case R.id.rb_book:
                return Constant.PRODUCT_TYPE_BOOK;
            case R.id.rb_music:
                return Constant.PRODUCT_TYPE_MUSIC;
            default:
                return Constant.PRODUCT_TYPE_MOVIE;
        }
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
