package com.s23010210.mindease;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.concurrent.Executor;

public class Fingerprint_login extends AppCompatActivity {

    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fingerprint_login);

        // Step 1: Check if device supports biometric
        BiometricManager biometricManager = BiometricManager.from(this);
        if (biometricManager.canAuthenticate() != BiometricManager.BIOMETRIC_SUCCESS) {
            Toast.makeText(this, "Biometric authentication not available", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Step 2: Set up the executor and biometric prompt
        Executor executor = ContextCompat.getMainExecutor(this);
        biometricPrompt = new BiometricPrompt(Fingerprint_login.this, executor,
                new BiometricPrompt.AuthenticationCallback() {
                    @Override
                    public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                        super.onAuthenticationSucceeded(result);

                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        if (user != null) {
                            // User still logged in, go to dashboard
                            Intent intent = new Intent(Fingerprint_login.this, dashboard.class);
                            startActivity(intent);
                            finish();
                        } else {
                            // No user logged in, redirect to normal login
                            Toast.makeText(Fingerprint_login.this, "User session expired", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(Fingerprint_login.this, Signup.class);
                            startActivity(intent);
                            finish();
                        }
                    }

                    @Override
                    public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                        super.onAuthenticationError(errorCode, errString);
                        Toast.makeText(Fingerprint_login.this, "Error: " + errString, Toast.LENGTH_SHORT).show();
                        finish(); // Optional: close the fingerprint screen
                    }

                    @Override
                    public void onAuthenticationFailed() {
                        super.onAuthenticationFailed();
                        Toast.makeText(Fingerprint_login.this, "Fingerprint not recognized", Toast.LENGTH_SHORT).show();
                    }
                });

        // Step 3: Build the prompt info
        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Fingerprint Login")
                .setSubtitle("Use fingerprint to log in to your MindEase account")
                .setNegativeButtonText("Cancel")
                .build();

        // Step 4: Start authentication
        biometricPrompt.authenticate(promptInfo);
    }

}
