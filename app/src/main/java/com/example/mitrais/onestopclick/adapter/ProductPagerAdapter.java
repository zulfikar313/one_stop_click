package com.example.mitrais.onestopclick.adapter;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.mitrais.onestopclick.Constant;
import com.example.mitrais.onestopclick.view.main.product_list.ProductListFragment;

public class ProductPagerAdapter extends FragmentPagerAdapter {
    private String genre = "";

    public ProductPagerAdapter(FragmentManager fm, String genre) {
        super(fm);
        this.genre = genre;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return Constant.PRODUCT_TYPE_ALL;
            case 1:
                return Constant.PRODUCT_TYPE_BOOK;
            case 2:
                return Constant.PRODUCT_TYPE_MUSIC;
            case 3:
                return Constant.PRODUCT_TYPE_MOVIE;
            default:
                return Constant.PRODUCT_TYPE_ALL;
        }
    }

    @Override
    public Fragment getItem(int i) {
        switch (i) {
            case 0:
                return ProductListFragment.newInstance(Constant.PRODUCT_TYPE_ALL, genre);
            case 1:
                return ProductListFragment.newInstance(Constant.PRODUCT_TYPE_BOOK, genre);
            case 2:
                return ProductListFragment.newInstance(Constant.PRODUCT_TYPE_MUSIC, genre);
            case 3:
                return ProductListFragment.newInstance(Constant.PRODUCT_TYPE_MOVIE, genre);
            default:
                return ProductListFragment.newInstance(Constant.PRODUCT_TYPE_ALL, genre);
        }
    }

    @Override
    public int getCount() {
        return 4;
    }
}
