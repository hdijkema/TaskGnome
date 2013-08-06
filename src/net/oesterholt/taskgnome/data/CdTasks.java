package net.oesterholt.taskgnome.data;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import net.oesterholt.jndbm.NDbm2;
import net.oesterholt.jndbm2.exceptions.NDbmException;

public class CdTasks extends Vector<CdTask> {

	private static final long serialVersionUID = 1L;
	
	private Id _id;
	//private NDbm2 _dbm;
	private CdCategories _categories;
	
	private Hashtable<String, CdTask> _tasks;
	
	public CdTasks(CdCategories cats) throws NDbmException {
		_id = new Id(cats.dbm(), "tasks");
		_categories = cats;
		_tasks = new Hashtable<String, CdTask>();
		//_dbm = cats.dbm();
		read();
	}
	
	protected void write() throws NDbmException {
		Vector<String> task_ids = new Vector<String>();
		Iterator<CdTask> it = super.iterator();
		while (it.hasNext()) {
			CdTask task = it.next();
			task_ids.add(task.id());
		}
		_id.dbm().putVectorOfString(_id.id(), task_ids);
	}
	
	protected void read() throws NDbmException {
		super.clear();
		Vector<String> task_ids = _id.dbm().getVectorOfString(_id.id());
		if (task_ids!=null) {
			Iterator<String> it = task_ids.iterator();
			while (it.hasNext()) {
				String id = it.next();
				Id task_id = new Id(_id.dbm(),id);
				CdTask task = new CdTask(_categories, _id.dbm(), task_id);
				super.add(task);
				_tasks.put(task.id(), task);
			}
		}
	}
	
	public CdTask getTask(String id) {
		return _tasks.get(id);
	}
	
	public boolean add(CdTask task) {
		boolean b = super.add(task);
		_tasks.put(task.id(), task);
		try {
			write();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return b;
	}
	
	public void add(int index, CdTask task) {
		super.add(index, task);
		_tasks.put(task.id(), task);
		try {
			write();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public CdTask remove(int index) {
		CdTask b = super.remove(index);
		if (b != null) {
			_tasks.remove(b.id());
		}
		try {
			write();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return b;
	}
	
	public CdTask set(int index, CdTask task) {
		
		CdTask t = super.set(index,task);
		if (t != null) {
			_tasks.remove(t.id());
		}
		_tasks.put(task.id(), task);
		
		try {
			write();
		} catch(Exception e) {
			e.printStackTrace();
		}
		return t;
	}
	
	public void clear() {
		super.clear();
		_tasks.clear();
		try {
			write();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void removeTask(String task_id) {
		CdTask t = _tasks.get(task_id);
		if (t != null) {
			super.remove(t);
			_tasks.remove(task_id);
		}
		try {
			write();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
