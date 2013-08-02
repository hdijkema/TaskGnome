package net.oesterholt.taskgnome.utils;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Window;

import javax.swing.SwingUtilities;

public class Swing {
	
	public static void centerOnParent(Window w,Component parent) {
		Window p = getWindow(parent);

		Dimension psize=p.getSize();
		Dimension wsize=w.getSize();
		
		Point wpoint = p.getLocation();
		
		int my=psize.height/2-wsize.height/2;
		int mx=psize.width/2-wsize.width/2;
		
		w.setLocation(mx + wpoint.x,my + wpoint.y);
	}
	
	public static Window getWindow(Component c) {
		return (Window) ((c instanceof Window) ? c : SwingUtilities.getWindowAncestor(c));
	}

}
