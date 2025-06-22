package com.s23010210.mindease;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class calm_down extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calm_down);

        TextView cdQuoteText = findViewById(R.id.cd_quotes_showing);
        Button backToDashboard = findViewById(R.id.calmdown_b);

        String quote = getIntent().getStringExtra("quote_text");
        cdQuoteText.setText(quote != null ? quote : "No quote found.");

        backToDashboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(calm_down.this, dashboard.class);
                intent.putExtra("quote_returned", quote); // Optional, in case you want to use
                startActivity(intent);
                finish(); // remove calm_down from back stack
            }
        });
    }
}
