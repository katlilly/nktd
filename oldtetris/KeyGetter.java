package tetris;

import java.util.HashMap;
import java.lang.reflect.Field;
import java.awt.event.KeyEvent;
import java.lang.reflect.Modifier;

public class KeyGetter {

    //private int[] keys;
    private static HashMap<Integer, String> keys;
    // the integer is a key to get the string object
    
    public static void loadKeys() {
	keys = new HashMap<Integer, String>();
	Field[] fields = KeyEvent.class.getFields();
	for(Field f: fields) {
	    if (Modifier.isStatic(f.getModifiers())) {
		System.out.println();
	    }
	}
    }

}
