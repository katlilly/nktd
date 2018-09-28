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

/**
 * Service that runs alongside RecognizedActivities to provide speech interpretation.
 */
public class Recognizer extends Service implements RecognitionListener {

    /**
     * An object that provides a binding for a RecognizedActivity.
     */
    public class RecognizerBinder extends Binder {
        /**
         * Gets the instance of the running activity.
         * @return this service.
         */
        public Recognizer getService() {
            return Recognizer.this;
        }
    }

    private SpeechRecognizer interpreter;
    private RecognizerBinder binder = new RecognizerBinder();
    private WeakReference<Service> reference = new WeakReference<>((Service)this);

    /* Search names */
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

    /* Upon starting this service, set up recognizer to interpret for MainActivity */
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
        return true;
    }

    @Override
    public void onBeginningOfSpeech() {
        listener.onSoundHeard();
    }

    @Override
    public void onEndOfSpeech() {}

    @Override
    public void onResult(Hypothesis hypothesis){}

    private static String[] finishedCommands = {"about frozen bubble", "addition", "back", "clear", "cancel", "colorblind", "continue", "down",
    "don't rush me", "easier", "eight", "enter", "exit", "fire", "five", "four", "frozen bubble", "full screen", "game one", "game two", "game three",
            "game four", "harder", "help me", "left", "multiplication", "new game", "new question", "nine",
            "now", "number", "okay", "one", "powers of two", "right", "rush me", "seven", "six", "subtraction",
            "tear a gram", "three", "times tables", "twenty forty eight", "two", "up", "zero", "blue", "green", "red", "pink"};

    private int repetitionCount = 3;
    private String previousResult = "";
    @Override
    public void onPartialResult(Hypothesis hypothesis) {

        if (hypothesis != null) {
            result = hypothesis.getHypstr();
            /* Stop interpreting if we find three identical results in a row without finding a
            finished command */
            if(previousResult.equals(result)) {
                repetitionCount--;
            }
            if(repetitionCount <= 0) {
                interpreter.stop();
                previousResult = "";
                repetitionCount = 3;
                listener.onFailedRecognition();
            }
            previousResult = result;

            if (Arrays.asList(finishedCommands).contains(result)) {
                interpreter.stop();
            }

            Log.d("status", "Heard " + result);

            interpreter.startListening(currentSearch);

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

    /**
     * Set a listener to talk to.
     * @param listener listener handed from a RecognizedActivity.
     */
    public void setListener(SpeechResultListener listener) {
        this.listener = listener;
    }

    /**
     * Get the interpreters result.
     * @return what the interpreter heard.
     */
    public String getResult() {
        return result;
    }

    /**
     * Get the search mode of the interpreter.
     * @return The search name of the interpreter.
     */
    public String getSearchName() {
        return interpreter.getSearchName();
    }

    /**
     * Stop interpreter
     */
    public void stopRecognition() {
        this.interpreter.stop();
        listening = false;
        listener.onStopRecognition();
    }

    /**
     * Start interpreter.
     */
    public void startRecognition() {
        this.interpreter.startListening(currentSearch);
        listening = true;
        listener.onStartRecognition();
    }

    /**
     * Is the interpreter listening or not?
     * @return true if the interpreter is listening, false otherwise.
     */
    public boolean isListening() {
        return listening;
    }

    /**
     * Sets model, grammar files and dictionary of the interpreter.
     * @param assetsDir the directory the model, grammar files and dictionary are to be found.
     * @throws IOException if a requested file isn't found.
     */
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

    /**
     * Swaps between search modes.
     * @param newSearch The search name to switch to.
     */
    public void swapSearch(String newSearch) {
        Log.d("swapping", newSearch);
        currentSearch = newSearch;
        if(listening) {
            interpreter.stop();
            interpreter.startListening(currentSearch);
        }
    }

}
