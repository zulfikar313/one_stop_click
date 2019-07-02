package com.example.mitrais.onestopclick.adapter;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.mitrais.onestopclick.Constant;
import com.example.mitrais.onestopclick.view.main.product_list.ProductListFragment;

public class ProductPagerAdapter extends FragmentPagerAdapter {
    private String genre;

    public ProductPagerAdapter(FragmentManager fm, String genre) {
        super(fm);
        this.genre = genre;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return Constant.PRODUCT_TYPE_BOOK;
            case 1:
                return Constant.PRODUCT_TYPE_MUSIC;
            default:
                return Constant.PRODUCT_TYPE_MOVIE;
        }
    }

    @Override
    public Fragment getItem(int i) {
        switch (i) {
            case 0:
                return ProductListFragment.newInstance(Constant.PRODUCT_TYPE_BOOK, genre);
            case 1:
                return ProductListFragment.newInstance(Constant.PRODUCT_TYPE_MUSIC, genre);
            default:
                return ProductListFragment.newInstance(Constant.PRODUCT_TYPE_MOVIE, genre);
        }
    }

    @Override
    public int getCount() {
        return 3;
    }
}
