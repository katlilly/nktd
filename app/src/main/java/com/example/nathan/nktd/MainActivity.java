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

import java.util.HashMap;
import java.util.Map;
import org.jfedor.frozenbubble.FrozenBubble;

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
                    case "game four":
                        openG4(null);
                        break;
                    case "frozen bubble":
                        openG4(null);
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

            @Override
            public void onConfirmExit() {

            }

            @Override
            public void onDenyExit() {

            }
        };

        /* Permissions taken almost directly from pocketSphinx's demo app*/
        int permissionCheck = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, PERMISSIONS_REQUEST_RECORD_AUDIO);
        }

        startService(recognizerStarterIntent);
        bindRecognizer();
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

    /* Searches to be switched to for each game */
    private static Map<Class, String> defaultSearches = new HashMap<>();
    static {
        defaultSearches.put(TeragramActivity.class, Recognizer.TERAGRAM_SEARCH);
        defaultSearches.put(MainActivity2048.class, Recognizer.TWENTY_FORTY_EIGHT_SEARCH);
        defaultSearches.put(FrozenBubble.class, Recognizer.FROZENBUBBLE_SEARCH);
    }

    private void startGame(Class game) {
        recognizerService.swapSearch(defaultSearches.get(game));
        Intent intent = new Intent(this, game);
        intent.putExtra("listening", recognizerListening);
        startActivity(intent);
    }

    public void openTetris(View view){
        Intent intent = new Intent(this, TetrisActivity.class);
        startActivity(intent);
    }

    public void openG2(View view){
        startGame(TeragramActivity.class);
    }

    public void openG3(View view){
        startGame(MainActivity2048.class);
    }

    public void openG4(View view){
        startGame(FrozenBubble.class);
    }
}
