package input;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

/**
 *
 * @author ddavidso
 */
public class SoundRecorder {

    File wavFile;
    AudioFileFormat.Type fileType;
    TargetDataLine line;
    
    public SoundRecorder(String fileName) throws FileNotFoundException {
        wavFile = new File("../speech_control/src/main/resources/audio/" + fileName + ".wav");
        fileType = AudioFileFormat.Type.WAVE;        
    }
    
    private AudioFormat makeAudioFormat() {
        int sampleRate = 16000;
        int sampleSizeInBits = 8;
        int channels = 1;
        boolean signed = true;
        boolean bigEndian = true;
        return new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);
    }
    
    public void record() {
        
        int VOLUME_THRESHOLD = 5;
    
        try {
            AudioFormat format = makeAudioFormat();
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
            
            line = (TargetDataLine)AudioSystem.getLine(info);
            line.open(format);
            line.start();
            AudioInputStream input = new AudioInputStream(line);
            AudioSystem.write(input, fileType, wavFile);
            Thread.sleep(500);

            line.stop();
            line.close();
        } catch(LineUnavailableException ex) {
            System.out.println(ex.getMessage());
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        } catch (InterruptedException ex) {
            System.out.println(ex.getMessage());
        }
    
    }
    
}
