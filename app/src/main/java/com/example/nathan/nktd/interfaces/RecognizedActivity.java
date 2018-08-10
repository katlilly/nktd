package com.example.nathan.nktd.interfaces;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.example.nathan.nktd.MainActivity;
import com.example.nathan.nktd.R;
import com.example.nathan.nktd.Recognizer;

public abstract class RecognizedActivity extends AppCompatActivity {

    protected SpeechResultListener recognizerListener;

    protected static boolean recognizerBound;
    public static Recognizer recognizerService;
    protected boolean recognizerListening = true;
    protected ImageButton recognizerButton;

    Dialog exitDialog;

    protected void bindRecognizer() {
        Log.d("binding", "");
        Intent recognizerIntent = new Intent(this, Recognizer.class);
        bindService(recognizerIntent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    String savedSearch;
    public void showExitDialog() {
        savedSearch = recognizerService.getSearchName();
        recognizerService.swapSearch(Recognizer.YESNO_SEARCH);
        exitDialog = new Dialog(this);
        exitDialog.setContentView(R.layout.exit_dialog);
        exitDialog.show();
    }

    public void exitGame(View view) {
        recognizerService.swapSearch(Recognizer.MENU_SEARCH);
        finish();
    }

    public void dismissExitDialog(View view) {
        recognizerService.swapSearch(savedSearch);
        exitDialog.dismiss();
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
            recognizerService.startRecognition();
        }
    }

    @Override
    public void onBackPressed() {
        if(this.getClass() != MainActivity.class) {
            exitGame(null);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            exitGame(null);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
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
