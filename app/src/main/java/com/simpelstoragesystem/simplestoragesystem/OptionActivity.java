package com.simpelstoragesystem.simplestoragesystem;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import io.particle.android.sdk.utils.Toaster;

public class OptionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_option);

        Button apply = findViewById(R.id.button_apply);
        apply.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Toaster.s(OptionActivity.this, "Changes applied!");
                finish();
            }
        });

        Button calibrate0 = findViewById(R.id.sensor0calibrate);
        calibrate0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toaster.s(OptionActivity.this, "Calibrated sensor 0");

            }
        });

        Button calibrate1 = findViewById(R.id.sensor1calibrate);
        calibrate1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toaster.s(OptionActivity.this, "Calibrated sensor 1");

            }
        });

        Button calibrate2 = findViewById(R.id.sensor2calibrate);
        calibrate2.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                Toaster.s(OptionActivity.this, "Calibrated sensor 2");

            }
        });

        Button calibrate3 = findViewById(R.id.sensor3calibrate);
        calibrate3.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Toaster.s(OptionActivity.this, "Calibrated sensor 3");

            }
        });

    }
}

