
package Tetris;

import java.awt.Choice;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;


public class Config {
    public static String rotate = "Up", left = "Left", right = "Right",
	down = "Down", pause = "P";
    private static ArrayList<Choice> choices;
    
    public static void openConfig(JFrame frame){
        choices = new ArrayList<Choice>();
        final JFrame options = new JFrame ("Options");
        options.setSize(400,300);
        options.setResizable(false);
        options.setLocationRelativeTo(frame);
        options.setLayout(null);
        Choice left = addChoice("Left", options, 30,30);
        left.select(Config.left);
        Choice right = addChoice("Right", options, 150,30);
        right.select(Config.right);
        Choice down = addChoice("Down", options, 30,80);
        down.select(Config.down);
        Choice rotate = addChoice("Rotate", options, 150,80);
        rotate.select(Config.rotate);
        Choice pause = addChoice("Pause", options, 30,130);
        pause.select(Config.pause);
        JButton done = new JButton("Done");
        done.setBounds(150,200,100,30);
        done.addActionListener(new ActionListener(){
        public void actionPerformed(ActionEvent e ){
            options.dispose();
            saveChanges();
    }
});
        options.add(done);
        options.setVisible(true);
        
    }
    public static void saveChanges(){
        Choice left = choices.get(0);
        Choice right = choices.get(1);
        Choice down = choices.get(2);
        Choice rotate = choices.get(3);
        Choice pause = choices.get(4);
        Config.left = left.getSelectedItem();
        Config.right = right.getSelectedItem();
        Config.down = down.getSelectedItem();
        Config.rotate = rotate.getSelectedItem();
        Config.pause = pause.getSelectedItem();
        
        try
        {
            saveConfig();
        }
        catch (Exception e ){
            e.printStackTrace();
        
        }
        
    }
    
    
    public static Choice addChoice(String name, JFrame options, int x, int y){
        JLabel label = new JLabel(name);
        label.setBounds(x,y-20 , 100 , 20);
        Choice key = new Choice();
        for (String s: getKeyNames()){
            key.add(s);
        
        }
        key.setBounds(x,y,100,20);
        options.add(key);
        options.add(label);
        choices.add(key);
        return key;
    
    }
    
    public static ArrayList<String> getKeyNames(){
        ArrayList<String> result = new ArrayList<String>();
        for (String s: KeyGetter.keyNames ){
            result.add(s);
            if(s.equalsIgnoreCase("F24")){
                break;
            
            }
        
        }
        return result;
    
    }
    
    public static void loadConfig() throws Exception{
        File directory = new File(getDefaultDirectory(),"/Tetris");
        if(!directory.exists()){
            directory.mkdirs();
        
        }
        //System.out.println(directory.getPath());
        File config = new File(directory, "/config.txt");
        if(!config.exists ()){
            
            config.createNewFile();
            System.out.println("File Not Found, saving defaults");
            saveConfig();
            return;
        }
        Scanner s = new Scanner(config);
        HashMap <String, String>values = new HashMap<String, String>();
       
        while(s.hasNextLine()){
            String[] entry = s.nextLine().split(":");
            String key = entry[0];
            String value = entry[1];
            values.put(key, value);
        }
            if(values.size() != 5){
                System.out.println("Config is unusable, saving defaults");
                saveConfig();
                return;
            
            }
            if(!values.containsKey("left") || !values.containsKey("right")|| !values.containsKey("rotate")|| !values.containsKey("down") || !values.containsKey("pause")){
                System.out.println("Invalid names in config saving defaults");
                saveConfig();
                return;
            }
            String left = values.get("left");
            String right = values.get("right");
            String rotate = values.get("rotate");
            String down = values.get("down");
            String pause = values.get("pause");
            
            if(!(getKeyNames().contains(left)&& getKeyNames().contains(right)&&getKeyNames().contains(rotate)&&getKeyNames().contains(down)&& getKeyNames().contains(pause))){
                System.out.println("Invalid Key in config, saving defaults");
                return;      
            }
            Config.left = left;
            Config.right = right;
            Config.rotate = rotate;
            Config.down = down;
            Config.pause = pause;
                    
            
            
                
    }
    
    public static void saveConfig() throws Exception{
        File directory = new File(getDefaultDirectory(),"/Tetris");
        if(!directory.exists()){
            directory.mkdirs();
        
        }
        File config = new File(directory, "/config.txt");
        PrintWriter pw = new PrintWriter(config);
        
        pw.println("right:" + right);
        pw.println("left:" + left);
        pw.println("rotate:" + rotate);
        pw.println("down:" + down);
        pw.println("pause:" + pause);
        pw.close();
    }
    
    public static String getDefaultDirectory(){
        String OS = System.getProperty("os.name").toUpperCase();
        if(OS.contains("WIN")){
            return System.getenv("APPDATA");
            
        }
        if(OS.contains("MAC")){
            return System.getProperty("user.home") + "Library//Application Support";
            
        }
        return System.getProperty("user.home");
    }
}
