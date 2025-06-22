package com.s23010210.mindease;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class addQuote extends AppCompatActivity {

    private EditText quoteText;
    private Button addQuoteBtn;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_quote);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Initialize UI elements
        quoteText = findViewById(R.id.q_text_box);
        addQuoteBtn = findViewById(R.id.b_add_quote);
        Button later = findViewById(R.id.later_b);

        // Back to dashboard
        later.setOnClickListener(v -> {
            Intent intent = new Intent(addQuote.this, dashboard.class);
            startActivity(intent);
        });

        // Handle quote adding
        addQuoteBtn.setOnClickListener(v -> {
            String quote = quoteText.getText().toString().trim();

            if (TextUtils.isEmpty(quote)) {
                Toast.makeText(addQuote.this, "Write a quote to add", Toast.LENGTH_SHORT).show();
                return;
            }

            FirebaseUser user = mAuth.getCurrentUser();
            if (user != null) {
                String uid = user.getUid();

                Map<String, Object> quoteData = new HashMap<>();
                quoteData.put("quote", quote);
                quoteData.put("timestamp", System.currentTimeMillis());

                // Store in Firestore under users/{uid}/quotes
                db.collection("users")
                        .document(uid)
                        .collection("quotes")
                        .add(quoteData)
                        .addOnSuccessListener(documentReference -> {
                            Toast.makeText(addQuote.this, "Quote added successfully", Toast.LENGTH_SHORT).show();
                            quoteText.setText(""); // Clear the text box
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(addQuote.this, "Failed to add quote: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        });
            } else {
                Toast.makeText(addQuote.this, "User not logged in", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
