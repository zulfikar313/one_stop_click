package com.example.mitrais.onestopclick.view.cart;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.mitrais.onestopclick.R;
import com.example.mitrais.onestopclick.adapter.ProductAdapter;
import com.example.mitrais.onestopclick.model.Product;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CartActivity extends AppCompatActivity implements ProductAdapter.Listener {
    private ProductAdapter productAdapter;

    @Inject
    CartViewModel viewModel;

    @BindView(R.id.txt_no_product_in_cart)
    TextView txtNoProductInCart;

    @BindView(R.id.rec_product)
    RecyclerView recProduct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        ButterKnife.bind(this);
        initDagger();
        initRecyclerView();
        observeProducts();
    }

    private void observeProducts() {
        viewModel.getProductsInCart().observe(this, products -> {
            if (products != null) {
                productAdapter.submitList(products);
                txtNoProductInCart.setVisibility(products.size() == 0 ? View.VISIBLE : View.INVISIBLE);
            }
        });
    }

    private void initRecyclerView() {
        productAdapter = new ProductAdapter();
        productAdapter.setListener(this);
        recProduct.setHasFixedSize(true);
        recProduct.setAdapter(productAdapter);
        recProduct.setLayoutManager(new LinearLayoutManager(this));
    }

    private void initDagger() {
        CartActivityComponent component = DaggerCartActivityComponent.builder()
                .cartActivity(this)
                .build();
        component.inject(this);
    }

    @Override
    public void onItemClicked(Product product) {

    }

    @Override
    public void onLikeClicked(Product product) {

    }

    @Override
    public void onDislikeClicked(Product product) {

    }

    @Override
    public void onShareImageClicked(Product product) {

    }

    @Override
    public void onShareTextClicked(Product product) {

    }

    @Override
    public void onAddToCartButtonClicked(Product product) {

    }
}
