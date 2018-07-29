package com.example.nathan.nktd;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.example.nathan.nktd.interfaces.RecognizedActivity;
import com.example.nathan.nktd.interfaces.SpeechResultListener;
import com.example.nathan.nktd.nktd2048.MainActivity2048;

import static android.widget.Toast.makeText;

public class MainActivity extends RecognizedActivity{

    private static final int PERMISSIONS_REQUEST_RECORD_AUDIO = 1;

    private Intent recognizerStarterIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("status", "oncreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* Recognizer Setup */
        recognizerButton = findViewById(R.id.recognizerStatus);
        recognizerStarterIntent = new Intent(this, Recognizer.class);
        recognizerStarterIntent.putExtra("searchName", Recognizer.MENU_SEARCH);

        recognizerListener = new SpeechResultListener() {
            @Override
            public void onSpeechResult() {
                String result = recognizerService.getResult();

                /* Game opening happens here. */
                switch (result) {
                    case "game two":
                        openG2(null);
                        break;
                    case "tear a gram":
                        openG2(null);
                        break;
                    case "game three":
                        openG3(null);
                        break;
                    case "twenty forty eight":
                        openG3(null);
                        break;
                }
            }

            @Override
            public void onStartRecognition() {
                recognizerListening = true;
                recognizerButton.setImageDrawable(getResources().getDrawable(R.drawable.listening));
            }

            @Override
            public void onStopRecognition() {
                recognizerListening = false;
                recognizerButton.setImageDrawable(getResources().getDrawable(R.drawable.notlistening));
            }

            @Override
            public void onNumberRecognition() {
                recognizerButton.setImageDrawable(getResources().getDrawable(R.drawable.listening_number));
            }
        };

        /* Permissions taken almost directly from pocketSphinx's demo app*/
        int permissionCheck = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, PERMISSIONS_REQUEST_RECORD_AUDIO);
        }

        startService(recognizerStarterIntent);
        bindRecognizer(Recognizer.MENU_SEARCH);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("status", "onresume");
        restartRecognizer();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("status", "onpause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("status", "onstop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("status", "ondestroy");
    }

    /* Permissions request taken directly from pocketSphinx's demo app */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions
            , @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST_RECORD_AUDIO) {
            if (!(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                Log.d("status", "grandResults.length !> 0");
                finish();
            }
        }
    }

    public void openTetris(View view){
        Intent intent = new Intent(this, TetrisActivity.class);
        startActivity(intent);
    }

    public void openG2(View view){
        Intent intent = new Intent(this, TeragramActivity.class);
        intent.putExtra("listening", recognizerListening);
        startActivity(intent);
    }

    public void openG3(View view){
        Intent intent = new Intent(this, MainActivity2048.class);
        intent.putExtra("listening", recognizerListening);
        startActivity(intent);
    }

    public void openG4(View view){
        Intent intent = new Intent(this, Game4Activity.class);
        startActivity(intent);
    }
}
