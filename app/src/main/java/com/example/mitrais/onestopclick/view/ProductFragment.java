package com.example.mitrais.onestopclick.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.mitrais.onestopclick.Constant;
import com.example.mitrais.onestopclick.R;
import com.example.mitrais.onestopclick.dagger.component.DaggerProductFragmentComponent;
import com.example.mitrais.onestopclick.dagger.component.ProductFragmentComponent;
import com.example.mitrais.onestopclick.view.adapter.ProductAdapter;
import com.example.mitrais.onestopclick.viewmodel.ProductViewModel;
import com.google.android.gms.tasks.Task;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;

public class ProductFragment extends Fragment implements ProductAdapter.Listener {
    private Task<Void> addLikeTask;
    private Task<Void> addDislikeTask;

    @Inject
    ProductAdapter productAdapter;

    @Inject
    ProductViewModel viewModel;

    @BindView(R.id.rec_product)
    RecyclerView recProduct;

    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().setTitle(getString(R.string.product_list));
        View view = inflater.inflate(R.layout.fragment_product, container, false);
        ButterKnife.bind(this, view);
        initDagger();
        initRecyclerView();

        return view;
    }

    @OnClick(R.id.img_add_product)
    void onAddProductImageClicked() {
        goToProductDetailPage("");
    }

    // initialize dagger injection
    private void initDagger() {
        ProductFragmentComponent component = DaggerProductFragmentComponent.builder()
                .productFragment(this)
                .build();
        component.inject(this);
    }

    // initialize recycler view
    private void initRecyclerView() {
        productAdapter = new ProductAdapter();
        productAdapter.setListener(this);
        recProduct.setHasFixedSize(true);
        recProduct.setAdapter(productAdapter);
        recProduct.setLayoutManager(new LinearLayoutManager(getActivity()));
        viewModel.getAllProducts().observe(this, products -> {
            productAdapter.submitList(products);
        });
    }

    @Override
    public void onItemClicked(String productId) {
        goToProductDetailPage(productId);
    }

    @Override
    public void onLikeClicked(String productId) {
        if (isAddLikeInProgress())
            Toasty.info(getActivity(), getString(R.string.add_like_in_progress), Toast.LENGTH_SHORT).show();
        else {
            addLikeTask = viewModel
                    .addLike(productId)
                    .addOnFailureListener(e -> {
                        Toasty.error(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                    });
        }
    }

    @Override
    public void onDislikeClicked(String productId) {
        if (isAddDislikeInProgress())
            Toasty.info(getActivity(), getString(R.string.add_dislike_in_progress), Toast.LENGTH_SHORT).show();
        else {
            addDislikeTask = viewModel
                    .addDislike(productId)
                    .addOnFailureListener(e -> {
                        Toasty.error(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                    });
        }
    }

    @Override
    public void onShareClicked(String productId) {

    }

    // Go to product detail page
    private void goToProductDetailPage(String productId) {
        Intent intent = new Intent(getActivity(), ProductDetailActivity.class);
        intent.putExtra(Constant.EXTRA_PRODUCT_ID, productId);
        startActivity(intent);
    }

    // return true if add like in progress
    private boolean isAddLikeInProgress() {
        return addLikeTask != null && !addLikeTask.isComplete();
    }

    // return true if add dislike in progress
    private boolean isAddDislikeInProgress() {
        return addDislikeTask != null && !addDislikeTask.isComplete();
    }
}