package Tetris;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Controller implements KeyListener{
	
	TetrisMain game;
	public boolean left, right, down, rotate, pause; 
	
	public Controller(TetrisMain game){
		this.game = game;
	}
	
    public void keyPressed(KeyEvent e){
    	if(KeyEvent.getKeyText(e.getKeyCode()).equals(Config.left)){
    		left = true;
    	}
    	else if(KeyEvent.getKeyText(e.getKeyCode()).equals(Config.right)){
    		right = true;
    	}
    	else if(KeyEvent.getKeyText(e.getKeyCode()).equals(Config.rotate)){
    		rotate = true;
    	}
    	else if(KeyEvent.getKeyText(e.getKeyCode()).equals(Config.down)){
    		down = true;
    	}
    	else if(KeyEvent.getKeyText(e.getKeyCode()).equals(Config.pause)){
    		pause = true;
    	}
    }
    
    public void keyTyped(KeyEvent e){
    	
    }
    
    public void keyReleased(KeyEvent e){
    	if(KeyEvent.getKeyText(e.getKeyCode()).equals(Config.left)){
    		left = false;
    	}
    	else if(KeyEvent.getKeyText(e.getKeyCode()).equals(Config.right)){
    		right = false;
    	}
    	else if(KeyEvent.getKeyText(e.getKeyCode()).equals(Config.rotate)){
    		rotate = false;
    	}
    	else if(KeyEvent.getKeyText(e.getKeyCode()).equals(Config.down)){
    		down = false;
    	}
    	else if(KeyEvent.getKeyText(e.getKeyCode()).equals(Config.pause)){
    		pause = false;
    	}
    }

}
