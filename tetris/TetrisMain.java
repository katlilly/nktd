import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferStrategy;

import javax.swing.JFrame;


public class TetrisMain extends Canvas implements Runnable, KeyListener{

    public static final int WIDTH = 400, HEIGHT = 540;
    
    public static void main(String[] args) {

	JFrame frame = new JFrame("Tetris");
	frame.setSize(WIDTH, HEIGHT);
	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	frame.setLocationRelativeTo(null);
	frame.setResizable(false);
	TetrisMain tm = new TetrisMain();
	frame.add(tm);
	frame.setVisible(true);
	tm.start();
	//System.out.println("hello");

    }


    public void start() {
	Thread t = new Thread(this);
	t.setPriority(Thread.MAX_PRIORITY);
	t.start();
    }
    
    public void run() {
	boolean running = true;
	while (running) {
	    update();
	    BufferStrategy buf = getBufferStrategy();
	    if (buf==null) {
		createBufferStrategy(3);
		continue;
	    }
	    Graphics2D g = (Graphics2D) buf.getDrawGraphics();
	    render(g);
	    buf.show();
	}
    }


    public void update() {

    }

    public void render(Graphics2D g) {
	g.setColor(Color.BLACK);
	g.fillRect(0,0,WIDTH,HEIGHT);
	g.setColor(Color.WHITE);
	g.setFont(new Font("Calibri", Font.PLAIN, 20));
	g.drawString("Tetris", 150, 50);
    }
    
    public void keyPressed(KeyEvent e) {

    }

    public void keyTyped(KeyEvent e) {

    }

    public void keyReleased(KeyEvent e) {

    }
    
}
