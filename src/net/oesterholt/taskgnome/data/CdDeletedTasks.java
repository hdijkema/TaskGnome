package net.oesterholt.taskgnome.data;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;

import net.oesterholt.jndbm.NDbm2;
import net.oesterholt.jndbm2.exceptions.NDbmException;

public class CdDeletedTasks extends Vector<String> {

	private static final long serialVersionUID = 1L;
	
	private Id _id;
	private HashSet<String> _deleted_tasks;
	
	public CdDeletedTasks(NDbm2 dbm) throws NDbmException {
		_id = new Id(dbm, "deletedtasks");
		_deleted_tasks = new HashSet<String>();
		read();
	}
	
	protected void read() throws NDbmException {
		super.clear();
		Vector<String> ids = _id.dbm().getVectorOfString(_id.id());
		Iterator<String> it = ids.iterator();
		while (it.hasNext()) {
			String s = it.next();
			super.add(s);
			_deleted_tasks.add(s);
		}
	}
	
	protected void write() throws NDbmException {
		_id.dbm().putVectorOfString(_id.id(), this);
	}
	
	public boolean add(String id) {
		boolean b = super.add(id);
		_deleted_tasks.add(id);
		try {
			write();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return b;
	}
	
	public void add(int index, String id) {
		super.add(index, id);
		_deleted_tasks.add(id);
		try {
			write();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public String remove(int index) {
		String s = super.remove(index);
		if (s != null) {
			_deleted_tasks.remove(s);
			try {
				write();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return s;
	}
	
	public String set(int index, String id) {
		String prev = super.set(index, id);
		if (prev != null) {
			_deleted_tasks.remove(prev);
		} 
		_deleted_tasks.add(id);
		
		try {
			write();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return prev;
	}
	
	public void clear() {
		super.clear();
		_deleted_tasks.clear();
		try {
			write();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public boolean containsTask(String task_id) {
		return _deleted_tasks.contains(task_id);
	}
	
}
