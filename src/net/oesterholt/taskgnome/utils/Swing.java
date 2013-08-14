package net.oesterholt.taskgnome.utils;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.Window;
import java.util.Enumeration;

import javax.swing.SwingUtilities;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.plaf.FontUIResource;

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

	public static void scaleToScreen(String name) {
		Toolkit t = Toolkit.getDefaultToolkit();
	    int dpi = t.getScreenResolution();
	   // System.out.println("dpi="+dpi);
		float scale=((float) dpi) / 96.0f;
		try {
			UIManager.setLookAndFeel(name);

			UIDefaults defaults = UIManager.getDefaults();
			Enumeration newKeys = defaults.keys();

			while (newKeys.hasMoreElements()) {
				Object obj = newKeys.nextElement();
				Object current = UIManager.get(obj);
				if (current instanceof FontUIResource) {
					FontUIResource resource = (FontUIResource) current;
					defaults.put(obj, new FontUIResource(resource.deriveFont(resource.getSize2D()*scale)));
					// System.out.printf("%50s : %s\n", obj,  UIManager.get(obj));
				} else if (current instanceof Font) {
					Font resource = (Font) current;
					defaults.put(obj, resource.deriveFont(resource.getSize2D()*scale));
					// System.out.printf("%50s : %s\n", obj,  UIManager.get(obj));
				}
			}

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
	}		

}
