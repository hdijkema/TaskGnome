package net.oesterholt.taskgnome;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Point;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.net.URL;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JToolBar;
import javax.swing.UIManager;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.log4j.Logger;

import net.miginfocom.swing.MigLayout;
import net.oesterholt.jndbm.NDbm2;
import net.oesterholt.jndbm2.exceptions.NDbmException;
import net.oesterholt.taskgnome.data.CdCategories;
import net.oesterholt.taskgnome.data.DataFactory;
import net.oesterholt.taskgnome.ui.JStatusBar;
import net.oesterholt.taskgnome.utils.Config;
import net.oesterholt.taskgnome.utils.TgLogger;

public class TaskWindow implements Runnable, ActionListener {

	static Logger logger=TgLogger.getLogger(TaskWindow.class);
	
	private JFrame 			_frame;
	private JMenuBar 		_menu;
	private JButton			_finished;
	private JButton			_active;
	private JStatusBar      _status;
	
	private DataFactory		_factory;
	private TasksController _controller;
	private TasksView       _view;
	
	private void einde() {
		_controller.endSyncs(new Runnable() {
			public void run() {
				_factory = null;
				TaskGnome.setWindowPosition(_frame.getLocation(),_frame.getSize());
				_frame.setVisible(false);
				System.exit(0);
			}
		});
	}
	
	private void hideshow() {
		if (_frame.isVisible()) {
			_frame.setVisible(false);;
		} else {
			_frame.setVisible(true);
		}
	}
	
	public void actionPerformed(ActionEvent e) {
		String cmd=e.getActionCommand();
		if ("quit".equals(cmd)) {
			einde();
		} else if ("addtask".equals(cmd)) {
			_controller.addTask(_frame);
		} else if ("changetask".equals(cmd)) {
			_controller.editSelectedTask(_frame);
		} else if ("deletetask".equals(cmd)) {
			_controller.deleteSelectedTask(_frame);
		} else if ("checktask".equals(cmd)) {
			_controller.checkSelectedTask(_frame);
		} else if ("changekind".equals(cmd)) {
			_controller.changeKind(_frame);
			_finished.setVisible(false);
			_active.setVisible(true);;
		} else if ("changekind1".equals(cmd)) {
			_controller.changeKind(_frame);
			_active.setVisible(false);;
			_finished.setVisible(true);
		} else if ("sync".equals(cmd)) {
			_controller.sync(_frame);
		} else if ("prefs".equals(cmd)) {
			_controller.prefs(_frame);
		}
	}
	
