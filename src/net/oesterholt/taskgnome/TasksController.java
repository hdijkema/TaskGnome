package net.oesterholt.taskgnome;

import java.awt.Component;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import net.oesterholt.jndbm.NDbm2;
import net.oesterholt.jndbm2.exceptions.NDbmException;
import net.oesterholt.splittable.AbstractTwoLevelSplitTableModel;
import net.oesterholt.JXSplitTable;
import net.oesterholt.JXTwoLevelSplitTable;
import net.oesterholt.taskgnome.data.CdTask;
import net.oesterholt.taskgnome.data.CdTasks;
import net.oesterholt.taskgnome.data.DataFactory;
import net.oesterholt.taskgnome.ui.TaskDialog;
import net.oesterholt.taskgnome.utils.DateUtils;
import net.oesterholt.taskgnome.utils.StringUtils;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.Duration;

public class TasksController extends AbstractTwoLevelSplitTableModel implements JXTwoLevelSplitTable.SelectionListener {
	
	private static final long serialVersionUID = 1L;
	
	public static int TOO_LATE = 0;
	public static int TODAY = 1;
	public static int TOMORROW = 2;
	public static int COMING_WEEK = 3;
	public static int LATER = 4;
	
	private DataFactory		_factory;
	Vector<Vector<Integer>> _sortedTasks;
	Vector<Boolean>			_expanded;
	
	private int             _kind = CdTask.KIND_ACTIVE;
	
	private SimpleDateFormat _dt_format = new SimpleDateFormat("dd-MM-yyyy");
	
	JFrame					_frame;
	int						_selectedNode = -1;
	int						_selectedRow = -1;

	////////////////////////////////////////////////////////////////////
	
	private String groupName(int i) {
		if (i == TOO_LATE) { 
			if (_kind == CdTask.KIND_ACTIVE) { return "Overdue"; }
			else { return "Past"; }
		}
		else if (i == TODAY) { return "Today"; }
		else if (i == TOMORROW) { return "Tomorrow"; }
		else if (i == COMING_WEEK) { return "Coming week"; }
		else if (i == LATER) { return "Later"; }
		else { return "Unknown"; }
	}
	
	private int compareTasksActive(CdTask t1, CdTask t2) {
		int cd = t1.getDue().compareTo(t2.getDue());
		if (cd == 0) {
			int cp = ((Integer) t1.getPriority()).compareTo(t2.getPriority());
			if (cp == 0) {
				return t1.getName().compareTo(t2.getName());
			} else {
				return cp;
			}
		} else {
			return cd;
		}
	}

	private int compareTasksFinished(CdTask t1, CdTask t2) {
		int cd = t2.getDue().compareTo(t1.getDue());
		if (cd == 0) {
			int cp = ((Integer) t1.getPriority()).compareTo(t2.getPriority());
			if (cp == 0) {
				return t1.getName().compareTo(t2.getName());
			} else {
				return cp;
			}
		} else {
			return cd;
		}
	}

	private Integer taskGroup(CdTask c) {
		Date due = c.getDue();

		Date now = DateUtils.today();
		Date tomorrow = DateUtils.tomorrow();
		Date nextweek = DateUtils.nextweek();

		if (due.equals(now)) {
			return TODAY;
		} else if (due.compareTo(now) < 0) {
			return TOO_LATE;
		} else if (due.compareTo(tomorrow) == 0) {
			return TOMORROW;
		} else if (due.compareTo(tomorrow) > 0 && due.compareTo(nextweek) < 0) {
			return COMING_WEEK;
		} else {
			return LATER;
		}
	}
	
	private void orderTasks() {
		_sortedTasks.clear();
		int i;
		for (i = 0; i <= LATER; i++) {
			_sortedTasks.add(new Vector<Integer>());
		}

		final CdTasks ts = _factory.tasks();
		Iterator<CdTask> it = ts.iterator();
		i = 0;
		while (it.hasNext()) {
			CdTask c = it.next();
			int group = this.taskGroup(c);
			Vector<Integer> section = _sortedTasks.get(group);
			section.add(i);
			i += 1;
		}
		
		for (i = 0; i <= LATER; i++) {
			Vector<Integer> section = _sortedTasks.get(i);
			if (_kind == CdTask.KIND_ACTIVE) {
				Collections.sort(section, new Comparator<Integer>() {
					public int compare(Integer o1, Integer o2) {
						CdTask t1 = ts.get(o1);
						CdTask t2 = ts.get(o2);
						return compareTasksActive(t1,t2);
					}
				});
			} else {
				Collections.sort(section, new Comparator<Integer>() {
					public int compare(Integer o1, Integer o2) {
						CdTask t1 = ts.get(o1);
						CdTask t2 = ts.get(o2);
						return compareTasksFinished(t2,t1);
					}
				});
			}
			
		}
	}

	////////////////////////////////////////////////////////////////////
	
	// voor de node zelf
	public int getNodeColumnCount() {
		return 1;
	}

	// voor de rij in de node
	public int getNodeColumnCount(int a) {
		return 4;
	}

	public boolean getNodeExpanded(int node) {
		return true;
	}
	
