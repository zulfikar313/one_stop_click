package com.example.mitrais.onestopclick.view.search_product;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mitrais.onestopclick.Constant;
import com.example.mitrais.onestopclick.R;
import com.example.mitrais.onestopclick.model.Product;
import com.example.mitrais.onestopclick.model.Profile;
import com.example.mitrais.onestopclick.view.edit_book.EditBookActivity;
import com.example.mitrais.onestopclick.view.edit_movie.EditMovieActivity;
import com.example.mitrais.onestopclick.view.edit_music.EditMusicActivity;
import com.example.mitrais.onestopclick.adapter.ProductAdapter;
import com.google.android.gms.tasks.Task;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;
import maes.tech.intentanim.CustomIntent;

public class SearchProductActivity extends AppCompatActivity implements ProductAdapter.Listener {
    private static final String TAG = "SearchProductActivity";
    public static final String EXTRA_SEARCH_QUERY = "EXTRA_SEARCH_QUERY";
    private String search;
    private Profile profile;
    private SearchProductViewModel viewModel;
    private Task<Void> addToCartTask;

    @Inject
    ProductAdapter productAdapter;

    @BindView(R.id.rec_product)
    RecyclerView recProduct;

    @BindView(R.id.txt_product_not_found)
    TextView txtProductNotFound;

    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_product);
        ButterKnife.bind(this);
        initExtra();
        initViewModel();
        initRecyclerView();
        observeProfile(viewModel.getUser().getEmail());
        observeProduct(search);
    }

    private void initExtra() {
        search = getIntent().getStringExtra(EXTRA_SEARCH_QUERY);
        setTitle(search);
    }

    private void initViewModel() {
        viewModel = ViewModelProviders.of(this).get(SearchProductViewModel.class);
    }

    private void initRecyclerView() {
        productAdapter = new ProductAdapter();
        productAdapter.setListener(this);
        recProduct.setHasFixedSize(true);
        recProduct.setAdapter(productAdapter);
        recProduct.setLayoutManager(new LinearLayoutManager(this));
    }

    private void observeProfile(String email) {
        viewModel.getProfileByEmail(email).observe(this, profile -> this.profile = profile);
    }

    private void observeProduct(String search) {
        viewModel.searchProduct(search).observe(this, products -> {
            if (products != null) {
                productAdapter.submitList(products);
                txtProductNotFound.setVisibility(products.size() == 0 ? View.VISIBLE : View.INVISIBLE);
            }
        });
    }

    @Override
    public void onItemClicked(Product product) {
        viewModel.addView(product);
        switch (product.getType()) {
            case Constant.PRODUCT_TYPE_BOOK:
                goToEditBookPage(product.getId());
                break;
            case Constant.PRODUCT_TYPE_MUSIC:
                goToEditMusicPage(product.getId());
                break;
            default: // movie
                goToEditMoviePage(product.getId());
                break;
        }
    }


    @Override
    public void onShareImageClicked(Product product) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, "Recommended " + product.getType());
        intent.putExtra(Intent.EXTRA_TEXT, product.getTitle());
        startActivity(Intent.createChooser(intent, "Share via"));
    }

    @Override
    public void onAddToCartButtonClicked(Product product) {
        if (isAddToCartInProgress()) {
            Toasty.info(this, getString(R.string.add_to_cart_in_progress), Toast.LENGTH_SHORT).show();
        } else {
            if (!product.isInCart()) {
                addToCartTask = viewModel
                        .addPutInCartBy(product)
                        .addOnFailureListener(e -> {
                            Log.e(TAG, e.getMessage());
                        });
            } else {
                addToCartTask = viewModel
                        .removePutInCartBy(product)
                        .addOnFailureListener(e -> {
                            Log.e(TAG, e.getMessage());
                        });
            }
        }
    }

    private void goToEditBookPage(String id) {
        Intent intent = new Intent(this, EditBookActivity.class);
        intent.putExtra(Constant.EXTRA_PRODUCT_ID, id);
        intent.putExtra(Constant.EXTRA_IS_ADMIN, profile.isAdmin());
        startActivity(intent);
        CustomIntent.customType(this, Constant.ANIMATION_FADEIN_TO_FADEOUT);
    }

    private void goToEditMusicPage(String id) {
        Intent intent = new Intent(this, EditMusicActivity.class);
        intent.putExtra(Constant.EXTRA_PRODUCT_ID, id);
        intent.putExtra(Constant.EXTRA_IS_ADMIN, profile.isAdmin());
        startActivity(intent);
        CustomIntent.customType(this, Constant.ANIMATION_FADEIN_TO_FADEOUT);
    }

    private void goToEditMoviePage(String id) {
        Intent intent = new Intent(this, EditMovieActivity.class);
        intent.putExtra(Constant.EXTRA_PRODUCT_ID, id);
        intent.putExtra(Constant.EXTRA_IS_ADMIN, profile.isAdmin());
        startActivity(intent);
        CustomIntent.customType(this, Constant.ANIMATION_FADEIN_TO_FADEOUT);
    }

    private boolean isAddToCartInProgress() {
        return addToCartTask != null && !addToCartTask.isComplete();
    }
}
