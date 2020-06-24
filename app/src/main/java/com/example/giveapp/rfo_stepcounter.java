package com.example.giveapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class rfo_stepcounter extends AppCompatActivity {

    private TextView stepCounter;
    private double magnitudePrev = 0;
    private int count = 0;

    Button donateBtn;
    FirebaseFirestore db = FirebaseFirestore.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rfo_stepcounter);

        stepCounter = findViewById(R.id.numSteps);
        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        SensorEventListener stepDetector = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                if (event!=null) {
                    float x_acceleration = event.values[0];
                    float y_acceleration = event.values[1];
                    float z_acceleration = event.values[2];

                    double magnitude = Math.sqrt(x_acceleration*x_acceleration + y_acceleration*y_acceleration + z_acceleration*z_acceleration);
                    double magnitudeDelta = magnitude - magnitudePrev;
                    magnitudePrev = magnitude;

                    if (magnitudeDelta > 6) { // 10 for running, put 6 now to see if its "working"
                        count++;
                    }
                    stepCounter.setText(Integer.toString(count));
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };

        sensorManager.registerListener(stepDetector, sensor, SensorManager.SENSOR_DELAY_NORMAL);

        donateBtn = findViewById(R.id.donateBtn);
        donateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(rfo_stepcounter.this, rfo_donate.class));
            }
        });

/*        Beneficiary test = new Beneficiary("TESTING", "", "", 0);
Beneficiary ccf = new Beneficiary("TESTING", "", "", 0);
Beneficiary ffth = new Beneficiary("TESTING", "", "", 0);
       db.collection("beneficiaries").add(ffth);
       db.collection("beneficiaries").add(ccf);
       db.collection("beneficiaries").add(test);
*/

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    protected void onPause() {
        super.onPause();

        SharedPreferences sharedPreferences = getSharedPreferences("countSP", MODE_PRIVATE);
        sharedPreferences.edit().putInt("count", count).apply();
    }

    protected void onStop() {
        super.onStop();

        SharedPreferences sharedPreferences = getSharedPreferences("countSP", MODE_PRIVATE);
        sharedPreferences.edit().putInt("count", count).apply();
    }

    protected void onResume() {
        super.onResume();

        SharedPreferences sharedPreferences = getSharedPreferences("countSP", MODE_PRIVATE);
        count = sharedPreferences.getInt("count", 0);
    }

    
}