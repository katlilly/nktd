package display;

import graphical.BoardPanel;
import graphical.TileType;
import java.awt.Graphics;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author ddavidso
 */
public class TestBoardPanel {

    BoardPanel boardPanel;
    
    public TestBoardPanel() {
    }

    @Before
    public void setUp() {        
        boardPanel = new BoardPanel(null);
    }

    @After
    public void tearDown() {
    }
    
    @Test
    public void blockingDetection() {
        assertTrue("Detecting a piece that isn't there"
                , boardPanel.isValidAndEmpty(TileType.TypeO, 0, 0, 0));
        boardPanel.addPiece(TileType.TypeO, 0, 0, 0);
        assertFalse("New piece not blocked"
                , boardPanel.isValidAndEmpty(TileType.TypeO, 0, 0, 0));    
    }
    
    @Test
    public void checkLines() {
        assertEquals("There should be 0 lines to clear", 0, boardPanel.checkLines());
        boardPanel.addPiece(TileType.TypeO, 0, 0, 0);
        boardPanel.addPiece(TileType.TypeO, 2, 0, 0);
        boardPanel.addPiece(TileType.TypeO, 4, 0, 0);
        boardPanel.addPiece(TileType.TypeO, 6, 0, 0);
        boardPanel.addPiece(TileType.TypeO, 8, 0, 0);
        assertEquals("There should be 2 lines to clear", 2, boardPanel.checkLines());  
    }
    


}