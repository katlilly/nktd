package com.example.nathan.nktd;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.nathan.nktd.interfaces.SpeechResultListener;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.cmu.pocketsphinx.Assets;
import edu.cmu.pocketsphinx.Hypothesis;
import edu.cmu.pocketsphinx.RecognitionListener;
import edu.cmu.pocketsphinx.SpeechRecognizer;
import edu.cmu.pocketsphinx.SpeechRecognizerSetup;

import static android.widget.Toast.makeText;

public class MainActivity extends AppCompatActivity implements RecognitionListener{
    public static final String EXTRA_MESSAGE = "com.example.nathan.nktd.MESSAGE";

    public static final String MENU_SEARCH = "menu";
    public static final String TERAGRAM_SEARCH = "teragram";
    public static final String NUMBER_SEARCH = "number";

    private static final int PERMISSIONS_REQUEST_RECORD_AUDIO = 1;

    private String result = ""; // what the interpreter hears

    private SpeechRecognizer recognizer;
    private SpeechResultListener listener;

    private boolean setupComplete = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* Permissions taken directly from pocketSphinx's demo app*/
        int permissionCheck = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, PERMISSIONS_REQUEST_RECORD_AUDIO);
            return;
        }
        new SetupTask(this).execute();

        while (setupComplete == false) {
        }
        recognizer.startListening(MENU_SEARCH);
    }

    /* Permissions request taken directly from pocketSphinx's demo app */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions
            , @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST_RECORD_AUDIO) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                new SetupTask(this).execute();
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
        swapSearch(TERAGRAM_SEARCH);
        //startActivity(intent);
    }

    public void openG3(View view){
        Intent intent = new Intent(this, Game3Activity.class);
        startActivity(intent);
    }

    public void openG4(View view){
        Intent intent = new Intent(this, Game4Activity.class);
        startActivity(intent);
    }

    public String getResult() {
        return result;
    }

    private void setupRecognizer(File assetsDir) throws IOException {

        recognizer = SpeechRecognizerSetup.defaultSetup()
                .setAcousticModel(new File(assetsDir, "cmusphinx-en-us-ptm-5.2"))
                .setDictionary(new File(assetsDir, "nktd.dic"))
                .getRecognizer();
        recognizer.addListener(this);

        File menuGrammar = new File(assetsDir, "menu.gram");
        File teragramGrammar = new File(assetsDir, "teragram.gram");
        File numberGrammar = new File(assetsDir, "number.gram");
        recognizer.addGrammarSearch(MENU_SEARCH, menuGrammar);
        recognizer.addGrammarSearch(TERAGRAM_SEARCH, teragramGrammar);
        recognizer.addGrammarSearch(NUMBER_SEARCH, numberGrammar);

        this.setupComplete = true;
    }

    private void swapSearch(String newSearch) {
        recognizer.startListening(newSearch);
    }

    @Override
    public void onBeginningOfSpeech() {
        ((TextView) findViewById(R.id.resultText)).setText("say something");
    }

    @Override
    public void onEndOfSpeech() {
    }

    @Override
    public void onPartialResult(Hypothesis hypothesis) {
        if (hypothesis != null) {
            String searchName = recognizer.getSearchName();
            recognizer.stop();
            result = hypothesis.getHypstr();
            ((TextView) findViewById(R.id.resultText)).setText(result);
            if (result.equals("game two")) {
                openG2(null);
            } else if (result.equals("number")) {
                swapSearch(NUMBER_SEARCH);
            } else {
                recognizer.startListening(searchName);
            }
        }
    }

    @Override
    public void onResult(Hypothesis hypothesis) {
        if (hypothesis != null) {
            result = hypothesis.getHypstr();
            ((TextView) findViewById(R.id.resultText)).setText(result);
        }
    }

    @Override
    public void onError(Exception e) {

    }

    @Override
    public void onTimeout() {

    }

    /* Setup class taken directly from pocketSphinx's demo app */
    private static class SetupTask extends AsyncTask<Void, Void, Exception> {
        WeakReference<MainActivity> activityReference;

        SetupTask(MainActivity activity) {
            this.activityReference = new WeakReference<>(activity);
        }

        @Override
        protected Exception doInBackground(Void... params) {
            try {
                Assets assets = new Assets(activityReference.get());
                File assetDir = assets.syncAssets();
                activityReference.get().setupRecognizer(assetDir);
            } catch (IOException e) {
                Log.d("ex msg: ", e.getMessage());
                return e;
            }
            return null;
        }
    }

    public void setListener(SpeechResultListener listener) {
        this.listener = listener;
    }
}
