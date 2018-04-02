package Tetris;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.image.BufferStrategy;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JLabel;



public class TetrisMain extends Canvas implements Runnable, KeyListener{
  
  public static final int WIDTH = 400, HEIGHT = 540;
  
  public static void main(String[] args) {
    
    JFrame frame = new JFrame("Tetris");
    frame.setSize(WIDTH, HEIGHT);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setLocationRelativeTo(null);
    frame.setResizable(false);
    frame.setLayout(null);
    
    JMenuBar bar = new JMenuBar();
    bar.setBounds(0, 0, WIDTH - 1, 25);
    
    JMenu menu = new JMenu("Menu");
    menu.setBounds(0, 0, 45, 24);
    
    JMenuItem newGame = new JMenuItem("New Game");
    newGame.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        // code for new game
        System.out.println("Starting New Game...");
      }
    });
    
    JMenuItem highScore = new JMenuItem("Highscore");
    highScore.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        int highscore = 0; // replace this with getHighScore method later
        final JFrame alert = new JFrame("High Score");
        alert.setSize(200, 150);
        alert.setLayout(null);
        alert.setLocationRelativeTo(null);
        
        JLabel score = new JLabel("The highscore is: " + highscore);
        score.setBounds(10, 0, 200, 50);
        // set locations of these things relatively rather than absolutely?
        // how to deal with different sized screens?
        
        JButton okayButton = new JButton("Okay");
        okayButton.setBounds(50, 80, 100, 30);
        okayButton.addActionListener(new ActionListener(){
          public void actionPerformed(ActionEvent e) {
            alert.dispose();
          }
        });
        alert.add(score);
        alert.add(okayButton); 
        alert.setResizable(false);
        alert.setVisible(true);
        
      }
    });
    
    JMenuItem exit = new JMenuItem("Exit");
    newGame.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        System.out.println("Closing...");
        System.exit(0);
      }
    });
    
    TetrisMain tm = new TetrisMain();
    tm.setBounds(0, 25, WIDTH, HEIGHT - 25);

    frame.add(tm);
    menu.add(newGame);
    menu.add(highScore);
    menu.add(exit);
    bar.add(menu);
    frame.add(bar);
    frame.setVisible(true);
    tm.start();
    
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
