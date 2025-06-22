package com.s23010210.mindease;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private int progressStatus = 0;
    private Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar = findViewById(R.id.splashProgress);

        new Thread(() -> {
            while (progressStatus < 100) {
                progressStatus += 1;

                handler.post(() -> progressBar.setProgress(progressStatus));

                try {
                    Thread.sleep(20); // Simulate loading
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            handler.post(() -> {
                // After progress complete, navigate to LoginActivity
                Intent intent = new Intent(MainActivity.this, login.class);
                startActivity(intent);
                finish();
            });

        }).start();
    }
}