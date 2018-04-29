/**
 *
 * @author ddavidso
 */

import edu.cmu.sphinx.api.Configuration;
import edu.cmu.sphinx.api.LiveSpeechRecognizer;

public class Main {
    
    private static void recognizeCommand(LiveSpeechRecognizer recognizer){
        System.out.println("Go.");
        recognizer.startRecognition(true);
        while (true) {
            String utterance = recognizer.getResult().getHypothesis();
            if (utterance.equals("one zero one") || utterance.equals("one oh one"))
                break;
            else
                System.out.println(utterance);
        }
    }
    
    public static void main(String[] args) throws Exception {
        
        Configuration configuration = new Configuration();
                
        configuration.setAcousticModelPath("resource:/edu/cmu/sphinx/models/en-us/en-us");
        configuration.setDictionaryPath("resource:/edu/cmu/sphinx/models/en-us/cmudict-en-us.dict");
        configuration.setLanguageModelPath("resource:/edu/cmu/sphinx/models/en-us/en-us.lm.bin");
        configuration.setGrammarPath("src/main/resources/grammars/");
        configuration.setUseGrammar(true);
        configuration.setGrammarName("tetrisCommand");
        
        LiveSpeechRecognizer recognizer = new LiveSpeechRecognizer(configuration);
        
        recognizeCommand(recognizer);
        
    }
    
}
