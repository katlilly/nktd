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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    private SpeechResultListener listener;

    public boolean setupComplete = false;
    private boolean listening = false;

    private String gameName;
    private String result = "";
    private String initSearch;

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
        Log.d("status", "onStartCommand");
        initSearch = intent.getStringExtra("searchName");
        interpreter.startListening(initSearch);
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
        return true;
    }

    @Override
    public void onBeginningOfSpeech() {
    }

    @Override
    public void onEndOfSpeech() {}

    @Override
    public void onResult(Hypothesis hypothesis){}

    private static List<String> finishedCommands = new ArrayList();
    private static String[] commands = {"addition", "back", "clear", "cancel", "down",
    "easier", "eight", "enter", "exit", "five", "four", "game one", "game two", "game three",
            "game four", "harder",
    "left", "multiplication", "new question", "nine", "number", "one", "right", "seven",
    "six", "subtraction", "tear a gram", "three", "twenty forty eight", "two", "up",
    "zero"};

    private int repetitionCount = 0;
    private String previousResult = "";
    @Override
    public void onPartialResult(Hypothesis hypothesis) {

        if (hypothesis != null) {
            String searchName = interpreter.getSearchName();
            result = hypothesis.getHypstr();
            if(previousResult.equals(result)) {
                repetitionCount++;
            }
            if(repetitionCount > 5) {
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
            switch (searchName) {
                case MENU_SEARCH:
                    if (result.equals("game two") || result.equals("tear a gram")) {
                        swapSearch(TERAGRAM_SEARCH);
                    } else if(result.equals("game three") || result.equals("twenty forty eight")) {
                        swapSearch(TWENTY_FORTY_EIGHT_SEARCH);
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
                case TWENTY_FORTY_EIGHT_SEARCH:
                    interpreter.startListening(searchName);
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

    public void stopRecognition() {
        Log.d("stopstart", "stopping");
        this.interpreter.stop();
        listening = false;
        listener.onStopRecognition();
    }

    public void startRecognition(String searchName) {
        Log.d("stopstart", "starting");
        this.interpreter.startListening(searchName);
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
        interpreter.addGrammarSearch(MENU_SEARCH, menuGrammar);
        interpreter.addGrammarSearch(TERAGRAM_SEARCH, teragramGrammar);
        interpreter.addGrammarSearch(NUMBER_SEARCH, numberGrammar);
        interpreter.addGrammarSearch(TWENTY_FORTY_EIGHT_SEARCH, twentyFortyEightGrammar);

        this.setupComplete = true;
    }

    public void swapSearch(String newSearch) {
        Log.d("swapping", newSearch);
        if(listening) {
            interpreter.stop();
        }
        interpreter.startListening(newSearch);
    }

}
