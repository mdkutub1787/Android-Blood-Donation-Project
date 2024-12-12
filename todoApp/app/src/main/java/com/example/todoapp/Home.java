package com.example.todoapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class Home extends AppCompatActivity {

    private Button btnView, btnCreate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Initialize buttons
        btnView = findViewById(R.id.btnView);
        btnCreate = findViewById(R.id.btnCreate);

        // Set OnClickListener for the "View Todo" button
        btnView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to GetActivity
                Intent intent = new Intent(Home.this, GetActivity.class);
                startActivity(intent);
            }
        });

        // Set OnClickListener for the "Create Todo" button
        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to PostActivity
                Intent intent = new Intent(Home.this, PostActivity.class);
                startActivity(intent);
            }
        });
    }
}
