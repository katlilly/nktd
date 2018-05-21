package com.example.nathan.nktd.speech_recognition;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.io.File;
import java.io.IOException;

import edu.cmu.pocketsphinx.Assets;
import edu.cmu.pocketsphinx.Hypothesis;
import edu.cmu.pocketsphinx.RecognitionListener;
import edu.cmu.pocketsphinx.SpeechRecognizer;
import edu.cmu.pocketsphinx.SpeechRecognizerSetup;

public class Recognizer extends Service implements RecognitionListener {

    public class RecognizerBinder extends Binder {
        public Recognizer getService() {
            return Recognizer.this;
        }
    }

    public static final String TETRIS_NAME = "tetris-command";

    private SpeechRecognizer recognizer;
    private String gameName;
    private boolean resultAchieved;
    private String result;
    private Context parent;

    public Recognizer(){}

    /*public void setupRecognizer(){
        File assetsDir;
        try {
            Assets assets = new Assets();
            assetsDir = assets.syncAssets();
            recognizer = SpeechRecognizerSetup.defaultSetup()
                    .setAcousticModel(new File(assetsDir
                            , "cmusphinx-en-us-ptm-5.2"))
                    .setDictionary(new File(assetsDir
                            , "tetris-dictionary.dic"))
                    .getRecognizer();
            File grammarFile = new File(assetsDir, "tetris-grammar.gram");
            recognizer.addGrammarSearch(TETRIS_NAME, grammarFile);
        } catch (IOException ex) {
            Log.e("AssetInit", "failed");
        }
    }*/

    public String interpretCommand(SpeechRecognizer recognizer){
        this.result = null;
        this.resultAchieved = false;
        recognizer.startListening(gameName, 10000);
        while(this.resultAchieved == false) {

        }
        return this.result;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        this.gameName = intent.getAction();
        return START_NOT_STICKY;
    }

    private final IBinder binder = new RecognizerBinder();
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        if (recognizer != null) {
            recognizer.cancel();
            recognizer.shutdown();
        }
        this.stopSelf();
        return false;
    }

    @Override
    public void onBeginningOfSpeech() {}

    @Override
    public void onEndOfSpeech() {}

    @Override
    public void onResult(Hypothesis hypothesis){
        if (hypothesis == null) {
            this.resultAchieved = true;
            return;
        }
        this.result = hypothesis.getHypstr();
        this.resultAchieved = true;
    }

    @Override
    public void onPartialResult(Hypothesis hypothesis) {}

    @Override
    public void onTimeout() {}

    @Override
    public void onError(Exception ex) {}

}
