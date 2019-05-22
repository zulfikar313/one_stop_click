package com.example.mitrais.onestopclick.view.main.product_list_parent;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mitrais.onestopclick.R;
import com.example.mitrais.onestopclick.adapter.ProductPagerAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProductListParentFragment extends Fragment {
    public static final String ARG_GENRE = "ARG_GENRE";
    private String genre = "";

    @BindView(R.id.tabs)
    TabLayout tabs;

    @BindView(R.id.view_pager)
    ViewPager viewPager;

    public ProductListParentFragment() {
        // Required empty public constructor
    }

    public static ProductListParentFragment newInstance(String genre) {
        ProductListParentFragment fragment = new ProductListParentFragment();
        Bundle args = new Bundle();
        args.putString(ARG_GENRE, genre);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product_list_parent, container, false);
        ButterKnife.bind(this, view);

        if (getArguments() != null) {
            genre = getArguments().getString(ARG_GENRE);
        }

        initViewPager();
        return view;
    }

    /**
     * initialize view pager
     */
    private void initViewPager() {
        ProductPagerAdapter adapter = new ProductPagerAdapter(getChildFragmentManager(), genre);
        viewPager.setAdapter(adapter);
        tabs.setupWithViewPager(viewPager);
    }
}
