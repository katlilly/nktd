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
            if (utterance.equals("right seven"))
                break;
            else
                System.out.println(utterance);
        }
    }
    
    public static void main(String[] args) throws Exception {
        
        Configuration configuration = new Configuration();
                
        configuration.setAcousticModelPath("src/main/resources/cmusphinx-en-us-ptm-5.2");
        configuration.setDictionaryPath("src/main/resources/1922.dic");
        configuration.setLanguageModelPath("src/main/resources/1922.lm");
        configuration.setGrammarPath("src/main/resources/grammars/");
        configuration.setUseGrammar(true);
        configuration.setGrammarName("tetrisCommand");
        
        LiveSpeechRecognizer recognizer = new LiveSpeechRecognizer(configuration);
        
        recognizeCommand(recognizer);
        
    }
    
}
