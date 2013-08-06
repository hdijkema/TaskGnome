package net.oesterholt.taskgnome;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;

import net.oesterholt.JXTwoLevelSplitTable;
import net.oesterholt.jndbm2.exceptions.NDbmException;
import net.oesterholt.splittable.AbstractTwoLevelSplitTableModel;
import net.oesterholt.taskgnome.data.CdCategory;
import net.oesterholt.taskgnome.data.CdTask;
import net.oesterholt.taskgnome.data.CdTasks;
import net.oesterholt.taskgnome.data.DataFactory;
import net.oesterholt.taskgnome.sync.Synchronizer;
import net.oesterholt.taskgnome.sync.Synchronizer.Callback;
import net.oesterholt.taskgnome.ui.TaskDialog;
import net.oesterholt.taskgnome.utils.DateUtils;
import net.oesterholt.taskgnome.utils.StringUtils;
import net.oesterholt.taskgnome.utils.TgLogger;

public class TasksController extends AbstractTwoLevelSplitTableModel implements JXTwoLevelSplitTable.SelectionListener {
	
	static Logger logger=TgLogger.getLogger(TasksController.class);
	
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
	
	JFrame					_frame;
	int						_selectedNode = -1;
	int						_selectedRow = -1;
	
	private Timer			_syncTimer;

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

		int group; 
		if (due.equals(now)) {
			group = TODAY;
		} else if (due.compareTo(now) < 0) {
			group = TOO_LATE;
		} else if (due.compareTo(tomorrow) == 0) {
			group = TOMORROW;
		} else if (due.compareTo(tomorrow) > 0 && due.compareTo(nextweek) < 0) {
			group = COMING_WEEK;
		} else {
			group = LATER;
		}
		return group;
	}
	
	public boolean isToday(int node) {
		return node == TODAY;
	}

	public boolean isPast(int node) {
		return node == TOO_LATE;
	}
	
	private void orderTasks() {
		_sortedTasks.clear();
		int i;
		for (i = 0; i <= LATER; i++) {
			_sortedTasks.add(new Vector<Integer>());
		}

		// Fill vectors and filter by kind
		final CdTasks ts = _factory.tasks();
		Iterator<CdTask> it = ts.iterator();
		i = 0;
		while (it.hasNext()) {
			CdTask c = it.next();
			logger.info(c);
			if (c.getKind() == _kind) {
				int group = this.taskGroup(c);
				Vector<Integer> section = _sortedTasks.get(group);
				section.add(i);
			}
			i += 1;
		}
		
		// Order
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
		return _expanded.get(node);
	}
	
	public int getNodeRowCount() {
		return _sortedTasks.size();
	}

	public int getNodeRowCount(int i) {
		int size =_sortedTasks.get(i).size();
		return size;
	}

	public Object getNodeValue(int node, int col) {
		if (col==0) {
			return groupName(node);
		} else {
			return new String();
		}
	}

	public Object getValueAt(int node, int i, int col) {
		try {
			CdTask t = _factory.tasks().get(_sortedTasks.get(node).get(i));
			if (col==0) {
				return (Integer) t.getPriority();
			} else if (col==1) {
				return t.getName();
			} else if (col==2) {
				return t.getDue();
			} else if (col==3) {
				return t.getCategory();
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
		return 1;
	}
	
	public String getMaxString(int col) {
		String s;
		if (col == 0) {
			//-s="99";
			s = "Coming Week***";
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
		return true;
	}
	
	public boolean hasDetails(int node, int row) {
		try {
			CdTask t = _factory.tasks().get(_sortedTasks.get(node).get(row));
			return !t.getMoreInfo().equals("");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public void setValueAt(Object val,int node, int row, int col) {
		try {
			CdTask t = _factory.tasks().get(_sortedTasks.get(node).get(row));
			if (col==0) {
				Integer prio = (Integer) val;
				if (prio == 0) { prio = 9; }
				t.setPriority(prio);
			} else if (col==1) {
				String name = (String) val;
				t.setName(name.trim());
			} else if (col==2) {
				Date d = (Date) val;
				t.setDue(d);
			} else if (col==3) {
				CdCategory c = (CdCategory) val;
				t.setCategory(c);
			}
			sync(_frame);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static String [] _cols={"Prio", "Task", "Due", "Category" };
	
	public String getColumnName(int col) {
		return _cols[col];
	}

	////////////////////////////////////////////////////////////////////

	public void choosen(int node, int row, int col, boolean left) {
		if (row < 0) { return; }
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
	
	/////////////////////////////////////////////////////
	
	void addTask(JFrame window) {
		TaskDialog dlg = new TaskDialog(window, _factory.categories());
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
				sync(window);
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
		TaskDialog dlg = new TaskDialog(window, _factory.categories());

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
				
				sync(window);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void deleteSelectedTask(JFrame _frame2) {
		// TODO Auto-generated method stub
		
	}

	public void checkSelectedTask(JFrame _frame2) {
		int node = _selectedNode;
		int row = _selectedRow;
		if (node < 0 || row < 0) {
			// do nothing
		} else {
			try {
				CdTask t = _factory.tasks().get(_sortedTasks.get(node).get(row));
				if (_kind == CdTask.KIND_ACTIVE) { 
					t.setKind(CdTask.KIND_FINISHED);
				} else {
					t.setKind(CdTask.KIND_ACTIVE);
				}
				sync(_frame2);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void changeKind(JFrame _frame2) {
		if (_kind == CdTask.KIND_ACTIVE) {
			_kind = CdTask.KIND_FINISHED;
		} else {
			_kind = CdTask.KIND_ACTIVE;
		}
		sync(_frame2);
	}
	
	public boolean isActive() {
		return _kind == CdTask.KIND_ACTIVE;
	}
	
	public void refreshFromDatabase() {
		try {
			_factory.refresh();
			orderTasks();
			super.fireTableDataChanged();
		} catch (NDbmException e) {
			e.printStackTrace();
		}
	}

	private volatile Synchronizer _syncer = null;
	private volatile boolean      _can_sync = true;
	
	private synchronized boolean isSyncing() {
		return _syncer != null;
	}
	
	private synchronized void setSyncing(Synchronizer s) {
		_syncer = s;
		logger.info("syncer = " + s);
	}
	
	public void sync(JFrame _frame) {
		if (_can_sync) {
			if (!isSyncing()) {
				setSyncing(new Synchronizer(_factory));
				_syncer.SyncNow(new Callback() {
					public void callback(Synchronizer s) {
						if (s.getErrorMessage() != null) {
							logger.error(s.getErrorMessage());;
						}
						refreshFromDatabase();
						setSyncing(null);
					}
				});
			}
		}
	}
	
	public void endSyncs(final Runnable R) {
		new Thread(new Runnable() {
			public void run() {
				while (isSyncing()) {
					logger.warn("Waiting for synchronization to finish (syncing = " + isSyncing() + ")");
					try {
						Thread.sleep(300);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				_can_sync =false;
				try {
					SwingUtilities.invokeAndWait(R);
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}
	
	/////////////////////////////////////////////////////

	public DataFactory dataFactory() {
		return _factory;
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
		refreshFromDatabase();
		_syncTimer = new Timer("TaskGnomeSync");
		_syncTimer.scheduleAtFixedRate(new TimerTask() {
			public void run() {
				sync(_frame);
			}
		}, 10000, 20*60*1000 );		// every 20 minutes
	}


}
