package net.oesterholt.taskgnome.ui;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JLabel;

import net.oesterholt.taskgnome.utils.Config;

public class JStatusBar extends JLabel {
		    
    /** Creates a new instance of StatusBar */
    public JStatusBar() {
        super();
        super.setPreferredSize(new Dimension(100, 16));
        setMessage("-");
    }
    
    public void setMessage(String message) {
    	Config cfg = new Config();
    	super.setForeground(Color.gray);
        super.setText(" "+cfg.getUserId()+" - "+message);        
    }        
    
    public void setError(String m) {
    	Config cfg = new Config();
    	super.setForeground(Color.red);
    	super.setText(" "+cfg.getUserId()+" - "+m);
    }
}
