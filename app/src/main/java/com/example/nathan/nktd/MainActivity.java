package com.example.nathan.nktd;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    public static final String EXTRA_MESSAGE = "com.example.nathan.nktd.MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void openTetris(View view){
        Intent intent = new Intent(this, TetrisActivity.class);
        startActivity(intent);
    }

    public void openG2(View view){
        Intent intent = new Intent(this, TeragramActivity.class);
        startActivity(intent);
    }

    public void openG3(View view){
        Intent intent = new Intent(this, Game3Activity.class);
        startActivity(intent);
    }

    public void openG4(View view){
        Intent intent = new Intent(this, Game4Activity.class);
        startActivity(intent);
    }
}
