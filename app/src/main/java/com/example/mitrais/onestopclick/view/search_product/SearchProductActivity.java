package com.example.mitrais.onestopclick.view.search_product;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.annotation.Nullable;
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
import com.example.mitrais.onestopclick.model.ProfileProduct;
import com.example.mitrais.onestopclick.view.edit_book.EditBookActivity;
import com.example.mitrais.onestopclick.view.edit_movie.EditMovieActivity;
import com.example.mitrais.onestopclick.view.edit_music.EditMusicActivity;
import com.example.mitrais.onestopclick.adapter.ProductAdapter;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;
import maes.tech.intentanim.CustomIntent;

public class SearchProductActivity extends AppCompatActivity implements ProductAdapter.Listener {
    private static final String TAG = "SearchProductActivity";
    public static final String EXTRA_SEARCH_QUERY = "EXTRA_SEARCH_QUERY";
    private List<Product> products;
    private List<ProfileProduct> profileProducts;
    private String search;
    private SearchProductViewModel viewModel;
    private Task<Void> likeTask;
    private Task<Void> dislikeTask;

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
        observeProduct(search);
        observeProfileProducts();
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

    private void observeProduct(String search) {
        viewModel.searchProduct(search).observe(this, products -> {
            if (products != null) {
                // update local products data with profile products data
                SearchProductActivity.this.products = products;
                syncProductWithProfile();

                txtProductNotFound.setVisibility(products.size() == 0 ? View.VISIBLE : View.INVISIBLE);
            }
        });
    }

    private void observeProfileProducts() {
        viewModel.getAllProfileProducts().observe(this, new Observer<List<ProfileProduct>>() {
            @Override
            public void onChanged(@Nullable List<ProfileProduct> profileProducts) {
                if (profileProducts != null) {
                    SearchProductActivity.this.profileProducts = profileProducts;
                    syncProductWithProfile();
                }
            }
        });
    }

    private void syncProductWithProfile() {
        List<Product> newProducts = new ArrayList<>();
        if (products != null && profileProducts != null) {
            for (Product product : products) {
                product.setLike(0);
                product.setDislike(0);
                for (ProfileProduct profileProduct : profileProducts) {
                    if (product.getId().equals(profileProduct.getProductId())) {
                        if (viewModel.getUser().getEmail().equals(profileProduct.getEmail())) {
                            product.setLiked(profileProduct.isLiked());
                            product.setDisliked(profileProduct.isDisliked());
                        }
                        if (profileProduct.isLiked())
                            product.setLike(product.getLike() + 1);
                        if (profileProduct.isDisliked())
                            product.setDislike(product.getDislike() + 1);
                    }
                }
                newProducts.add(product);
            }
            productAdapter.submitList(newProducts);
        }
    }

    @Override
    public void onItemClicked(String id, String type) {
        switch (type) {
            case Constant.PRODUCT_TYPE_BOOK:
                goToEditBookPage(id);
                break;
            case Constant.PRODUCT_TYPE_MUSIC:
                goToEditMusicPage(id);
                break;
            default: // movie
                goToEditMoviePage(id);
                break;
        }
    }

    @Override
    public void onLikeClicked(String id, boolean isLiked, boolean isDisliked) {
        if (isAddLikeInProgress())
            Toasty.info(this, getString(R.string.add_like_in_progress), Toast.LENGTH_SHORT).show();
        else {
            if (!isLiked) {
                likeTask = viewModel.addLike(id)
                        .addOnFailureListener(e -> {
                            Log.e(TAG, e.getMessage());
                        });
            } else {
                likeTask = viewModel.removeLike(id)
                        .addOnFailureListener(e -> {
                            Log.e(TAG, e.getMessage());
                        });
            }
        }
    }

    @Override
    public void onDislikeClicked(String id, boolean isLiked, boolean isDisliked) {
        if (isAddDislikeInProgress())
            Toasty.info(this, getString(R.string.add_dislike_in_progress), Toast.LENGTH_SHORT).show();
        else {
            if (!isDisliked) {
                dislikeTask = viewModel
                        .addDislike(id)
                        .addOnFailureListener(e -> {
                            Log.e(TAG, e.getMessage());
                        });
            } else {
                dislikeTask = viewModel
                        .removeDislike(id)
                        .addOnFailureListener(e -> {
                            Log.e(TAG, e.getMessage());
                        });
            }
        }
    }

    @Override
    public void onShareClicked(String id) {

    }

    private void goToEditBookPage(String id) {
        Intent intent = new Intent(this, EditBookActivity.class);
        intent.putExtra(Constant.EXTRA_PRODUCT_ID, id);
        startActivity(intent);
        CustomIntent.customType(this, Constant.ANIMATION_FADEIN_TO_FADEOUT);
    }

    private void goToEditMusicPage(String id) {
        Intent intent = new Intent(this, EditMusicActivity.class);
        intent.putExtra(Constant.EXTRA_PRODUCT_ID, id);
        startActivity(intent);
        CustomIntent.customType(this, Constant.ANIMATION_FADEIN_TO_FADEOUT);
    }

    private void goToEditMoviePage(String id) {
        Intent intent = new Intent(this, EditMovieActivity.class);
        intent.putExtra(Constant.EXTRA_PRODUCT_ID, id);
        startActivity(intent);
        CustomIntent.customType(this, Constant.ANIMATION_FADEIN_TO_FADEOUT);
    }

    /**
     * @return true if add like in progress
     */
    private boolean isAddLikeInProgress() {
        return likeTask != null && !likeTask.isComplete();
    }

    /**
     * @return true if add dislike in progress
     */
    private boolean isAddDislikeInProgress() {
        return dislikeTask != null && !dislikeTask.isComplete();
    }
}
