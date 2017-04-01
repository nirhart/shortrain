package com.nirhart.shortrain.tutorial;

import android.app.Fragment;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nirhart.shortrain.R;

public class TutorialFragment extends Fragment implements TextureView.SurfaceTextureListener {

    private static final String MOVIE_RES_ID = "MOVIE_RES_ID";
    private static final String TEXT_RES_ID = "TEXT_ID";

    private MediaPlayer mediaPlayer;

    public static Fragment create(int movieResId, int textId) {
        TutorialFragment tutorialFragment = new TutorialFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(MOVIE_RES_ID, movieResId);
        bundle.putInt(TEXT_RES_ID, textId);
        tutorialFragment.setArguments(bundle);
        return tutorialFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.tutorial_view, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        if (mediaPlayer == null) {
            TextureView textureView = (TextureView) view.findViewById(R.id.tutorial_view_texture_view);
            textureView.setSurfaceTextureListener(this);
        }

        int textId = getArguments().getInt(TEXT_RES_ID);
        TextView textView = (TextView) view.findViewById(R.id.tutorial_view_text_view);
        textView.setOnClickListener((OnNextSlideClicked) getActivity());
        textView.setText(textId);
    }

    private void videoIsVisibleToUser() {
        mediaPlayer.seekTo(0);
        mediaPlayer.start();
    }

    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (mediaPlayer != null && isVisibleToUser) {
            videoIsVisibleToUser();
        }
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        Surface s = new Surface(surface);

        int movieId = getArguments().getInt(MOVIE_RES_ID);
        try {
            mediaPlayer = new MediaPlayer();
            String path = "android.resource://" + getContext().getPackageName() + "/" + movieId;
            mediaPlayer.setDataSource(getContext(), Uri.parse(path));
            mediaPlayer.setSurface(s);
            mediaPlayer.prepare();
            mediaPlayer.setLooping(true);
            if (isVisible()) {
                videoIsVisibleToUser();
            }
        } catch (Exception ignore) {
            // This is an experiment...
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
        mediaPlayer = null;
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
    }

    public interface OnNextSlideClicked extends View.OnClickListener {
    }
}
