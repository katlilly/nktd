package nktd;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.io.File;
import java.io.IOException;

import app.R;
import edu.cmu.pocketsphinx.Assets;
import edu.cmu.pocketsphinx.Hypothesis;
import edu.cmu.pocketsphinx.RecognitionListener;
import edu.cmu.pocketsphinx.SpeechRecognizer;
import edu.cmu.pocketsphinx.SpeechRecognizerSetup;

public class TetrisActivity extends AppCompatActivity implements RecognitionListener{

    SpeechRecognizer recognizer;
    String result;
    boolean resultAchieved = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tetris);
        setupRecognizer();
    }

    @Override
    protected void onStart(){
        super.onStart();
        while(recognizer != null) {
            Log.d("here", "we are");
            Log.d("result", interpretCommand(recognizer));
        }
    }

    public void setupRecognizer(){
        File assetsDir;
        try {
            Assets assets = new Assets(this);
            assetsDir = assets.syncAssets();
            Log.d("here","here");
            this.recognizer = SpeechRecognizerSetup.defaultSetup()
                    .setAcousticModel(new File(assetsDir
                            , "cmusphinx-en-us-ptm-5.2"))
                    .setDictionary(new File(assetsDir
                            , "tetris-dictionary.dic"))
                    .getRecognizer();
            File grammarFile = new File(assetsDir, "tetris-grammar.gram");
            recognizer.addGrammarSearch("tetris", grammarFile);
        } catch (IOException ex) {
            Log.e("AssetInit", ex.getMessage());
        }
    }

    public String interpretCommand(SpeechRecognizer recognizer){
        this.result = null;
        this.resultAchieved = false;
        recognizer.startListening("tetris", 10000);
        while(this.resultAchieved == false) {

        }
        return this.result;
    }

    @Override
    public void onBeginningOfSpeech() {

    }

    @Override
    public void onEndOfSpeech() {

    }

    @Override
    public void onPartialResult(Hypothesis hypothesis) {

    }

    @Override
    public void onResult(Hypothesis hypothesis){
        if (hypothesis == null) {
            return;
        }
        this.result = hypothesis.getHypstr();
        this.resultAchieved = true;
    }

    @Override
    public void onError(Exception e) {

    }

    @Override
    public void onTimeout() {

    }
}
