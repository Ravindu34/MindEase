package com.s23010210.mindease;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class profile extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private Switch fingerprintSwitch;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Setup fingerprint switch
        fingerprintSwitch = findViewById(R.id.e_fingerprint);
        preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);

        boolean isEnabled = preferences.getBoolean("fingerprint_enabled", false);
        fingerprintSwitch.setChecked(isEnabled);

        fingerprintSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("fingerprint_enabled", isChecked);
            editor.apply();
        });

        // Show user info
        TextView showName = findViewById(R.id.show_un);
        TextView showGmail = findViewById(R.id.show_gmail);
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            String uid = currentUser.getUid();
            db.collection("users").document(uid).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String username = documentSnapshot.getString("name");
                            String email = documentSnapshot.getString("email");
                            showName.setText(username != null ? username : "N/A");
                            showGmail.setText(email != null ? email : "N/A");
                        } else {
                            Toast.makeText(profile.this, "User data not found", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(profile.this, "Failed to fetch user data", Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
        }

        // Sign out button
        Button signout = findViewById(R.id.signout);
        signout.setOnClickListener(v -> {
            Intent intent = new Intent(profile.this, login.class);
            startActivity(intent);
        });

        // Delete account popup trigger
        ImageView deleteAccount = findViewById(R.id.delete_account);
        deleteAccount.setOnClickListener(this::showPopup);
    }

    private void showPopup(View anchorView) {
        LayoutInflater inflater = getLayoutInflater();
        View popupView = inflater.inflate(R.layout.popup_layout, null);

        PopupWindow popupWindow = new PopupWindow(
                popupView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                true
        );

        popupWindow.showAtLocation(anchorView, Gravity.CENTER, 0, 0);

        // Close popup
        Button closePopup = popupView.findViewById(R.id.close_popup);
        closePopup.setOnClickListener(v -> popupWindow.dismiss());

        // Delete account
        Button confirmDelete = popupView.findViewById(R.id.delete_popup);
        confirmDelete.setOnClickListener(v -> {
            FirebaseUser user = mAuth.getCurrentUser();

            if (user != null) {
                String uid = user.getUid();

                // Disable fingerprint switch and save preference
                fingerprintSwitch.setChecked(false);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean("fingerprint_enabled", false);
                editor.apply();

                // Delete user data from Firestore
                db.collection("users").document(uid).delete()
                        .addOnSuccessListener(aVoid -> {
                            // Delete Firebase Auth user
                            user.delete()
                                    .addOnCompleteListener(task -> {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(profile.this, "Account deleted", Toast.LENGTH_SHORT).show();
                                            popupWindow.dismiss();
                                            Intent intent = new Intent(profile.this, login.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(intent);
                                        } else {
                                            Toast.makeText(profile.this, "Failed to delete Firebase user", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(profile.this, "Failed to delete Firestore data", Toast.LENGTH_SHORT).show();
                        });
            } else {
                Toast.makeText(profile.this, "No user is logged in", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
