package com.s23010210.mindease;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class panic_mode extends AppCompatActivity {

    ImageView pauseButton;
    MediaPlayer mediaPlayer;
    boolean isPausedByUser = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_panic_mode);

        View rootLayout = findViewById(R.id.root_layout);
        pauseButton = findViewById(R.id.stop_panic_sound);

        // Initialize and start music
        mediaPlayer = MediaPlayer.create(this, R.raw.alarm_alart);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();

        // Click to pause/resume
        pauseButton.setOnClickListener(v -> {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                isPausedByUser = true;
            } else {
                mediaPlayer.start();
                isPausedByUser = false;
            }
        });

        // Background animation
        int[] colors = {
                Color.parseColor("#E6E6FA"),
                Color.parseColor("#D8BFD8"),
                Color.parseColor("#DA70D6"),
                Color.parseColor("#BA55D3"),
                Color.parseColor("#9932CC"),
                Color.parseColor("#BA55D3"),
                Color.parseColor("#DA70D6"),
                Color.parseColor("#E6E6FA"),
        };

        ValueAnimator colorAnimation = ValueAnimator.ofFloat(0, colors.length - 1);
        colorAnimation.setDuration(8000);
        colorAnimation.setRepeatCount(ValueAnimator.INFINITE);

        colorAnimation.addUpdateListener(animation -> {
            float position = (float) animation.getAnimatedValue();
            int index = (int) position;
            int nextIndex = (index + 1) % colors.length;
            float fraction = position - index;
            int color = (Integer) new ArgbEvaluator().evaluate(fraction, colors[index], colors[nextIndex]);
            rootLayout.setBackgroundColor(color);
        });

        colorAnimation.start();
    }

    // Automatically pause music when activity is not visible
    @Override
    protected void onPause() {
        super.onPause();
        if (mediaPlayer != null && mediaPlayer.isPlaying() && !isPausedByUser) {
            mediaPlayer.pause();
        }
    }

    // Resume music only if not paused manually
    @Override
    protected void onResume() {
        super.onResume();
        if (mediaPlayer != null && !mediaPlayer.isPlaying() && !isPausedByUser) {
            mediaPlayer.start();
        }
    }

    // Stop and release resources
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
