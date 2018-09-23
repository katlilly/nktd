package com.example.nathan.nktd;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.nathan.nktd.interfaces.RecognizedActivity;
import com.example.nathan.nktd.interfaces.SpeechResultListener;
import com.example.nathan.nktd.nktd2048.MainActivity2048;

import org.jfedor.frozenbubble.FrozenBubble;

public class MainActivity extends RecognizedActivity{

    private static final int PERMISSIONS_REQUEST_RECORD_AUDIO = 1;

    private Intent recognizerStarterIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("status", "oncreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* Start recognizer */
        recognizerStarterIntent = new Intent(this, Recognizer.class);
        startService(recognizerStarterIntent);

        recognizerListener = new SpeechResultListener(this) {
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
                    case "game four":
                        openG4(null);
                        break;
                    case "frozen bubble":
                        openG4(null);
                        break;
                }
            }
        };

        /* Permissions taken almost directly from pocketSphinx's demo app*/
        int permissionCheck = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, PERMISSIONS_REQUEST_RECORD_AUDIO);
        }
        setup();
    }

    @Override
    protected void onResume() {
        super.onResume();
        restartRecognizer();
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
        swapActivity(com.example.nathan.nktd.TeragramActivity.class);
    }

    public void openG3(View view){
        swapActivity(com.example.nathan.nktd.nktd2048.MainActivity2048.class);
    }

    public void openG4(View view){
        swapActivity(org.jfedor.frozenbubble.FrozenBubble.class);
    }
}
