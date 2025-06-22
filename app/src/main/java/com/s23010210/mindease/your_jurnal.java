package com.s23010210.mindease;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;

public class your_jurnal extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_your_jurnal);

        Button jBtn;

            jBtn = findViewById(R.id.j_btn);

            jBtn.setOnClickListener(v -> showPopup(v));
        }

        private void showPopup(View anchorView) {
            // Inflate the popup layout
            LayoutInflater inflater = getLayoutInflater();
            View popupView = inflater.inflate(R.layout.popup_layout, null);

            // Create the PopupWindow
            PopupWindow popupWindow = new PopupWindow(
                    popupView,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    true // Focusable
            );

            // Show the popup below the button (or change to showAtLocation for center)
            popupWindow.showAtLocation(anchorView, android.view.Gravity.CENTER, 0, 0);


            // Close button action
            Button closePopup = popupView.findViewById(R.id.close_popup);
            closePopup.setOnClickListener(v -> popupWindow.dismiss());
    }
}