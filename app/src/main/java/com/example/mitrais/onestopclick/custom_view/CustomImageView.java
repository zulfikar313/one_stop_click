package com.example.mitrais.onestopclick.custom_view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.mitrais.onestopclick.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.supercharge.shimmerlayout.ShimmerLayout;

public class CustomImageView extends ShimmerLayout {
    private static final String TAG = "CustomImageView";
    @BindView(R.id.shimmer_layout)
    ShimmerLayout shimmerLayout;

    @BindView(R.id.img_view)
    ImageView imgView;

    @BindView(R.id.image_progress_bar)
    ProgressBar imgProgressBar;

    public CustomImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        inflate(context, R.layout.view_custom_image, this);
        ButterKnife.bind(this);
    }

    public void setImageDrawable(Drawable drawable) {
        imgView.setImageDrawable(drawable);
    }

    public void loadImageUri(Uri uri) {
        showProgressBar();
        Glide.with(this).load(uri)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        hideProgressBar();
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        hideProgressBar();
                        return false;

                    }
                })
                .into(imgView);
    }

    public void startShimmerAnimation() {
        shimmerLayout.startShimmerAnimation();
    }

    public void stopShimmerAnimation() {
        shimmerLayout.stopShimmerAnimation();
    }

    public void showProgressBar() {
        imgProgressBar.setVisibility(VISIBLE);
    }

    public void hideProgressBar() {
        imgProgressBar.setVisibility(INVISIBLE);
    }
}
