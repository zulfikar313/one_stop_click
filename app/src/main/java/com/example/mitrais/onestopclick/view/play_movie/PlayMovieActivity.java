package com.example.mitrais.onestopclick.view.play_movie;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.mitrais.onestopclick.Constant;
import com.example.mitrais.onestopclick.R;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;

public class PlayMovieActivity extends AppCompatActivity {
    private static final String TAG = "PlayMovieActivity";
    private Uri movieUri = Uri.parse("");
    private ExoPlayer moviePlayer;

    @BindView(R.id.movie_view)
    PlayerView movieView;

    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_movie);
        ButterKnife.bind(this);

        if (getIntent() != null) {
            movieUri = Uri.parse(getIntent().getStringExtra(Constant.EXTRA_MOVIE_URI));
            if (!movieUri.toString().isEmpty())
                prepareMovie(movieUri);
            else
                Toasty.error(this, getString(R.string.movie_file_not_found), Toast.LENGTH_LONG).show();
        }
    }

    private void prepareMovie(Uri movieUri) {
        showProgressBar();

        // prepare video player
        TrackSelector trackSelector = new DefaultTrackSelector();
        moviePlayer = ExoPlayerFactory.newSimpleInstance(this, trackSelector);
        moviePlayer.addListener(new Player.DefaultEventListener() {
            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                super.onPlayerStateChanged(playWhenReady, playbackState);
                if (playbackState == Player.STATE_READY) {
                    hideProgressBar();
                }
            }
        });

        // prepare video source
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this, Util.getUserAgent(this, getPackageName()));
        MediaSource videoSource = new ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(movieUri);

        // configure video player
        moviePlayer.prepare(videoSource);
        movieView.setPlayer(moviePlayer);
    }

    private void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgressBar() {
        progressBar.setVisibility(View.INVISIBLE);
    }
}
