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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mitrais.onestopclick.Constant;
import com.example.mitrais.onestopclick.R;
import com.example.mitrais.onestopclick.model.Product;
import com.example.mitrais.onestopclick.model.Profile;
import com.example.mitrais.onestopclick.view.add_product.AddProductActivity;
import com.example.mitrais.onestopclick.view.edit_book.EditBookActivity;
import com.example.mitrais.onestopclick.view.edit_movie.EditMovieActivity;
import com.example.mitrais.onestopclick.view.edit_music.EditMusicActivity;
import com.example.mitrais.onestopclick.adapter.ProductAdapter;
import com.google.android.gms.tasks.Task;

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
    private Profile profile;
    private Context context;
    private Task<Void> likeTask;
    private Task<Void> dislikeTask;
    private Task<Void> addToCartTask;
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

    @BindView(R.id.img_add_product)
    ImageView imgAddProduct;

    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

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
        observeProfile(viewModel.getUser().getEmail());
        observeProducts(productType, genre);
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
    public void onItemClicked(Product product) {
        viewModel.addView(product);
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
    public void onLikeClicked(Product product) {
        if (isAddLikeInProgress()) {
            if (context != null)
                Toasty.info(context, getString(R.string.add_like_in_progress), Toast.LENGTH_SHORT).show();
        } else {
            if (!product.isLiked()) {
                likeTask = viewModel.addLike(product)
                        .addOnFailureListener(e -> {
                            Log.e(TAG, e.getMessage());
                        });
            } else {
                likeTask = viewModel.removeLike(product)
                        .addOnFailureListener(e -> {
                            Log.e(TAG, e.getMessage());
                        });
            }
        }
    }

    @Override
    public void onDislikeClicked(Product product) {
        if (isAddDislikeInProgress()) {
            if (context != null)
                Toasty.info(context, getString(R.string.add_dislike_in_progress), Toast.LENGTH_SHORT).show();
        } else {
            if (!product.isDisliked()) {
                dislikeTask = viewModel
                        .addDislike(product)
                        .addOnFailureListener(e -> {
                            Log.e(TAG, e.getMessage());
                        });
            } else {
                dislikeTask = viewModel
                        .removeDislike(product)
                        .addOnFailureListener(e -> {
                            Log.e(TAG, e.getMessage());
                        });
            }
        }
    }

    @Override
    public void onAddToCartButtonClicked(Product product) {
        if (isAddToCartInProgress()) {
            if (context != null)
                Toasty.info(context, getString(R.string.add_to_cart_in_progress), Toast.LENGTH_SHORT).show();
        } else {
            if (!product.isInCart()) {
                addToCartTask = viewModel
                        .addPutInCartBy(product)
                        .addOnFailureListener(e -> {
                            Log.e(TAG, e.getMessage());
                        });
            } else {
                addToCartTask = viewModel
                        .removePutInCartBy(product)
                        .addOnFailureListener(e -> {
                            Log.e(TAG, e.getMessage());
                        });
            }
        }
    }

    @Override
    public void onShareImageClicked(Product product) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, "Recommended " + product.getType());
        intent.putExtra(Intent.EXTRA_TEXT, product.getTitle());
        startActivity(Intent.createChooser(intent, "Share via"));
    }

    @Override
    public void onShareTextClicked(Product product) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, "Recommended " + product.getType());
        intent.putExtra(Intent.EXTRA_TEXT, product.getTitle());
        startActivity(Intent.createChooser(intent, "Share via"));
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

    private void observeProfile(String email) {
        viewModel.getProfileByEmail(email).observe(getViewLifecycleOwner(), profile -> {
            this.profile = profile;
            if (profile.isAdmin())
                imgAddProduct.setVisibility(View.VISIBLE);
        });
    }

    private void observeProducts(String type, String genre) {
        if (type.equals(Constant.PRODUCT_TYPE_ALL) && genre.isEmpty()) {
            viewModel.getAllProducts().observe(getViewLifecycleOwner(), products -> {
                if (products != null) {
                    productAdapter.submitList(products);
                    txtProductNotFound.setVisibility(products.size() == 0 ? View.VISIBLE : View.INVISIBLE);
                }
            });
        } else if (!type.equals(Constant.PRODUCT_TYPE_ALL) && genre.isEmpty()) {
            viewModel.getProductsByType(type).observe(getViewLifecycleOwner(), products -> {
                if (products != null) {
                    productAdapter.submitList(products);
                    txtProductNotFound.setVisibility(products.size() == 0 ? View.VISIBLE : View.INVISIBLE);
                }
            });
        } else if (type.equals(Constant.PRODUCT_TYPE_ALL) && !genre.isEmpty()) {
            viewModel.getProductsByGenre(genre).observe(getViewLifecycleOwner(), products -> {
                if (products != null) {
                    productAdapter.submitList(products);
                    txtProductNotFound.setVisibility(products.size() == 0 ? View.VISIBLE : View.INVISIBLE);
                }
            });
        } else if (!type.equals(Constant.PRODUCT_TYPE_ALL) && !genre.isEmpty()) {
            viewModel.getProductsByTypeAndGenre(type, genre).observe(getViewLifecycleOwner(), products -> {
                if (products != null) {
                    productAdapter.submitList(products);
                    txtProductNotFound.setVisibility(products.size() == 0 ? View.VISIBLE : View.INVISIBLE);
                }
            });
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
        intent.putExtra(Constant.EXTRA_IS_ADMIN, profile.isAdmin());
        startActivity(intent);
        CustomIntent.customType(context, Constant.ANIMATION_FADEIN_TO_FADEOUT);
    }

    private void goToEditMusicPage(String id) {
        Intent intent = new Intent(context, EditMusicActivity.class);
        intent.putExtra(Constant.EXTRA_PRODUCT_ID, id);
        intent.putExtra(Constant.EXTRA_IS_ADMIN, profile.isAdmin());
        startActivity(intent);
        CustomIntent.customType(context, Constant.ANIMATION_FADEIN_TO_FADEOUT);
    }

    private void goToEditMoviePage(String id) {
        Intent intent = new Intent(context, EditMovieActivity.class);
        intent.putExtra(Constant.EXTRA_PRODUCT_ID, id);
        intent.putExtra(Constant.EXTRA_IS_ADMIN, profile.isAdmin());
        startActivity(intent);
        CustomIntent.customType(context, Constant.ANIMATION_FADEIN_TO_FADEOUT);
    }

    private boolean isAddToCartInProgress() {
        return addToCartTask != null && !addToCartTask.isComplete();
    }

    private boolean isAddLikeInProgress() {
        return likeTask != null && !likeTask.isComplete();
    }

    private boolean isAddDislikeInProgress() {
        return dislikeTask != null && !dislikeTask.isComplete();
    }
    // endregion
}
