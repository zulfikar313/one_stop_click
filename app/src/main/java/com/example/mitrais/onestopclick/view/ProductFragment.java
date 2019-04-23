package com.example.mitrais.onestopclick.view;

import android.app.Service;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mitrais.onestopclick.Constant;
import com.example.mitrais.onestopclick.R;
import com.example.mitrais.onestopclick.dagger.component.DaggerProductFragmentComponent;
import com.example.mitrais.onestopclick.dagger.component.ProductFragmentComponent;
import com.example.mitrais.onestopclick.model.Product;
import com.example.mitrais.onestopclick.view.adapter.ProductAdapter;
import com.example.mitrais.onestopclick.viewmodel.ProductViewModel;
import com.google.android.gms.tasks.Task;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;
import maes.tech.intentanim.CustomIntent;

/**
 * ProductFragment handle product page logic
 */
public class ProductFragment extends Fragment implements ProductAdapter.Listener {
    private static final String ARG_TYPE = "ARG_TYPE";
    private Observer<List<Product>> productsObserver;
    private LiveData<List<Product>> productLiveData;
    private Task<Void> addLikeTask;
    private Task<Void> addDislikeTask;
    private String type = "";

    @Inject
    ProductAdapter productAdapter;

    @Inject
    ProductViewModel viewModel;

    @BindView(R.id.rec_product)
    RecyclerView recProduct;

    @BindView(R.id.txt_search)
    TextInputLayout txtSearch;

    @BindView(R.id.txt_product_not_found)
    TextView txtProductNotFound;

    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    /**
     * @param type product type filter
     * @return new ProductFragment instance
     */
    public static ProductFragment newInstance(String type) {
        ProductFragment fragment = new ProductFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TYPE, type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_product, container, false);
        ButterKnife.bind(this, view);
        initDagger();

        /* get product type */
        if (getArguments() != null) {
            type = getArguments().getString(ARG_TYPE);
            switch (type) {
                case Constant.PRODUCT_TYPE_ALL:
                    getActivity().setTitle(getString(R.string.all_products));
                    break;
                case Constant.PRODUCT_TYPE_BOOK:
                    getActivity().setTitle(getString(R.string.book));
                    break;
                case Constant.PRODUCT_TYPE_MUSIC:
                    getActivity().setTitle(getString(R.string.music));
                    break;
                case Constant.PRODUCT_TYPE_MOVIE:
                    getActivity().setTitle(getString(R.string.movie));
                    break;
            }
        }

        initRecyclerView();
        initObserver();

        return view;
    }

    @OnClick(R.id.btn_search)
    void onSearchButtonClicked() {
        String searchInput = txtSearch.getEditText().getText().toString().trim();
        searchProducts(type, searchInput);
    }

    @OnClick(R.id.img_add_product)
    void onAddProductImageClicked() {
        goToProductDetailPage("");
    }

    /**
     * initialize dagger injection
     */
    private void initDagger() {
        ProductFragmentComponent component = DaggerProductFragmentComponent.builder()
                .productFragment(this)
                .build();
        component.inject(this);
    }

    /**
     * initialize recycler view
     */
    private void initRecyclerView() {
        productAdapter = new ProductAdapter();
        productAdapter.setListener(this);
        recProduct.setHasFixedSize(true);
        recProduct.setAdapter(productAdapter);
        recProduct.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    /**
     * initialize products observer
     */
    private void initObserver() {
        productsObserver = products -> {
            if (products != null) {
                productAdapter.submitList(products);
                if (products.size() == 0)
                    new Handler().postDelayed(() -> txtProductNotFound.setVisibility(View.VISIBLE), Constant.HALF_DELAY);
                else
                    txtProductNotFound.setVisibility(View.INVISIBLE);


                hideProgressBar();
            }
        };
        observeProducts(type);
    }

    @Override
    public void onItemClicked(String productId) {
        goToProductDetailPage(productId);
    }

    @Override
    public void onLikeClicked(String id) {
        if (isAddLikeInProgress())
            Toasty.info(getActivity(), getString(R.string.add_like_in_progress), Toast.LENGTH_SHORT).show();
        else {
            addLikeTask = viewModel
                    .addLike(id)
                    .addOnFailureListener(e -> Toasty.error(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show());
        }
    }

    @Override
    public void onDislikeClicked(String productId) {
        if (isAddDislikeInProgress())
            Toasty.info(getActivity(), getString(R.string.add_dislike_in_progress), Toast.LENGTH_SHORT).show();
        else {
            addDislikeTask = viewModel
                    .addDislike(productId)
                    .addOnFailureListener(e -> Toasty.error(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show());
        }
    }

    @Override
    public void onShareClicked(String productId) {

    }

    /**
     * observe product by type
     *
     * @param type product type
     */
    private void observeProducts(String type) {
        showProgressBar();
        if (type.equals(Constant.PRODUCT_TYPE_ALL))
            productLiveData = viewModel.getAllProducts();
        else
            productLiveData = viewModel.getProductsByType(type);

        productLiveData.observe(getViewLifecycleOwner(), productsObserver);
    }

    /**
     * observe product by type and search input
     *
     * @param type   product type
     * @param search search input
     */
    private void searchProducts(String type, String search) {
        showProgressBar();
        if (type.equals(Constant.PRODUCT_TYPE_ALL))
            productLiveData = viewModel.searchProducts(search);
        else
            productLiveData = viewModel.searchProductsByType(type, search);

        productLiveData.observe(getViewLifecycleOwner(), productsObserver);

        hideSoftKeyboard();
    }


    /**
     * start ProductDetailActivity
     *
     * @param id product id
     */
    private void goToProductDetailPage(String id) {
        Intent intent = new Intent(getActivity(), ProductDetailActivity.class);
        intent.putExtra(Constant.EXTRA_PRODUCT_ID, id);
        startActivity(intent);
        CustomIntent.customType(getActivity(), Constant.ANIMATION_FADEIN_TO_FADEOUT);
    }

    /**
     * returns true if add like in progress
     *
     * @return add like progress status
     */
    private boolean isAddLikeInProgress() {
        return addLikeTask != null && !addLikeTask.isComplete();
    }

    /**
     * returns true if add dislike in progress
     *
     * @return add dislike progress status
     */
    private boolean isAddDislikeInProgress() {
        return addDislikeTask != null && !addDislikeTask.isComplete();
    }

    /**
     * set progress bar visible
     */
    private void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    /**
     * set progress bar invisible
     */
    private void hideProgressBar() {
        progressBar.setVisibility(View.INVISIBLE);
    }
    // endregion

    /**
     * hide soft keyboard
     */
    private void hideSoftKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Service.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(txtSearch.getWindowToken(), 0);
    }
}
