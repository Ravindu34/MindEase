package com.s23010210.mindease;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.*;

public class login extends AppCompatActivity {

    TextView signup;
    private EditText etEmail, etPassword;
    private Button btnSignIn;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check fingerprint preference before loading UI
        SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        boolean fingerprintEnabled = preferences.getBoolean("fingerprint_enabled", false);

        if (fingerprintEnabled) {
            Intent intent = new Intent(login.this, Fingerprint_login.class);
            startActivity(intent);
            finish(); // prevent returning to this screen
            return;
        }

        // Normal Login UI
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        // UI elements
        etEmail = findViewById(R.id.email);
        etPassword = findViewById(R.id.password);
        btnSignIn = findViewById(R.id.button);
        signup = findViewById(R.id.text_signup);

        // Sign In button
        btnSignIn.setOnClickListener(view -> validateAndLogin());

        // Sign Up link
        signup.setOnClickListener(v -> {
            Intent intent = new Intent(login.this, Signup.class);
            startActivity(intent);
        });
    }

    private void validateAndLogin() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Email is required");
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Enter a valid email");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Password is required");
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(login.this, "Login successful", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(login.this, dashboard.class));
                        finish();
                    } else {
                        Exception e = task.getException();
                        if (e instanceof FirebaseAuthInvalidUserException) {
                            etEmail.setError("No account found with this email");
                        } else if (e instanceof FirebaseAuthInvalidCredentialsException) {
                            etPassword.setError("Incorrect password");
                        } else {
                            Toast.makeText(login.this, "Login failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}
