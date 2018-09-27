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
import com.example.nathan.nktd.PowersofTwo;
import com.example.nathan.nktd.R;
import com.example.nathan.nktd.Recognizer;
import com.example.nathan.nktd.TeragramActivity;
import com.example.nathan.nktd.nktd2048.MainActivity2048;

import org.jfedor.frozenbubble.FrozenBubble;

import java.util.HashMap;
import java.util.Map;

public abstract class RecognizedActivity extends AppCompatActivity {

    protected SpeechResultListener recognizerListener;
    public static Recognizer recognizerService;

    protected static boolean recognizerBound; //is there a bound recognizer?
    protected static boolean recognizerListening; //should the recognizer be listening?

    protected ImageButton recognizerButton;
    protected Dialog exitDialog;

    protected String callingClassSearch; //the search to switch back to upon game exit.

    /* Default search modes for each Activity */
    public static Map<Class, String> defaultSearches = new HashMap<>();
    static {
        defaultSearches.put(MainActivity.class, Recognizer.MENU_SEARCH);
        defaultSearches.put(TeragramActivity.class, Recognizer.TERAGRAM_SEARCH);
        defaultSearches.put(MainActivity2048.class, Recognizer.TWENTY_FORTY_EIGHT_SEARCH);
        defaultSearches.put(FrozenBubble.class, Recognizer.FROZENBUBBLE_SEARCH);
        defaultSearches.put(PowersofTwo.class, Recognizer.POWERS_OF_TWO_SEARCH);
    }

    /**
     * Swaps to another RecognizedActivity.
     * @param swapTo the class of the activity to swap to.
     */
    protected void swapActivity(Class swapTo) {
        recognizerService.swapSearch(defaultSearches.get(swapTo));
        Intent intent = new Intent(this, swapTo);
        intent.putExtra("listening", recognizerListening);
        intent.putExtra("callingClassSearch", defaultSearches.get(this.getClass()));
        startActivity(intent);
    }

    /**
     * Setup procedures common to all RecognizedActivities.
     */
    protected void setup() {
        recognizerBound = false;
        if (!(this.getIntent().getStringExtra("callingClassSearch") == null)) {
            Intent callingIntent = this.getIntent();
            callingClassSearch = callingIntent.getStringExtra("callingClassSearch");
            recognizerListening = callingIntent.getBooleanExtra("listening", true);
        } else {
            recognizerListening = true;
        }
        recognizerButton = findViewById(R.id.recognizerStatus);
        bindRecognizer();
        setButton();
    }

    /**
     * Bind a Recognizer service to this Activity.
     */
    private void bindRecognizer() {
        Intent recognizerIntent = new Intent(this, Recognizer.class);
        bindService(recognizerIntent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    protected String savedSearch;
    protected SpeechResultListener savedListener;
    /**
     * Brings up an exit dialog box, swapping the Recognizer search to yes/no mode.
     */
    protected void showExitDialog() {
        savedSearch = recognizerService.getSearchName();
        savedListener = recognizerListener;
        recognizerService.swapSearch(Recognizer.YESNO_SEARCH);
        exitDialog = new Dialog(this);
        recognizerService.setListener(new SpeechResultListener(this) {
            @Override
            public void onSpeechResult() {
                String result = recognizerService.getResult();
                switch(result) {
                    case "yes":
                        exitGame(null);
                        break;
                    case "no":
                        dismissExitDialog(null);
                        recognizerService.setListener(savedListener);
                        break;
                }
            }
        });
        exitDialog.setContentView(R.layout.exit_dialog);
        exitDialog.show();
    }

    /**
     * Exits from this Activity.
     * @param view the button pressed to call this method, null if it was called by voice.
     */
    protected void exitGame(View view) {
        recognizerService.swapSearch(callingClassSearch);
        if (recognizerBound) {
            this.unbindService(serviceConnection);
        }
        finish();
    }

    /**
     * Dismisses dialog box and swaps back from yes/no to previous search mode.
     * @param view The button pressed to call this method, null if it was called by voice.
     */
    protected void dismissExitDialog(View view) {
        recognizerService.swapSearch(savedSearch);
        exitDialog.dismiss();
    }

    /**
     * Sets the Recognizer button to the correct image based on whether it is on or off.
     */
    private void setButton() {
        if (recognizerListening) {
            recognizerButton.setImageDrawable(getResources().getDrawable(R.drawable.listening));
        } else {
            recognizerButton.setImageDrawable(getResources().getDrawable(R.drawable.notlistening));
        }
    }

    /**
     * Sets the Recognizer button to the correct image based on the 'listening' extra of
     * the calling Intent.
     * @param intent the calling Intent.
     */
    protected void setButton(Intent intent) {
        recognizerListening = intent.getBooleanExtra("listening", true);
        if (recognizerListening) {
            recognizerButton.setImageDrawable(getResources().getDrawable(R.drawable.listening));
        } else {
            recognizerButton.setImageDrawable(getResources().getDrawable(R.drawable.notlistening));
        }
    }

    /**
     * Reattaches a listener to the recognizer. To be called if focus has previously shifted from this Activity.
     */
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

    /**
     * Toggles the Recognizer on or off.
     * @param view The button pressed to call this method.
     */
    public void onOff(View view) {
        if(recognizerService.isListening()) {
            stopRecognition();
            recognizerListening = false;
        } else {
            startRecognition();
            recognizerListening = true;
        }
    }

    /* Ensure proper Recognizer-related tasks are performed when back button is
     * pressed. */
    @Override
    public void onBackPressed() {
        Log.d("status", "onBackPressed");
        if(this.getClass() != MainActivity.class) {
            exitGame(null);
        }
    }

    /* Ensure proper Recognizer-related tasks are performed when options menu back button
     * is pressed. */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            exitGame(null);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    /* Ensure Recognizer stops when focus is shifted from this Activity. */
    @Override
    protected void onPause() {
        super.onPause();
        if (recognizerBound) {
            stopRecognition();
        }
    }

    /* Ensure Recognizer restarts when focus returns to this Activity. */
    @Override
    protected void onResume() {
        super.onResume();
        if (recognizerBound) {
            if (recognizerListening) {
                startRecognition();
            } else {
                stopRecognition();
            }
        }
    }

    /**
     * Stop Recognizer.
     */
    protected void stopRecognition() {
        recognizerService.stopRecognition();
    }

    /**
     * Start Recognizer.
     */
    protected void startRecognition() {
        recognizerService.startRecognition();
    }

    /* A connection between this Activity and a Recognizer service.
     * Ensure a SpeechResultListener is attached to the service. */
    public ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Recognizer.RecognizerBinder binder = (Recognizer.RecognizerBinder) service;
            recognizerService = binder.getService();
            recognizerBound = true;
            if(recognizerListener != null) {
                recognizerService.setListener(recognizerListener);
                recognizerListener.onBound();
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
