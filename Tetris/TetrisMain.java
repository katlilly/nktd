package Tetris;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferStrategy;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

public class TetrisMain extends Canvas implements Runnable{

    
    public static final int WIDTH = 400, HEIGHT = 565;
    private Image[] tetrisBlocks;
    Controller control;
    
    public static void main(String[] args) {
       final JFrame frame = new JFrame("Tetris");
        frame.setSize(WIDTH, HEIGHT);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setLayout(null);
        
        KeyGetter.loadKeys();
        try{
            Config.loadConfig();
        
        }catch(Exception e)
        {
            e.printStackTrace();
        
        }
        JMenuBar bar = new JMenuBar();
        bar.setBounds(0,0, WIDTH, 25);
        
        JMenu file = new JMenu("File");
        file.setBounds(0,0,45,24);
        
        JMenuItem newGame = new JMenuItem("New Game");
        newGame.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e ){
            //Code for new game
                System.out.println("Starting new Game");
            }
    });
        JMenuItem highScore = new JMenuItem("HighScore");
        highScore.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e ){
            //Get HighScore
                int highscore = 0; //Replace with high Score
                final JFrame alert = new JFrame("High Score");
                alert.setSize(200,150);
                alert.setLayout(null);
                alert.setLocationRelativeTo(null);
                //alert.setAlwaysOnTop(true);
                
                
                JLabel score = new JLabel ("The High Score is :" + highscore);
                score.setBounds (0,0,200,50);
                
                JButton okayButton = new JButton("Okay");
                okayButton.setBounds(50,80,100,30);
                okayButton.addActionListener(new ActionListener(){
                    public void actionPerformed(ActionEvent e){
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
        exit.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e ){
            //Code for new game
                System.out.println("Closing...");
                System.exit(0);
            }
    });
        
        JMenuItem options = new JMenuItem ("Options");
        options.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                Config.openConfig(frame);
            
            }
        
        });
        TetrisMain tm = new TetrisMain();
        tm.setBounds(0,25 , WIDTH, HEIGHT -25 );
        
        frame.add(tm);
        file.add(newGame);
        file.add(highScore);
        file.add(options);
        file.add(exit);
        bar.add(file);
        frame.add(bar);
        frame.setVisible(true);
          tm.start();
          
    }
    public void start(){
        Thread t = new Thread(this);
        t.setPriority(Thread.MAX_PRIORITY);
        t.start();
    }
    
    public void run(){
    	init();
        boolean running = true;
        while(running){
            update();
            BufferStrategy buf = getBufferStrategy();
            if (buf == null ){
                createBufferStrategy(3);
                continue;
            }
            Graphics2D g = (Graphics2D) buf.getDrawGraphics();
            render(g);
            buf.show();
        }
    }
    
    public void init(){
    	control = new Controller(this);
    	this.addKeyListener(control);
    	requestFocus();
    	try{
    		tetrisBlocks = ImageLoader.loadImage("/tetris.png", 25);
    	}
    	catch(IOException e){
    		System.out.println("Error loading in tetris.png");
    		System.exit(1);
    	}
    }
    public void update(){
    	System.out.println(control.left + " : " + control.right + " : " + control.down + " : " + control.rotate + " : " + control.pause);
    }
    public void render(Graphics2D g){
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, WIDTH, HEIGHT);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Calibri", Font.PLAIN, 20));
        g.drawString("Tetris", 170, 50);
        g.drawImage(tetrisBlocks[6], 100, 100, 25, 25, null);
    }
    
}
 
