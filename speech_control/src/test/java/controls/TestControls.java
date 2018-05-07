package controls;

import java.awt.Graphics;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/** @author katlilly */


public class TestControls {

    Tetris game;
    SpeechController sc;
    InputStream audioFile;
    
    public TestControls() {
    }

    @Before
    public void setUp() {
	game = new Tetris();
	game.startGame();
	// don't do above, just test controls for now, later test in game?

	// testing recorded input file version for now
	// audioFile = new InputStreamReader('filename.wav');
	// sc = new SpeechController(game, audioFile);
	// above is not right, first parameter should be a string


	
    }

    @After
    public void tearDown() {

    }


    @Test
    public void startsRecording() {
	
    }
    
    
    @Test
    public void acceptsMovementCommand() {
	
    }


    @Test
    public void acceptsPauseCommand() {
	
	
    }

}
    

