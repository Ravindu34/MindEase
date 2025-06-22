package com.s23010210.mindease;

import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Signup extends AppCompatActivity {

    TextView Blogin;
    private EditText etName, etEmail, etPassword, etConfirmPassword;
    private Button btnSignUp;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        etName = findViewById(R.id.username_s);
        etEmail = findViewById(R.id.email_s);
        etPassword = findViewById(R.id.Password_f);
        etConfirmPassword = findViewById(R.id.c_password_s);
        btnSignUp = findViewById(R.id.SignUp);

        btnSignUp.setOnClickListener(v -> validateAndRegister());

        Blogin = findViewById(R.id.textView);
        Blogin.setOnClickListener(v -> {
            Intent intent = new Intent(Signup.this, login.class);
            startActivity(intent);
        });
    }

    private void validateAndRegister() {
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            etName.setError("Name is required");
            return;
        }

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

        if (password.length() < 6) {
            etPassword.setError("Password must be at least 6 characters");
            return;
        }

        if (!password.equals(confirmPassword)) {
            etConfirmPassword.setError("Passwords do not match");
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            // ✅ Set display name in Firebase Auth
                            user.updateProfile(new UserProfileChangeRequest.Builder()
                                            .setDisplayName(name)
                                            .build())
                                    .addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful()) {
                                            // ✅ Save user data in Firestore
                                            Map<String, Object> userData = new HashMap<>();
                                            userData.put("name", name);
                                            userData.put("email", email);
                                            userData.put("uid", user.getUid());

                                            db.collection("users").document(user.getUid())
                                                    .set(userData)
                                                    .addOnSuccessListener(aVoid -> {
                                                        Toast.makeText(Signup.this, "Registered & data saved", Toast.LENGTH_SHORT).show();
                                                        startActivity(new Intent(Signup.this, dashboard.class));
                                                        finish();
                                                    })
                                                    .addOnFailureListener(e -> {
                                                        Toast.makeText(Signup.this, "Firestore error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                                    });
                                        } else {
                                            Toast.makeText(Signup.this, "Failed to set display name", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    } else {
                        if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                            etEmail.setError("Email already registered");
                        } else {
                            Toast.makeText(Signup.this, "Auth error: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}
