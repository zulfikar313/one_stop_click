package com.example.mitrais.onestopclick.custom_view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.example.mitrais.onestopclick.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

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
        startShimmerAnimation();
        Picasso.get().load(uri).placeholder(R.drawable.skeleton).into(imgView, new Callback() {
            @Override
            public void onSuccess() {
                stopShimmerAnimation();
            }

            @Override
            public void onError(Exception e) {
                stopShimmerAnimation();
                Log.e(TAG, e.toString());
            }
        });
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
