package com.example.mitrais.onestopclick.view.main.product_list;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mitrais.onestopclick.Constant;
import com.example.mitrais.onestopclick.R;
import com.example.mitrais.onestopclick.model.Product;
import com.example.mitrais.onestopclick.model.ProfileProduct;
import com.example.mitrais.onestopclick.view.add_product.AddProductActivity;
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
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;
import maes.tech.intentanim.CustomIntent;

public class ProductListFragment extends Fragment implements ProductAdapter.Listener {
    private static final String TAG = "ProductListFragment";
    private static final String ARG_PRODUCT_TYPE = "ARG_PRODUCT_TYPE";
    private static final String ARG_GENRE = "ARG_GENRE";
    private List<Product> products;
    private List<ProfileProduct> profileProducts;
    private Context context;
    private Task<Void> likeTask;
    private Task<Void> dislikeTask;
    private String productType;
    private String genre = "";

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
    public static ProductListFragment newInstance(String productType, String genre) {
        ProductListFragment fragment = new ProductListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PRODUCT_TYPE, productType);
        args.putString(ARG_GENRE, genre);
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
        observeProducts(productType, genre);
        observeProfileProducts();
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
    public void onLikeClicked(String id, boolean isLiked, boolean isDisliked) {
        if (isAddLikeInProgress()) {
            if (context != null)
                Toasty.info(context, getString(R.string.add_like_in_progress), Toast.LENGTH_SHORT).show();
        } else {
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
        if (isAddDislikeInProgress()) {
            if (context != null)
                Toasty.info(context, getString(R.string.add_dislike_in_progress), Toast.LENGTH_SHORT).show();
        } else {
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

    private void initDagger() {
        ProductListFragmentComponent component = DaggerProductListFragmentComponent.builder()
                .productFragment(this)
                .build();
        component.inject(this);
    }

    private void initArguments() {
        if (getArguments() != null) {
            productType = getArguments().getString(ARG_PRODUCT_TYPE);
            genre = getArguments().getString(ARG_GENRE);
        }
    }

    private void initRecyclerView() {
        productAdapter = new ProductAdapter();
        productAdapter.setListener(this);
        recProduct.setHasFixedSize(true);
        recProduct.setAdapter(productAdapter);
        recProduct.setLayoutManager(new LinearLayoutManager(context));
    }

    private void observeProducts(String type, String genre) {
        if (type.equals(Constant.PRODUCT_TYPE_ALL) && genre.isEmpty()) {
            viewModel.getAllProducts().observe(getViewLifecycleOwner(), products -> {
                if (products != null) {

                    // update local products data with profile products data
                    ProductListFragment.this.products = products;
                    syncProductWithProfile();

                    txtProductNotFound.setVisibility(products.size() == 0 ? View.VISIBLE : View.INVISIBLE);
                }
            });
        } else if (!type.equals(Constant.PRODUCT_TYPE_ALL) && genre.isEmpty()) {
            viewModel.getProductsByType(type).observe(getViewLifecycleOwner(), products -> {
                if (products != null) {
                    // update local products data with profile products data
                    ProductListFragment.this.products = products;
                    syncProductWithProfile();

                    txtProductNotFound.setVisibility(products.size() == 0 ? View.VISIBLE : View.INVISIBLE);
                }
            });
        } else if (type.equals(Constant.PRODUCT_TYPE_ALL) && !genre.isEmpty()) {
            viewModel.getProductsByGenre(genre).observe(getViewLifecycleOwner(), products -> {
                if (products != null) {
                    // update local products data with profile products data
                    ProductListFragment.this.products = products;
                    syncProductWithProfile();

                    txtProductNotFound.setVisibility(products.size() == 0 ? View.VISIBLE : View.INVISIBLE);
                }
            });
        } else if (!type.equals(Constant.PRODUCT_TYPE_ALL) && !genre.isEmpty()) {
            viewModel.getProductsByTypeAndGenre(type, genre).observe(getViewLifecycleOwner(), products -> {
                if (products != null) {
                    // update local products data with profile products data
                    ProductListFragment.this.products = products;
                    syncProductWithProfile();

                    txtProductNotFound.setVisibility(products.size() == 0 ? View.VISIBLE : View.INVISIBLE);
                }
            });
        }
    }

    private void observeProfileProducts() {
        viewModel.getAllProfileProducts().observe(getViewLifecycleOwner(), profileProducts -> {
            if (profileProducts != null) {
                ProductListFragment.this.profileProducts = profileProducts;
                syncProductWithProfile();
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

    private boolean isAddLikeInProgress() {
        return likeTask != null && !likeTask.isComplete();
    }

    private boolean isAddDislikeInProgress() {
        return dislikeTask != null && !dislikeTask.isComplete();
    }
    // endregion
}