	public TaskWindow(String dataDirectory) throws Exception {
		_factory = new DataFactory(new File(dataDirectory));
		CdCategories cats = _factory.categories();
		try {
			if (!cats.containsKey("-")) { cats.put(_factory.newCategoryForceId("-", "-")); }
			if (!cats.containsKey("Inbox_id")) { cats.put(_factory.newCategoryForceId("Inbox", "Inbox_id")); }
			if (!cats.containsKey("Personal_id")) { cats.put(_factory.newCategoryForceId("Personal", "Personal_id")); }
			if (!cats.containsKey("Work_id")) { cats.put(_factory.newCategoryForceId("Work", "Work_id")); }
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void message(String m) {
		_status.setMessage(m);;
	}
	
	public void errorMessage(String m) {
		_status.setError(m);
	}
	
	@SuppressWarnings("serial")
	public void run() {

		// Look and feel
		
		try {
			//UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    		UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
			//UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
	    } catch(Exception e) {
	    	try {
		    	UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
	    	} catch (Exception e1) {
	    		// 	ignore exception
	    	}
	    }
	    
	    // menu
	    {
	    	_menu=new JMenuBar();
	    	
	    	JMenu tasks=new JMenu("Tasks");
	    	
	    	tasks.add(TaskGnome.menu("addtask",  "Add Task",  this));
	    	tasks.add(TaskGnome.menu("changetask", "Change Task", this));
	    	tasks.add(TaskGnome.menu("checktask", "Toggle Task", this));
	    	tasks.add(new JSeparator());
	    	tasks.add(TaskGnome.menu("deletetask",  "Delete Task",  this));
	    	tasks.add(new JSeparator());
	    	tasks.add(TaskGnome.menu("quit","Quit", this));
	    }
	    
	    _frame=new JFrame("Task Gnome - " + Config.Copyright());
	    _controller = new TasksController(_frame, _factory, this);
	    _view = new TasksView(_controller);
	    
	    // tools
    	JToolBar bar=new JToolBar();
    	{ 
	    	bar.add(TaskGnome.toolBarAction("quit",this));
	    
	    	bar.add(new JSeparator(JSeparator.VERTICAL));
	    	bar.add(TaskGnome.toolBarAction("addtask", this));
	    	bar.add(TaskGnome.toolBarAction("changetask", this));
	    	bar.add(new JSeparator(JSeparator.VERTICAL));
	    	JButton deleted = TaskGnome.toolBarAction("deletetask", this); 
	    	bar.add(deleted);
	    	bar.add(new JSeparator(JSeparator.VERTICAL));
	    	bar.add(TaskGnome.toolBarAction("checktask", this));
	    	bar.add(new JSeparator(JSeparator.VERTICAL));
	    	bar.add(TaskGnome.toolBarAction("prefs", this));
	    	bar.add(Box.createHorizontalGlue());
	    	_finished = TaskGnome.toolBarAction("changekind", this);
	    	_active =  TaskGnome.toolBarAction("changekind1", this);
	    	_active.setVisible(false);
	    	bar.add(_finished);bar.add(_active);
	    	
	    	//bar.add(TaskGnome.toolBarAction("sync", this));
	    
	    	bar.setFloatable(false);
	    	bar.setFocusable(false);
	    
	    	bar.setBorder(BorderFactory.createEtchedBorder());
	    }
    	
    	// Status bar
    	_status = new JStatusBar();
    	
    	
    	// System tray
    	if (!SystemTray.isSupported()) {
    		logger.warn("System tray is not supported on this system");
    	} else {
    		URL url=TaskGnome.class.getResource(
					String.format("/net/oesterholt/taskgnome/resources/%s.png","icon")
					);
    		
    		final TrayIcon icon = new TrayIcon(new ImageIcon(url).getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH));
    		final SystemTray tray = SystemTray.getSystemTray();
    		try {
	    		tray.add(icon);;
	    		icon.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						if (_frame.isVisible()) {
							_frame.setVisible(false);
						} else {
							_frame.setVisible(true);
						}
					}
	    		});
	    		icon.addMouseListener(new MouseListener() {
					public void mouseClicked(MouseEvent e) {
						if (_frame.isVisible()) {
							_frame.setVisible(false);
						} else {
							_frame.setVisible(true);
						}
					}
					public void mousePressed(MouseEvent e) { }
					public void mouseReleased(MouseEvent e) { }
					public void mouseEntered(MouseEvent e) { }
					public void mouseExited(MouseEvent e) { }
	    			
	    		});
    		} catch (Exception e) {
    			logger.error(e);
    		}
    	}

	    _frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
	    _frame.addWindowListener(new WindowAdapter() {
	    	public void windowClosing(WindowEvent e) {
	    		hideshow();
	    	} 
	    });
	    TaskGnome.setIconImage(_frame);
	    _frame.setJMenuBar(_menu);
	    {
	    	JPanel p=new JPanel(new MigLayout("fill"));;
	    	p.add(bar,"dock north, growx, wrap");
	    	p.add(_view,"growx, growy");
	    	p.add(_status, "dock south, growx, wrap");
	    	_frame.add(p);
	    }
	    Point loc=TaskGnome.getPrevWindowLocation();
	    Dimension size=TaskGnome.getPrevWindowSize();
	    if (size!=null) { _frame.setPreferredSize(size); }
	    _frame.pack();
	    if (loc!=null) { _frame.setLocation(loc); }
	    _frame.setVisible(true);
	    
	}
	
}
