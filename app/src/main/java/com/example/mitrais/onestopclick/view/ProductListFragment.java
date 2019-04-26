package com.example.mitrais.onestopclick.view;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
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
import com.example.mitrais.onestopclick.view.adapter.ProductAdapter;
import com.example.mitrais.onestopclick.viewmodel.ProductListViewModel;
import com.google.android.gms.tasks.Task;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnEditorAction;
import es.dmoral.toasty.Toasty;
import maes.tech.intentanim.CustomIntent;

public class ProductListFragment extends Fragment implements ProductAdapter.Listener {
    private static final String ARG_PRODUCT_TYPE = "ARG_PRODUCT_TYPE";
    private Task<Void> likeTask;
    private Task<Void> dislikeTask;
    private String productType;

    @Inject
    ProductAdapter productAdapter;

    @Inject
    ProductListViewModel viewModel;

    @BindView(R.id.rec_product)
    RecyclerView recProduct;

    @BindView(R.id.txt_search)
    TextInputLayout txtSearch;

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

    @OnClick(R.id.btn_search)
    void onSearchButtonClicked() {
        String searchInput = txtSearch.getEditText().getText().toString().trim();
        searchProducts(productType, searchInput);
    }

    @OnClick(R.id.img_add_product)
    void onAddProductImageClicked() {
        goToProductScreen("");
    }

    @OnEditorAction(R.id.txt_edit_search)
    boolean onSearchTextEditorAction() {
        String search = txtSearch.getEditText().getText().toString();
        searchProducts(productType, search);
        return true;
    }

    @Override
    public void onItemClicked(String productId) {
        goToProductScreen(productId);
    }

    @Override
    public void onLikeClicked(String id) {
        if (isAddLikeInProgress())
            Toasty.info(getActivity(), getString(R.string.add_like_in_progress), Toast.LENGTH_SHORT).show();
        else {
            likeTask = viewModel
                    .addLike(id)
                    .addOnFailureListener(e -> Toasty.error(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show());
        }
    }

    @Override
    public void onDislikeClicked(String productId) {
        if (isAddDislikeInProgress())
            Toasty.info(getActivity(), getString(R.string.add_dislike_in_progress), Toast.LENGTH_SHORT).show();
        else {
            dislikeTask = viewModel
                    .addDislike(productId)
                    .addOnFailureListener(e -> Toasty.error(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show());
        }
    }

    @Override
    public void onShareClicked(String productId) {

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
     * initialize argument data
     */
    private void initArguments() {
        if (getArguments() != null) {
            productType = getArguments().getString(ARG_PRODUCT_TYPE);
            switch (productType) {
                case Constant.PRODUCT_TYPE_BOOK:
                    getActivity().setTitle(getString(R.string.book));
                    txtSearch.setHint(getString(R.string.search_book));
                    break;
                case Constant.PRODUCT_TYPE_MUSIC:
                    getActivity().setTitle(getString(R.string.music));
                    txtSearch.setHint(getString(R.string.search_music));
                    break;
                case Constant.PRODUCT_TYPE_MOVIE:
                    getActivity().setTitle(getString(R.string.movie));
                    txtSearch.setHint(getString(R.string.search_movie));
                    break;
                default:
                    getActivity().setTitle(getString(R.string.all_products));
                    break;
            }
        }
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
     * @param type product type
     */
    private void observeProducts(String type) {
        if (type.equals(Constant.PRODUCT_TYPE_ALL)) {
            viewModel.getAllProducts().observe(getViewLifecycleOwner(), products -> {
                {
                    productAdapter.submitList(products);
                    txtProductNotFound.setVisibility(products.size() == 0 ? View.VISIBLE : View.INVISIBLE);
                }
            });
        } else
            viewModel.getProductsByType(type).observe(getViewLifecycleOwner(), products -> {
                productAdapter.submitList(products);
                txtProductNotFound.setVisibility(products.size() == 0 ? View.VISIBLE : View.INVISIBLE);
            });
    }

    /**
     * @param type   product type
     * @param search search input
     */
    private void searchProducts(String type, String search) {
        txtSearch.getEditText().setText("");
        txtSearch.getEditText().clearFocus();
        hideSoftKeyboard();

        if (type.equals(Constant.PRODUCT_TYPE_ALL))
            viewModel.searchProducts(search).observe(getViewLifecycleOwner(), products -> {
                productAdapter.submitList(products);
                txtProductNotFound.setVisibility(products.size() == 0 ? View.VISIBLE : View.INVISIBLE);
            });
        else
            viewModel.searchProductsByType(type, search).observe(getViewLifecycleOwner(), products -> {
                productAdapter.submitList(products);
                txtProductNotFound.setVisibility(products.size() == 0 ? View.VISIBLE : View.INVISIBLE);
            });
    }

    private void goToProductScreen(String id) {
        Intent intent = new Intent(getActivity(), ProductActivity.class);
        intent.putExtra(Constant.EXTRA_PRODUCT_ID, id);
        startActivity(intent);
        CustomIntent.customType(getActivity(), Constant.ANIMATION_FADEIN_TO_FADEOUT);
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

    private void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgressBar() {
        progressBar.setVisibility(View.INVISIBLE);
    }

    private void hideSoftKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Service.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(txtSearch.getWindowToken(), 0);
    }
    // endregion
}
