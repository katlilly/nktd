package SpeechControl.src.main.java.controller;

import edu.cmu.sphinx.api.AbstractSpeechRecognizer;
import edu.cmu.sphinx.api.Configuration;
import edu.cmu.sphinx.api.LiveSpeechRecognizer;
import edu.cmu.sphinx.api.StreamSpeechRecognizer;
import edu.cmu.sphinx.result.WordResult;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;


public class SpeechController {
    
    private final String gameName;
    private final AbstractSpeechRecognizer recognizer;
    private final InputStream audioFile;
    
    
    public SpeechController(String gameName) throws IOException{
        this.gameName = gameName;
        this.recognizer = new LiveSpeechRecognizer(makeConfig(gameName));
        this.audioFile = null;
    }
    
    
    public SpeechController(String gameName, InputStream audioFile)
            throws IOException{
        this.gameName = gameName;
        this.recognizer = new StreamSpeechRecognizer(makeConfig(gameName));
        this.audioFile = audioFile;
    }
    
    
    private Configuration makeConfig(String gameName){
        Configuration configuration = new Configuration();
        configuration.setAcousticModelPath(
                "src/main/resources/cmusphinx-en-us-ptm-5.2");
        configuration.setDictionaryPath("src/main/resources/dictionaries/" + 
                gameName + "-dictionary.dic");
        configuration.setGrammarPath("src/main/resources/grammars/");
        configuration.setUseGrammar(true);
        configuration.setGrammarName(gameName + "-grammar");
        configuration.setSampleRate(8000);
        return configuration;
    }
    
    private String wordResultToString(List<WordResult> words){
        StringBuilder result = new StringBuilder();
        for(WordResult w: words){
            result.append(w.getWord().toString() + " ");
        }
        return result.toString();  
    }
    
    
    public String recognizeCommand(StreamSpeechRecognizer recognizer){
        System.out.println("Interpreting file");
        recognizer.startRecognition(audioFile);
        List<WordResult> words = recognizer.getResult().getWords();
        recognizer.stopRecognition();
        return wordResultToString(words);
    }
    
    
    public String recognizeCommand(LiveSpeechRecognizer recognizer){
        System.out.println("Say Something");
        recognizer.startRecognition(true);
        List<WordResult> words = recognizer.getResult().getWords();
        recognizer.stopRecognition();
        return wordResultToString(words);
    }
    
    
    private static void testLiveSpeechRecognizer(String gameName) throws IOException{
        SpeechController controller = new SpeechController(gameName);
        String exitPhrase = "right seven ";
        String utterance = null;
        while(!utterance.equals(exitPhrase)){
            utterance = controller.recognizeCommand(
                    (LiveSpeechRecognizer)controller.recognizer);
            System.out.println(utterance);
        }
    }
    
    private static void testStreamSpeechRecognizer(String gameName, String audioFileName) throws IOException{
        FileInputStream audioFile = new FileInputStream
                        ("src/main/resources/audio/" + audioFileName);
        SpeechController controller = new SpeechController(gameName, audioFile);
        String utterance = controller.recognizeCommand(
                (StreamSpeechRecognizer)controller.recognizer);
        System.out.println(utterance);
    }
    
    
    public static void main(String[] args) throws IOException{
        testStreamSpeechRecognizer("tetris", "left-one.wav");
        //testLiveSpeechRecognizer("tetris");
    }
    
}
