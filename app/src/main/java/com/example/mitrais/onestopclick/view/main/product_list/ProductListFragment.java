package com.example.mitrais.onestopclick.view.main.product_list;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mitrais.onestopclick.Constant;
import com.example.mitrais.onestopclick.R;
import com.example.mitrais.onestopclick.view.add_product.AddProductActivity;
import com.example.mitrais.onestopclick.view.edit_book.EditBookActivity;
import com.example.mitrais.onestopclick.view.edit_movie.EditMovieActivity;
import com.example.mitrais.onestopclick.view.edit_music.EditMusicActivity;
import com.example.mitrais.onestopclick.view.product_detail.ProductDetailActivity;
import com.example.mitrais.onestopclick.adapter.ProductAdapter;
import com.google.android.gms.tasks.Task;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;
import maes.tech.intentanim.CustomIntent;

public class ProductListFragment extends Fragment implements ProductAdapter.Listener {
    private static final String ARG_PRODUCT_TYPE = "ARG_PRODUCT_TYPE";
    private Context context;
    private Task<Void> likeTask;
    private Task<Void> dislikeTask;
    private String productType;

    @Inject
    ProductAdapter productAdapter;

    @Inject
    ProductListViewModel viewModel;

    @BindView(R.id.rec_product)
    RecyclerView recProduct;

    @BindView(R.id.txt_product_not_found)
    TextView txtProductNotFound;

    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    /**
     * @param productType product type filter
     * @return ProductListFragment new instance
     */
    public static ProductListFragment newInstance(String productType) {
        ProductListFragment fragment = new ProductListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PRODUCT_TYPE, productType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product_list, container, false);
        ButterKnife.bind(this, view);
        initDagger();
        initArguments();
        initRecyclerView();
        observeProducts(productType);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.context = null;
    }

    @OnClick(R.id.img_add_product)
    void onAddProductImageClicked() {
        goToAddProductPage();
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
    public void onLikeClicked(String id) {
        if (isAddLikeInProgress()) {
            if (context != null)
                Toasty.info(context, getString(R.string.add_like_in_progress), Toast.LENGTH_SHORT).show();
        } else {
            likeTask = viewModel
                    .addLike(id)
                    .addOnFailureListener(e -> {
                        if (context != null)
                            Toasty.error(context, e.getMessage(), Toast.LENGTH_LONG).show();
                    });
        }
    }

    @Override
    public void onDislikeClicked(String id) {
        if (isAddDislikeInProgress()) {
            if (context != null)
                Toasty.info(context, getString(R.string.add_dislike_in_progress), Toast.LENGTH_SHORT).show();
        } else {
            dislikeTask = viewModel
                    .addDislike(id)
                    .addOnFailureListener(e -> {
                        if (context != null)
                            Toasty.error(context, e.getMessage(), Toast.LENGTH_LONG).show();
                    });
        }
    }

    @Override
    public void onShareClicked(String id) {

    }

    private void initDagger() {
        ProductListFragmentComponent component = DaggerProductListFragmentComponent.builder()
                .productFragment(this)
                .build();
        component.inject(this);
    }

    private void initArguments() {
        if (getArguments() != null)
            productType = getArguments().getString(ARG_PRODUCT_TYPE);
    }

    private void initRecyclerView() {
        productAdapter = new ProductAdapter();
        productAdapter.setListener(this);
        recProduct.setHasFixedSize(true);
        recProduct.setAdapter(productAdapter);
        recProduct.setLayoutManager(new LinearLayoutManager(context));
    }

    private void observeProducts(String type) {
        if (type.equals(Constant.PRODUCT_TYPE_ALL)) {
            viewModel.getAllProducts().observe(getViewLifecycleOwner(), products -> {
                if (products != null) {
                    productAdapter.submitList(products);
                    txtProductNotFound.setVisibility(products.size() == 0 ? View.VISIBLE : View.INVISIBLE);
                }
            });
        } else
            viewModel.getProductsByType(type).observe(getViewLifecycleOwner(), products -> {
                if (products != null) {
                    productAdapter.submitList(products);
                    txtProductNotFound.setVisibility(products.size() == 0 ? View.VISIBLE : View.INVISIBLE);
                }
            });
    }

    private void goToAddProductPage() {
        Intent intent = new Intent(context, AddProductActivity.class);
        startActivity(intent);
        CustomIntent.customType(context, Constant.ANIMATION_FADEIN_TO_FADEOUT);
    }

    private void goToEditBookPage(String id) {
        Intent intent = new Intent(context, EditBookActivity.class);
        intent.putExtra(Constant.EXTRA_PRODUCT_ID, id);
        startActivity(intent);
        CustomIntent.customType(context, Constant.ANIMATION_FADEIN_TO_FADEOUT);
    }

    private void goToEditMusicPage(String id) {
        Intent intent = new Intent(context, EditMusicActivity.class);
        intent.putExtra(Constant.EXTRA_PRODUCT_ID, id);
        startActivity(intent);
        CustomIntent.customType(context, Constant.ANIMATION_FADEIN_TO_FADEOUT);
    }

    private void goToEditMoviePage(String id) {
        Intent intent = new Intent(context, EditMovieActivity.class);
        intent.putExtra(Constant.EXTRA_PRODUCT_ID, id);
        startActivity(intent);
        CustomIntent.customType(context, Constant.ANIMATION_FADEIN_TO_FADEOUT);
    }

    private void goToEditProductPage(String id) {
        Intent intent = new Intent(context, ProductDetailActivity.class);
        intent.putExtra(Constant.EXTRA_PRODUCT_ID, id);
        startActivity(intent);
        CustomIntent.customType(context, Constant.ANIMATION_FADEIN_TO_FADEOUT);
    }

    private boolean isAddLikeInProgress() {
        return likeTask != null && !likeTask.isComplete();
    }

    private boolean isAddDislikeInProgress() {
        return dislikeTask != null && !dislikeTask.isComplete();
    }
    // endregion
}
