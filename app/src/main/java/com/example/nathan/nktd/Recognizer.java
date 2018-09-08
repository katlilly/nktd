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
import java.util.Arrays;

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
    private WeakReference<Service> reference = new WeakReference<>((Service)this);

    public static final String MENU_SEARCH = "menu";
    public static final String TERAGRAM_SEARCH = "teragram";
    public static final String NUMBER_SEARCH = "number";
    public static final String TWENTY_FORTY_EIGHT_SEARCH = "2048";
    public static final String YESNO_SEARCH = "yesno";
    public static final String FROZENBUBBLE_SEARCH = "frozenbubble";
    public static final String POWERS_OF_TWO_SEARCH = "powersoftwo";
    public static final String HELP_SEARCH = "help";

    private SpeechResultListener listener;

    public boolean setupComplete = false;
    private boolean listening = false;

    private String result = "";
    private String currentSearch;

    public Recognizer(){}

    /* Copy files to phone's memory if necessary. */
    @Override
    public void onCreate() {
        try {
            Assets assets = new Assets(reference.get());
            File assetDir = assets.syncAssets();
            setupRecognizer(assetDir);
        } catch (IOException e) {
            Log.d("ex msg: ", e.getMessage());
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        interpreter.startListening(Recognizer.MENU_SEARCH);
        currentSearch = MENU_SEARCH;
        listening = true;
        while(!setupComplete){}
        return Service.START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d("bind", "unbinding");
        return true;
    }

    @Override
    public void onBeginningOfSpeech() {
        Log.d("speech", "heard");
        listener.onSoundHeard();
    }

    @Override
    public void onEndOfSpeech() {}

    @Override
    public void onResult(Hypothesis hypothesis){}

    private static String[] commands = {"addition", "back", "clear", "cancel", "down",
    "easier", "eight", "enter", "exit", "fire", "five", "four", "game one", "game two", "game three",
            "game four", "harder", "help", "left", "multiplication", "new game", "new question", "nine",
            "now", "number", "okay", "one", "powers of two", "right", "seven", "six", "subtraction",
            "tear a gram", "three", "times tables", "twenty forty eight", "two", "up", "zero"};

    private int repetitionCount = 0;
    private String previousResult = "";
    @Override
    public void onPartialResult(Hypothesis hypothesis) {

        if (hypothesis != null) {
            result = hypothesis.getHypstr();
            if(previousResult.equals(result)) {
                repetitionCount++;
            }
            if(repetitionCount >= 3) {
                Log.d("status", "cancel");
                interpreter.stop();
                previousResult = "";
                repetitionCount = 0;
            }
            previousResult = result;
            if (Arrays.asList(commands).contains(result)) {
                interpreter.stop();
            }
            Log.d("status", "Heard " + result);
            /* Handle switching between searches here. */
            switch (currentSearch) {
                case TERAGRAM_SEARCH:
                    if (result.equals("number")) {
                        currentSearch = NUMBER_SEARCH;
                        listener.onNumberRecognition();
                    }
                    break;

                case NUMBER_SEARCH:
                    if (result.equals("okay") || result.equals("cancel")) {
                        currentSearch = TERAGRAM_SEARCH;
                        listener.onStartRecognition();
                    }
                    break;
            }
            interpreter.startListening(currentSearch);
            /* Let all listeners know there's a result. */
            if (listener != null) {
                listener.onSpeechResult();
            }
        }
    }

    @Override
    public void onTimeout() {
        listener.onFinishedRecognition();
    }

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

    public void stopRecognition() {
        this.interpreter.stop();
        listening = false;
        listener.onStopRecognition();
    }

    public void startRecognition() {
        this.interpreter.startListening(currentSearch);
        listening = true;
        listener.onStartRecognition();
    }

    public boolean isListening() {
        return listening;
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
        File twentyFortyEightGrammar = new File(assetsDir, "2048.gram");
        File yesNoGrammar = new File(assetsDir, "yesno.gram");
        File frozenbubbleGrammar = new File(assetsDir, "frozenbubble.gram");
        File powersOfTwoGrammar = new File(assetsDir, "powersoftwo.gram");
        File helpGrammar = new File(assetsDir, "help.gram");
        interpreter.addGrammarSearch(MENU_SEARCH, menuGrammar);
        interpreter.addGrammarSearch(TERAGRAM_SEARCH, teragramGrammar);
        interpreter.addGrammarSearch(NUMBER_SEARCH, numberGrammar);
        interpreter.addGrammarSearch(TWENTY_FORTY_EIGHT_SEARCH, twentyFortyEightGrammar);
        interpreter.addGrammarSearch(YESNO_SEARCH, yesNoGrammar);
        interpreter.addGrammarSearch(FROZENBUBBLE_SEARCH, frozenbubbleGrammar);
        interpreter.addGrammarSearch(POWERS_OF_TWO_SEARCH, powersOfTwoGrammar);
        interpreter.addGrammarSearch(HELP_SEARCH, helpGrammar);
        this.setupComplete = true;
    }

    public void swapSearch(String newSearch) {
        Log.d("swapping", newSearch);
        currentSearch = newSearch;
        if(listening) {
            interpreter.stop();
            interpreter.startListening(currentSearch);
        }
    }

}
