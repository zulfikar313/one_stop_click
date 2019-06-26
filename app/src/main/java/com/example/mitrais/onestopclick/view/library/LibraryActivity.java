package com.example.mitrais.onestopclick.view.library;

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
import com.example.mitrais.onestopclick.adapter.ProductV2Adapter;
import com.example.mitrais.onestopclick.model.Product;
import com.example.mitrais.onestopclick.view.edit_book.EditBookActivity;
import com.example.mitrais.onestopclick.view.edit_movie.EditMovieActivity;
import com.example.mitrais.onestopclick.view.edit_music.EditMusicActivity;
import com.google.android.gms.tasks.Task;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import maes.tech.intentanim.CustomIntent;

public class LibraryActivity extends AppCompatActivity implements ProductV2Adapter.Listener {
    private List<Product> products;
    private ProductV2Adapter productAdapter;
    private Task<Void> purchaseTask;

    @Inject
    LibraryViewModel viewModel;

    @BindView(R.id.txt_no_product_in_library)
    TextView txtNoProductInLibrary;

    @BindView(R.id.rec_product)
    RecyclerView recProduct;

    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library);
        ButterKnife.bind(this);
        initDagger();
        initRecyclerView();
        observeProducts();
    }

    @Override
    public void onItemClicked(Product product) {
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
    public void onDeleteClicked(Product product) {
        Toast.makeText(this, "Delete clicked", Toast.LENGTH_SHORT).show();
    }

    private void observeProducts() {
        viewModel.getOwnedProducts().observe(this, products -> {
            if (products != null) {
                bindProducts(products);
            }
        });
    }

    private void bindProducts(List<Product> products) {
        this.products = products;
        productAdapter.submitList(products);
        txtNoProductInLibrary.setVisibility(products.size() == 0 ? View.VISIBLE : View.INVISIBLE);
    }

    private void initRecyclerView() {
        productAdapter = new ProductV2Adapter();
        productAdapter.setListener(this);
        recProduct.setHasFixedSize(true);
        recProduct.setAdapter(productAdapter);
        recProduct.setLayoutManager(new LinearLayoutManager(this));
    }

    private void goToEditBookPage(String id) {
        Intent intent = new Intent(this, EditBookActivity.class);
        intent.putExtra(Constant.EXTRA_PRODUCT_ID, id);
        intent.putExtra(Constant.EXTRA_IS_OWNED, true);
        startActivity(intent);
        CustomIntent.customType(this, Constant.ANIMATION_FADEIN_TO_FADEOUT);
    }

    private void goToEditMusicPage(String id) {
        Intent intent = new Intent(this, EditMusicActivity.class);
        intent.putExtra(Constant.EXTRA_PRODUCT_ID, id);
        intent.putExtra(Constant.EXTRA_IS_OWNED, true);
        startActivity(intent);
        CustomIntent.customType(this, Constant.ANIMATION_FADEIN_TO_FADEOUT);
    }

    private void goToEditMoviePage(String id) {
        Intent intent = new Intent(this, EditMovieActivity.class);
        intent.putExtra(Constant.EXTRA_PRODUCT_ID, id);
        intent.putExtra(Constant.EXTRA_IS_OWNED, true);
        startActivity(intent);
        CustomIntent.customType(this, Constant.ANIMATION_FADEIN_TO_FADEOUT);
    }


    private void initDagger() {
        LibraryActivityComponent component = DaggerLibraryActivityComponent.builder()
                .libraryActivity(this)
                .build();
        component.inject(this);
    }

    private boolean isPurchaseInProgress() {
        return purchaseTask != null && !purchaseTask.isComplete();
    }

    private void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgressBar() {
        progressBar.setVisibility(View.INVISIBLE);
    }
}
