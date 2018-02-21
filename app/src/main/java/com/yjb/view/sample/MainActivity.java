package com.yjb.view.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.yjb.view.WaveProgressView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final WaveProgressView wpv = findViewById(R.id.wpv_sample);
        findViewById(R.id.btn_start_animation).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wpv.startAnimation();
            }
        });
        findViewById(R.id.btn_stop_animation).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wpv.stopAnimation();
            }
        });
    }
}
