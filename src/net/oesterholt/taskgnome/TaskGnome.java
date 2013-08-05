package net.oesterholt.taskgnome;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.URL;
import java.util.Vector;
import java.util.prefs.Preferences;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;

import net.oesterholt.taskgnome.sync.Synchronizer;
import net.oesterholt.taskgnome.utils.TgLogger;

import org.apache.log4j.Appender;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Logger;

public class TaskGnome {
	
	static Logger logger=TgLogger.getLogger(TaskGnome.class);
	
	public static void setIconImage(Window w) {
		URL url=TaskGnome.class.getResource("/net/oesterholt/taskgnome/resources/icon.png");
		ImageIcon icon = new ImageIcon(url);
		w.setIconImage(icon.getImage());
	}
	
	public static ImageIcon toolBarIcon(String name) {
		URL url=TaskGnome.class.getResource(
					String.format("/net/oesterholt/taskgnome/resources/%s.png",name)
					);
		return new ImageIcon(
					new ImageIcon(url).getImage().getScaledInstance(48, 48, Image.SCALE_SMOOTH)
					);
	}
	
	@SuppressWarnings("serial")
	public static JButton toolBarAction(final String action,final ActionListener l) {
    	JButton b=new JButton(new AbstractAction(action,TaskGnome.toolBarIcon(action)) {
    		public void actionPerformed(ActionEvent e) {
    			ActionEvent E=new ActionEvent(e.getSource(), e.getID(), action);
				l.actionPerformed(E);
    		}
    	});
    	b.setFocusable(false);
    	b.setHideActionText(true);
    	return b;
    }
	
	public static JMenuItem menu(final String action, final String txt,ActionListener l) {
		JMenuItem mnu=new JMenuItem(txt);
		mnu.addActionListener(l);
		mnu.setActionCommand(action);
		return mnu;
	}
	
	public static void setWindowPosition(Point where,Dimension size) {
		Preferences prefs=Preferences.userNodeForPackage(TaskGnome.class);
		prefs.putInt("wx", where.x);
		prefs.putInt("wy", where.y);
		prefs.putInt("width", size.width);
		prefs.putInt("height", size.height);
	}
	
	public static Point getPrevWindowLocation() {
		Preferences prefs=Preferences.userNodeForPackage(TaskGnome.class);
		int x=prefs.getInt("wx", -1);
		int y=prefs.getInt("wy", -1);
		if (x<0 || y<0) {
			return null;
		} else {
			return new Point(x,y);
		}
	}
	
	public static Dimension getPrevWindowSize() {
		Preferences prefs=Preferences.userNodeForPackage(TaskGnome.class);
		int w=prefs.getInt("width", -1);
		int h=prefs.getInt("height", -1);
		if (w<0 || h<0) {
			return null;
		} else {
			return new Dimension(w,h);
		}
	}
	
	public static void setLastPath(String f) {
		Preferences prefs=Preferences.userNodeForPackage(TaskGnome.class);
		prefs.put("lastpath",f);
	}
	
	public static Vector<String> getRecentlyUsed() {
		Preferences prefs=Preferences.userNodeForPackage(TaskGnome.class);
		Integer i;
		Vector<String> r=new Vector<String>();
		for(i=0;i<5;i++) {
			String key="recently_"+i;
			String f=prefs.get(key, null);
			if (f!=null) {
				r.add(f);
			}
		}
		return r;
	}
	
	
	public static void main(String argv[]) {
		//ConsoleAppender lap = new ConsoleAppender();
		//lap.setName("taskgnome");
		//lap.setTarget(ConsoleAppender.SYSTEM_OUT);
		//BasicConfigurator.configure(lap);
		BasicConfigurator.configure();
		//Logger.getRootLogger().addAppender(lap);
		
		logger.debug("TaskGnome started");
		
		File td = new File(System.getProperty("user.home"), ".taskgnome");
		TaskWindow u;
		try {
			u = new TaskWindow(td.getAbsolutePath());
			SwingUtilities.invokeLater(u);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	} 

}