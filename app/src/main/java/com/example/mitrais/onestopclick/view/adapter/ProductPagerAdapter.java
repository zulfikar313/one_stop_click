package com.example.mitrais.onestopclick.view.adapter;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.mitrais.onestopclick.Constant;
import com.example.mitrais.onestopclick.view.ProductListFragment;

public class ProductPagerAdapter extends FragmentPagerAdapter {
    public ProductPagerAdapter(FragmentManager fm) {
        super(fm);
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
                return ProductListFragment.newInstance(Constant.PRODUCT_TYPE_ALL);
            case 1:
                return ProductListFragment.newInstance(Constant.PRODUCT_TYPE_BOOK);
            case 2:
                return ProductListFragment.newInstance(Constant.PRODUCT_TYPE_MUSIC);
            case 3:
                return ProductListFragment.newInstance(Constant.PRODUCT_TYPE_MOVIE);
            default:
                return ProductListFragment.newInstance(Constant.PRODUCT_TYPE_ALL);
        }
    }

    @Override
    public int getCount() {
        return 4;
    }
}
