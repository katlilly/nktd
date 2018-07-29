package com.example.nathan.nktd.interfaces;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.example.nathan.nktd.R;
import com.example.nathan.nktd.Recognizer;

public abstract class RecognizedActivity extends AppCompatActivity {

    protected SpeechResultListener recognizerListener;

    protected static boolean recognizerBound;
    protected static Recognizer recognizerService;
    protected boolean recognizerListening = true;
    protected ImageButton recognizerButton;


    protected void bindRecognizer() {
        Intent recognizerIntent = new Intent(this, Recognizer.class);
        bindService(recognizerIntent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    protected void setButton() {
        if (recognizerListening) {
            recognizerButton.setImageDrawable(getResources().getDrawable(R.drawable.listening));
        } else {
            recognizerButton.setImageDrawable(getResources().getDrawable(R.drawable.notlistening));
        }
    }

    protected void setButton(Intent intent) {
        recognizerListening = intent.getBooleanExtra("listening", true);
        if (recognizerListening) {
            recognizerButton.setImageDrawable(getResources().getDrawable(R.drawable.listening));
        } else {
            recognizerButton.setImageDrawable(getResources().getDrawable(R.drawable.notlistening));
        }
    }

    protected void restartRecognizer() {
        if (recognizerBound) {
            if(recognizerListener != null) {
                recognizerService.setListener(recognizerListener);
            } else {
                Log.d("ERROR", "listener not created");
            }
            recognizerListening = recognizerService.isListening();
        }
        if (recognizerButton != null) {
            setButton();
        } else {
            Log.e("ERROR", "recognizerButton not set up");
        }
    }

    public void onOff(View view) {
        if(recognizerService.isListening()) {
            recognizerService.stopRecognition();
        } else {
            recognizerService.startRecognition(recognizerService.MENU_SEARCH);
        }
    }

    public ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Recognizer.RecognizerBinder binder = (Recognizer.RecognizerBinder) service;
            recognizerService = binder.getService();
            recognizerBound = true;
            if(recognizerListener != null) {
                recognizerService.setListener(recognizerListener);
            } else {
                Log.d("ERROR", "listener not created");
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            recognizerBound = false;
        }
    };

}
