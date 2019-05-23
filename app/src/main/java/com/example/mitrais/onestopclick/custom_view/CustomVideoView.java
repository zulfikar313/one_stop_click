package com.example.mitrais.onestopclick.custom_view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.mitrais.onestopclick.R;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ui.PlayerView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CustomVideoView extends RelativeLayout {
    @BindView(R.id.player_view)
    PlayerView playerView;

    @BindView(R.id.txt_no_video_found)
    TextView txtNoVideoFound;

    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    public CustomVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        inflate(context, R.layout.view_custom_video, this);
        ButterKnife.bind(this);
    }

    public void setPlayer(ExoPlayer player) {
        playerView.setPlayer(player);
        playerView.setVisibility(VISIBLE);
        txtNoVideoFound.setVisibility(GONE);
    }

    public void showProgressBar() {
        progressBar.setVisibility(VISIBLE);
    }

    public void hideProgressBar() {
        progressBar.setVisibility(INVISIBLE);
    }
}