	public int getNodeRowCount() {
		return _sortedTasks.size();
	}

	public int getNodeRowCount(int i) {
		return _sortedTasks.get(i).size();
	}

	public Object getNodeValue(int node, int col) {
		if (col==0) {
			return groupName(node);
		} else {
			return new String();
		}
	}

	public Object getValueAt(int node, int i,int col) {
		try {
			CdTask t = _factory.tasks().get(_sortedTasks.get(node).get(i));
			
			if (col==0) {
				if (t.getPriority() == 9 || t.getPriority() == 0) { 
					return "-";
				} else {
					return (Integer) t.getPriority();
				}
			} else if (col==1) {
				return t.getName();
			} else if (col==2) {
				Date d = t.getDue();
				return _dt_format.format(d);
			} else if (col==3) {
				return t.getCategory().getName();
			} else {
				return new String();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new String();
		}
	}

	public boolean setNodeExpanded(int node, boolean exp) {
		_expanded.set(node, !_expanded.get(node));
		return _expanded.get(node);
	}

	public int getSplitColumn() {
		return 0;
	}
	
	public String getMaxString(int col) {
		String s;
		if (col == 0) {
			//-s="99";
			s = "Coming Week";
		} else if (col == 1) {
			s = StringUtils.makeString(30, "name");
		} else if (col == 2) {
			s = "2013-01-01";
		} else {
			s = StringUtils.makeString(2, "category");
		}
		return s;
	}
	
	public boolean isCellEditable(int node,int row,int col) {
		return false;
	}
	
	public void setValueAt(Object val,int node, int row, int col) {
	}
	
	private static String [] _cols={"Prio", "Task", "Due", "Category" };
	
	public String getColumnName(int col) {
		return _cols[col];
	}

	////////////////////////////////////////////////////////////////////

	public void choosen(int node, int row, int col, boolean left) {
		CdTask t = _factory.tasks().get(_sortedTasks.get(node).get(row));
		this.editTask(_frame, t);
	}

	public void selected(int node, int row, int col, boolean left) {
		_selectedNode = node;
		_selectedRow = row;
	}

	public void unSelected(boolean arg0) {
		_selectedNode = -1;
		_selectedRow = -1;
	}

	////////////////////////////////////////////////////////////////////
	
	public AbstractTwoLevelSplitTableModel model() {
		return this;
	}
	
	// precondition: String is verified as a number (floating point)
	public boolean verify(String input,int col) {
		Float fl=Float.valueOf(input);
		if (col==1) { // budget
			return true;
		} else if (col>=3) {
			return (fl>=0 && fl<=24.0);
		} else {
			return false;
		}
	}
	
	public DateTime toMonday(DateTime d) {
		Duration dur=new Duration(24*3600*1000);
		while (d.getDayOfWeek()!=DateTimeConstants.MONDAY) {
			d=d.minus(dur);
		}
		return d;
	}
	
	/////////////////////////////////////////////////////
	
	void addTask(JFrame window) {
		TaskDialog dlg = new TaskDialog(window, _factory.categories().getCategories());
		dlg.setVisible(true);
		if (dlg.ok()) {
			CdTask newtask;
			try {
				newtask = _factory.newTask();
				
				newtask.setName(dlg.getName());
				newtask.setPriority(dlg.getPrio());
				newtask.setCategory(dlg.getCategory());
				newtask.setDue(dlg.getDue());
				newtask.setMoreInfo(dlg.getMoreInfo());
				newtask.setKind(dlg.getKind());
				
				_factory.tasks().add(newtask);
				orderTasks();
				super.fireTableDataChanged();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	void editSelectedTask(JFrame window) {
		int node = _selectedNode;
		int row = _selectedRow;
		if (node < 0 || row < 0) {
			// do nothing
		} else {
			CdTask t = _factory.tasks().get(_sortedTasks.get(node).get(row));
			editTask(window, t);
		}
	}

	void editTask(JFrame window, CdTask t) {
		TaskDialog dlg = new TaskDialog(window, _factory.categories().getCategories());

		dlg.setName(t.getName());
		dlg.setMoreInfo(t.getMoreInfo());
		dlg.setKind(t.getKind());
		dlg.setDue(t.getDue());
		dlg.setCategory(t.getCategory());
		dlg.setPrio(t.getPriority());
		
		dlg.setVisible(true);
		
		if (dlg.ok()) {
			try {
				CdTask newtask = t;
				
				newtask.setName(dlg.getName());
				newtask.setPriority(dlg.getPrio());
				newtask.setCategory(dlg.getCategory());
				newtask.setDue(dlg.getDue());
				newtask.setMoreInfo(dlg.getMoreInfo());
				newtask.setKind(dlg.getKind());
				
				orderTasks();
				super.fireTableDataChanged();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	/////////////////////////////////////////////////////
	
	public TasksController(JFrame fr, DataFactory f) {
		_frame = fr;
		_factory = f;
		_expanded = new Vector<Boolean>();
		int i;
		for (i = 0; i <= LATER; i++) {
			_expanded.add(true);
		}
		_sortedTasks = new Vector<Vector<Integer>>();
		orderTasks();
	}


}