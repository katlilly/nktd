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
import android.widget.TextView;

import com.example.nathan.nktd.interfaces.SpeechResultListener;

import static android.widget.Toast.makeText;

public class MainActivity extends AppCompatActivity{
    public static final String EXTRA_MESSAGE = "com.example.nathan.nktd.MESSAGE";

    private static final int PERMISSIONS_REQUEST_RECORD_AUDIO = 1;

    private String result = ""; // what the interpreter hears

    private SpeechResultListener listener;

    private boolean recognizerBound = false;
    private Recognizer recognizerService;

    private Context context;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("status", "oncreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* Permissions taken directly from pocketSphinx's demo app*/
        int permissionCheck = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, PERMISSIONS_REQUEST_RECORD_AUDIO);
            return;
        }
        intent = new Intent(this, Recognizer.class);
        intent.setAction(Recognizer.MENU_SEARCH);
        startService(intent);
        context = getApplicationContext();
        context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("status", "onresume");
        if (!recognizerBound) {
            context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
        }
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
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startService(new Intent(this, Recognizer.class));
            } else {
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

    /* Update text box next to "What I heard" */
    public void updateResultBox(String string) {
        TextView resultText = findViewById(R.id.resultText);
        resultText.setText(string);
    }

    /* Recognizer-related interactions should go here. */
    public ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Recognizer.RecognizerBinder binder = (Recognizer.RecognizerBinder) service;
            recognizerService = binder.getService();
            recognizerBound = true;
            Log.d("status", "bound");
            /* Create listener and link it to recognizer. */
            recognizerService.setListener(new SpeechResultListener() {
                @Override
                public void onSpeechResult() {
                    String result = recognizerService.getResult();
                    updateResultBox(result);

                    /* Game opening happens here. */
                    switch (result) {
                        case "game two":
                            openG2(null);
                    }
                }
            });
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d("status", "disconnected");
            recognizerBound = false;
        }
    };

}
