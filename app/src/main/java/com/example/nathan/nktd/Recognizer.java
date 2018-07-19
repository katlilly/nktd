package com.example.nathan.nktd;

import android.app.Service;
import android.content.Intent;
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
        public boolean isSetUp() {return setupComplete;}
    }

    private SpeechRecognizer interpreter;
    private RecognizerBinder binder = new RecognizerBinder();
    private WeakReference<Service> reference = new WeakReference<>((Service)this);

    public static final String MENU_SEARCH = "menu";
    public static final String TERAGRAM_SEARCH = "teragram";
    public static final String NUMBER_SEARCH = "number";

    private SpeechResultListener listener;

    public boolean setupComplete = false;

    private String result = "";

    public Recognizer(){}

    /* Copy files to phone's memory if necessary. */
    @Override
    public void onCreate() {
        try {
            Assets assets = new Assets(reference.get());
            File assetDir = assets.syncAssets();
            setupRecognizer(assetDir);
            Log.d("Status", "setup complete");
        } catch (IOException e) {
            Log.d("ex msg: ", e.getMessage());
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        while(!setupComplete){}
        interpreter.startListening(MENU_SEARCH);
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

    @Override
    public void onBeginningOfSpeech() {
    }

    @Override
    public void onEndOfSpeech() {}

    @Override
    public void onResult(Hypothesis hypothesis){}

    @Override
    public void onPartialResult(Hypothesis hypothesis) {
        if (hypothesis != null) {
            String searchName = interpreter.getSearchName();
            interpreter.stop();
            result = hypothesis.getHypstr();
            Log.d("status", "Heard " + result);
            /* Handle switching between searches here. */
            switch (searchName) {
                case MENU_SEARCH:
                    if (result.equals("game two")) {
                        swapSearch(TERAGRAM_SEARCH);
                    } else {
                        interpreter.startListening(searchName);
                    }
                    break;

                case TERAGRAM_SEARCH:
                    if (result.equals("number")) {
                        swapSearch(NUMBER_SEARCH);
                        listener.onNumberRecognition();
                    } else if (result.equals("exit")) {
                        swapSearch(MENU_SEARCH);
                    } else {
                        interpreter.startListening(searchName);
                    }
                    break;

                case NUMBER_SEARCH:
                    if (result.equals("enter") || result.equals("cancel")) {
                        swapSearch(TERAGRAM_SEARCH);
                    } else {
                        interpreter.startListening(searchName);
                    }
                    break;
            }

            /* Let all listeners know there's a result. */
            if (listener != null) {
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

    public String getSearchName() {
        return interpreter.getSearchName();
    }

    private String lastSearch = "";
    public void stopRecognition() {
        Log.d("stopstart", "stopping");
        lastSearch = interpreter.getSearchName();
        this.interpreter.stop();
        listener.onStopRecognition();
    }

    public void startRecognition() {
        Log.d("stopstart", "starting");
        this.interpreter.startListening(lastSearch);
        listener.onStartRecognition();
    }

    /* Set model, dictionary and grammars for interpreter. */
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

    public void swapSearch(String newSearch) {
        interpreter.startListening(newSearch);
    }

}
