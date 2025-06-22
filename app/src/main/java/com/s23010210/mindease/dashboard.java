package com.s23010210.mindease;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.media.MediaPlayer;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class dashboard extends AppCompatActivity {

    private TextView quoteShowing;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String uid;
    private SharedPreferences prefs;
    MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        ImageView mapImage = findViewById(R.id.mood_map_b);
        ImageView panicImage = findViewById(R.id.panic_button);
        ImageView jurnalImage = findViewById(R.id.your_jurnul_b);
        ImageView soundImage = findViewById(R.id.relaxing_sounds_b);
        ImageView communityImage = findViewById(R.id.community_wall_b);
        ImageView moreInfo = findViewById((R.id.more_i));

        mapImage.setOnClickListener(v -> {
            Intent intent = new Intent(dashboard.this, mood_map.class);
            startActivity(intent);
        });

        panicImage.setOnClickListener(v -> {
            Intent intent = new Intent(dashboard.this, panic_mode.class);
            startActivity(intent);

        });

        moreInfo.setOnClickListener(v -> {
            Intent intent = new Intent(dashboard.this, profile.class);
            startActivity(intent);

        });

        soundImage.setOnClickListener(v -> {
            Intent intent = new Intent(dashboard.this, relaxing_sounds.class);
            startActivity(intent);

        });

        jurnalImage.setOnClickListener(v -> {
            Intent intent = new Intent(dashboard.this, your_jurnal.class);
            startActivity(intent);

        });

        communityImage.setOnClickListener(v -> {
            Intent intent = new Intent(dashboard.this, community_wall.class);
            startActivity(intent);

        });

        // Firebase setup
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            uid = user.getUid();
        } else {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        prefs = getSharedPreferences("MindEasePrefs", MODE_PRIVATE);
        quoteShowing = findViewById(R.id.quotes_showing);

        // Restore saved quote
        String savedQuote = prefs.getString("last_quote", null);
        if (savedQuote != null) {
            quoteShowing.setText(savedQuote);
        }

        // Happy & Normal
        ImageView happyImage = findViewById(R.id.happy);
        ImageView normalImage = findViewById(R.id.normal);


        View.OnClickListener clearQuoteAndGo = v -> {
            quoteShowing.setText(""); // Clear it
            SharedPreferences.Editor editor = prefs.edit();
            editor.remove("last_quote");
            editor.apply();

            Intent intent = new Intent(dashboard.this, addQuote.class);
            startActivity(intent);
        };

        happyImage.setOnClickListener(clearQuoteAndGo);
        normalImage.setOnClickListener(clearQuoteAndGo);

        // Sad / Soo Sad / Angry
        ImageView sadImage = findViewById(R.id.sad);
        ImageView sooSadImage = findViewById(R.id.soo_sad);
        ImageView angryImage = findViewById(R.id.angry);

        View.OnClickListener moodClickListener = v -> {
            db.collection("users")
                    .document(uid)
                    .collection("quotes")
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        List<String> quotes = new ArrayList<>();
                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                            String quote = doc.getString("quote");
                            if (quote != null && !quote.trim().isEmpty()) {
                                quotes.add(quote);
                            }
                        }

                        String quoteToSend = quotes.isEmpty()
                                ? "You haven't added any quotes yet."
                                : quotes.get(new Random().nextInt(quotes.size()));

                        // Show in dashboard
                        quoteShowing.setText(quoteToSend);

                        // Save to preferences
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString("last_quote", quoteToSend);
                        editor.apply();

                        // Send to calm_down
                        Intent intent = new Intent(dashboard.this, calm_down.class);
                        intent.putExtra("quote_text", quoteToSend);
                        startActivity(intent);
                    })
                    .addOnFailureListener(e -> {
                        String errorQuote = "Something went wrong. Try again.";
                        quoteShowing.setText(errorQuote);

                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString("last_quote", errorQuote);
                        editor.apply();

                        Intent intent = new Intent(dashboard.this, calm_down.class);
                        intent.putExtra("quote_text", errorQuote);
                        startActivity(intent);
                    });
        };

        sadImage.setOnClickListener(moodClickListener);
        sooSadImage.setOnClickListener(moodClickListener);
        angryImage.setOnClickListener(moodClickListener);
    }

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
