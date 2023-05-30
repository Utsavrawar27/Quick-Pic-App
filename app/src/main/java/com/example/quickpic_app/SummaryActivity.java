package com.example.quickpic_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class SummaryActivity extends AppCompatActivity {

    private TextView tvSummary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);

        tvSummary = findViewById(R.id.tv_summary);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("summary")) {
            String summary = intent.getStringExtra("summary");
            tvSummary.setText(summary);
        }
    }
    public void onquickpicmain(View view) {
        Intent intent = new Intent(SummaryActivity.this, HomePage.class);
        startActivity(intent);
    }
}