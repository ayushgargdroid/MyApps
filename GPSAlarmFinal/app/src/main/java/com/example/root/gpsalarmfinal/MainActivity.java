package com.example.root.gpsalarmfinal;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class MainActivity extends AppCompatActivity {

    ImageButton walk,car,train;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        walk = (ImageButton)findViewById(R.id.imageButton1);
        train = (ImageButton)findViewById(R.id.imageButton2);
        car = (ImageButton)findViewById(R.id.imageButton3);

        walk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),MapsActivity.class);
                i.putExtra("radius",10);
                Log.d("info","Selected Walk");
                startActivity(i);
            }
        });
        train.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),MapsActivity.class);
                i.putExtra("radius",1000);
                Log.d("info","Selected Train");
                startActivity(i);
            }
        });
        car.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),MapsActivity.class);
                i.putExtra("radius",100);
                Log.d("info","Selected Car");
                startActivity(i);
            }
        });

    }


}
