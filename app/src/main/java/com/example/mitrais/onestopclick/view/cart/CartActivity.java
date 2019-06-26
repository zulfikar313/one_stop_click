package com.example.mitrais.onestopclick.view.cart;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mitrais.onestopclick.R;
import com.example.mitrais.onestopclick.adapter.ProductV2Adapter;
import com.example.mitrais.onestopclick.model.Product;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CartActivity extends AppCompatActivity implements ProductV2Adapter.Listener {
    private List<Product> products;
    private ProductV2Adapter productAdapter;
    private Task<Void> purchaseTask;

    @Inject
    CartViewModel viewModel;

    @BindView(R.id.txt_no_product_in_cart)
    TextView txtNoProductInCart;

    @BindView(R.id.rec_product)
    RecyclerView recProduct;

    @BindView(R.id.txt_total)
    TextView txtTotal;

    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        ButterKnife.bind(this);
        initDagger();
        initRecyclerView();
        observeProducts();
    }

    @OnClick(R.id.btn_buy)
    void onBuyButtonClicked() {
        if (isPurchaseInProgress())
            Toast.makeText(this, "Purchase is in progress", Toast.LENGTH_SHORT).show();
        else {
            String email = viewModel.getUser().getEmail();
            for (Product product : products) {
                ArrayList<String> ownedBy;
                if (product.getOwnedBy() == null)
                    ownedBy = new ArrayList<>();
                else
                    ownedBy = product.getOwnedBy();

                if (!ownedBy.contains(email)) {
                    ownedBy.add(email);
                    product.setOwnedBy(ownedBy);
                    showProgressBar();
                    purchaseTask = viewModel.saveProduct(product)
                            .addOnCompleteListener(task -> hideProgressBar())
                            .addOnSuccessListener(aVoid -> {
                                finish();
                                onBackPressed();
                            })
                            .addOnFailureListener(e -> Toast.makeText(CartActivity.this, "Purchase failure somehow", Toast.LENGTH_SHORT).show());
                }
            }
        }
    }

    @Override
    public void onItemClicked(Product product) {
        if (isPurchaseInProgress())
            Toast.makeText(this, "Purchase is in progress", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(this, "Item clicked", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDeleteClicked(Product product) {
        if (isPurchaseInProgress())
            Toast.makeText(this, "Purchase is in progress", Toast.LENGTH_SHORT).show();
        else
            viewModel.removePutInCartBy(product);
    }

    private void observeProducts() {
        viewModel.getProductsInCart().observe(this, products -> {
            if (products != null) {
                bindProducts(products);
            }
        });
    }

    private void bindProducts(List<Product> products) {
        this.products = products;
        productAdapter.submitList(products);
        txtNoProductInCart.setVisibility(products.size() == 0 ? View.VISIBLE : View.INVISIBLE);

        float total = 0;
        for (Product product : products) {
            total += product.getPrice();
        }
        txtTotal.setText(getString(R.string.total) + ": Rp." + total);
    }

    private void initRecyclerView() {
        productAdapter = new ProductV2Adapter();
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
