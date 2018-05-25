package com.example.nathan.nktd;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.example.nathan.nktd.interfaces.SpeechResultListener;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;

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

    private SpeechRecognizer interpreter;
    private RecognizerBinder binder = new RecognizerBinder();

    public static final String MENU_SEARCH = "menu";
    public static final String TERAGRAM_SEARCH = "teragram";
    public static final String NUMBER_SEARCH = "number";

    private SpeechResultListener listener;

    public boolean setupComplete = false;

    private String result = "";

    public Recognizer(){}

    @Override
    public void onCreate() {
        new SetupTask(this).execute();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        String searchName = intent.getAction();
        while (!setupComplete) {
        }
        interpreter.startListening(searchName);
        return Service.START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return true;
    }

    public String test() {
        return "We did it";
    }

    @Override
    public void onBeginningOfSpeech() {
        Log.d("status", "beginning of speech");
    }

    @Override
    public void onEndOfSpeech() {}

    @Override
    public void onResult(Hypothesis hypothesis){}

    @Override
    public void onPartialResult(Hypothesis hypothesis) {
        Log.d("status", "onpartialresult");
        if (hypothesis != null) {
            Log.d("status", "onPartialResult");
            String searchName = interpreter.getSearchName();
            interpreter.stop();
            result = hypothesis.getHypstr();
            if (result.equals("game two")) {
                swapSearch(TERAGRAM_SEARCH);
            } else if (result.equals("number")) {
                swapSearch(NUMBER_SEARCH);
            } else {
                interpreter.startListening(searchName);
            }
            Log.d("status", "partial result");
            if (listener != null) {
                Log.d("status", "recognizer onspeechresult");
                listener.onSpeechResult();
            }
        }
    }

    @Override
    public void onTimeout() {}

    @Override
    public void onError(Exception ex) {}

    public void setListener(SpeechResultListener listener) {
        this.listener = listener;
    }

    public String getResult() {
        return result;
    }

    private void setupRecognizer(File assetsDir) throws IOException {

        interpreter = SpeechRecognizerSetup.defaultSetup()
                .setAcousticModel(new File(assetsDir, "cmusphinx-en-us-ptm-5.2"))
                .setDictionary(new File(assetsDir, "nktd.dic"))
                .getRecognizer();
        interpreter.addListener(this);

        File menuGrammar = new File(assetsDir, "menu.gram");
        File teragramGrammar = new File(assetsDir, "teragram.gram");
        File numberGrammar = new File(assetsDir, "number.gram");
        interpreter.addGrammarSearch(MENU_SEARCH, menuGrammar);
        interpreter.addGrammarSearch(TERAGRAM_SEARCH, teragramGrammar);
        interpreter.addGrammarSearch(NUMBER_SEARCH, numberGrammar);

        this.setupComplete = true;
    }

    private void swapSearch(String newSearch) {
        interpreter.startListening(newSearch);
    }

    /* Setup class taken directly from pocketSphinx's demo app */
    private static class SetupTask extends AsyncTask<Void, Void, Exception> {
        WeakReference<Recognizer> serviceReference;

        SetupTask(Recognizer service) {
            this.serviceReference = new WeakReference<>(service);
        }

        @Override
        protected Exception doInBackground(Void... params) {
            try {
                Assets assets = new Assets(serviceReference.get());
                File assetDir = assets.syncAssets();
                serviceReference.get().setupRecognizer(assetDir);
            } catch (IOException e) {
                Log.d("ex msg: ", e.getMessage());
                return e;
            }
            return null;
        }
    }
}
