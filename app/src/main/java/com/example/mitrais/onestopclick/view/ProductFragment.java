package com.example.mitrais.onestopclick.view;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mitrais.onestopclick.R;
import com.example.mitrais.onestopclick.dagger.component.DaggerProductFragmentComponent;
import com.example.mitrais.onestopclick.dagger.component.ProductFragmentComponent;
import com.example.mitrais.onestopclick.view.adapter.ProductAdapter;
import com.example.mitrais.onestopclick.viewmodel.ProductViewModel;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProductFragment extends Fragment {
    @Inject
    ProductAdapter productAdapter;

    @Inject
    ProductViewModel viewModel;

    @BindView(R.id.rec_product)
    RecyclerView recProduct;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().setTitle(getString(R.string.product_list));
        View view = inflater.inflate(R.layout.fragment_product, container, false);
        ButterKnife.bind(this, view);

        // initialize dagger injection
        ProductFragmentComponent component = DaggerProductFragmentComponent.builder()
                .productFragment(this)
                .build();
        component.inject(this);

        productAdapter = new ProductAdapter();
        recProduct.setHasFixedSize(true);
        recProduct.setAdapter(productAdapter);
        recProduct.setLayoutManager(new LinearLayoutManager(getActivity()));
        viewModel.getAllProducts().observe(this, products -> {
            productAdapter.submitList(products);
        });

        return view;
    }
}
