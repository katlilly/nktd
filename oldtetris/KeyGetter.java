
package Tetris;

import java.awt.event.KeyEvent;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;


public class KeyGetter {
    public static HashMap<String,Integer  > keys ;
    public static ArrayList<String> keyNames;
    public static void loadKeys(){
    keys = new HashMap <String, Integer >();
    keyNames = new ArrayList<String>();
    
    Field[] fields = KeyEvent.class.getFields();
    for(Field f: fields ){
        if(Modifier.isStatic(f.getModifiers())){
           if( f.getName().startsWith("VK")){
               try{
                   int num = f.getInt(null);
                   String name = KeyEvent.getKeyText(num);
                   keys.put( name,num);
                   keyNames.add(name);
                   
               //keys.put(f.getInt(null), KeyEvent.getKeyText(f.getInt(null)));
               }
               catch(Exception e ){
                   e.printStackTrace();
               
               }
           }
            


    }
    }
}
}
