package com.example.mitrais.onestopclick.view;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mitrais.onestopclick.Constant;
import com.example.mitrais.onestopclick.R;
import com.example.mitrais.onestopclick.view.adapter.ProductAdapter;
import com.example.mitrais.onestopclick.viewmodel.SearchProductViewModel;
import com.google.android.gms.tasks.Task;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;
import maes.tech.intentanim.CustomIntent;

public class SearchProductActivity extends AppCompatActivity implements ProductAdapter.Listener {
    public static final String EXTRA_SEARCH_QUERY = "EXTRA_SEARCH_QUERY";
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
                productAdapter.submitList(products);
                txtProductNotFound.setVisibility(products.size() == 0 ? View.VISIBLE : View.INVISIBLE);
            }
        });
    }

    @Override
    public void onItemClicked(String productId) {
        goToProductPage(productId);
    }

    @Override
    public void onLikeClicked(String id) {
        if (isAddLikeInProgress())
            Toasty.info(this, getString(R.string.add_like_in_progress), Toast.LENGTH_SHORT).show();
        else {
            likeTask = viewModel
                    .addLike(id)
                    .addOnFailureListener(e -> Toasty.error(this, e.getMessage(), Toast.LENGTH_LONG).show());
        }
    }

    @Override
    public void onDislikeClicked(String productId) {
        if (isAddDislikeInProgress())
            Toasty.info(this, getString(R.string.add_dislike_in_progress), Toast.LENGTH_SHORT).show();
        else {
            dislikeTask = viewModel
                    .addDislike(productId)
                    .addOnFailureListener(e -> Toasty.error(this, e.getMessage(), Toast.LENGTH_LONG).show());
        }
    }

    @Override
    public void onShareClicked(String productId) {

    }

    private void goToProductPage(String id) {
        Intent intent = new Intent(this, ProductDetailActivity.class);
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
