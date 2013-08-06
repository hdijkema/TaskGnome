package net.oesterholt.taskgnome.ui;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JLabel;

public class JStatusBar extends JLabel {
		    
    /** Creates a new instance of StatusBar */
    public JStatusBar() {
        super();
        super.setPreferredSize(new Dimension(100, 16));
        setMessage("-");
    }
    
    public void setMessage(String message) {
    	super.setForeground(Color.gray);
        super.setText(" "+message);        
    }        
    
    public void setError(String m) {
    	super.setForeground(Color.red);
    	super.setText(m);;
    }
}
