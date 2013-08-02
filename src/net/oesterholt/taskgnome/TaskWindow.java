package net.oesterholt.taskgnome;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
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

import net.miginfocom.swing.MigLayout;
import net.oesterholt.jndbm.NDbm2;
import net.oesterholt.jndbm2.exceptions.NDbmException;
import net.oesterholt.taskgnome.data.CdCategories;
import net.oesterholt.taskgnome.data.DataFactory;

public class TaskWindow implements Runnable, ActionListener {

	private JFrame 			_frame;
	private JMenuBar 		_menu;
	
	private DataFactory		_factory;
	private TasksController _controller;
	private TasksView       _view;
	
	private void einde() {
		_factory = null;
		TaskGnome.setWindowPosition(_frame.getLocation(),_frame.getSize());
		_frame.setVisible(false);
		System.exit(0);
	}
	
	public void actionPerformed(ActionEvent e) {
		String cmd=e.getActionCommand();
		if ("quit".equals(cmd)) {
			einde();
		} else if ("addtask".equals(cmd)) {
			_controller.addTask(_frame);
		} else if ("changetask".equals(cmd)) {
			_controller.editSelectedTask(_frame);
//			_controler.wijzigProject(_frame);
		} else if ("delproject".equals(cmd)) {
//			_controler.verwijderProject(_frame);
		} else if ("weekstaat".equals(cmd)) {
//			_controler.reportWeek(_frame);
		} else if ("maandstaat".equals(cmd)) {
//			_controler.reportMonth(_frame);
		} else if ("jaarstaat".equals(cmd)) {
//			_controler.reportJaar(_frame);
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
	
	@SuppressWarnings("serial")
	public void run() {

		// Look and feel
		
		try {
    		UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
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
	    
	    _frame=new JFrame("Task Gnome");
	    _controller = new TasksController(_frame, _factory);
	    _view = new TasksView(_controller);
	    
	    // tools
    	JToolBar bar=new JToolBar();
    	{ 
	    	bar.add(TaskGnome.toolBarAction("quit",this));
	    
	    	bar.add(new JSeparator(JSeparator.VERTICAL));
	    	bar.add(TaskGnome.toolBarAction("addtask", this));
	    	bar.add(TaskGnome.toolBarAction("changetask", this));
	    	bar.add(new JSeparator(JSeparator.VERTICAL));
	    	bar.add(TaskGnome.toolBarAction("deletetask", this));
	    	bar.add(new JSeparator(JSeparator.VERTICAL));
	    	bar.add(TaskGnome.toolBarAction("checktask", this));
	    
	    	bar.setFloatable(false);
	    	bar.setFocusable(false);
	    
	    	bar.setBorder(BorderFactory.createEtchedBorder());
	    }

	    _frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
	    _frame.addWindowListener(new WindowAdapter() {
	    	public void windowClosing(WindowEvent e) {
	    		einde();
	    	} 
	    });
	    TaskGnome.setIconImage(_frame);
	    _frame.setJMenuBar(_menu);
	    {
	    	JPanel p=new JPanel(new MigLayout("fill"));;
	    	p.add(bar,"growx,wrap");
	    	p.add(_view,"growx,growy");
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
