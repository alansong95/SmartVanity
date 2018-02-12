package com.example.alan.smartvanity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    Intent selectIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button button = findViewById(R.id.add_button);

        selectIntent = new Intent(this, SelectWidget.class);

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(selectIntent);
            }
        });
    }
}