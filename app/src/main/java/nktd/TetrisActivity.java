package nktd;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import app.R;
import nktd.speech_recognition.Recognizer;

public class TetrisActivity extends AppCompatActivity {

    Recognizer recognizer;
    boolean recognizerBound = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tetris);
        Intent intent = new Intent(this, Recognizer.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Recognizer.RecognizerBinder recognizerBinder = (Recognizer.RecognizerBinder) service;
            recognizer = recognizerBinder.getService();
            recognizerBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            recognizerBound = false;
        }
    };
}
