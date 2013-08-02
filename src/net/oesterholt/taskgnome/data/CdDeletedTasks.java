package net.oesterholt.taskgnome.data;

import java.util.Iterator;
import java.util.Vector;

import net.oesterholt.jndbm.NDbm2;
import net.oesterholt.jndbm2.exceptions.NDbmException;

public class CdDeletedTasks extends Vector<String> {

	private static final long serialVersionUID = 1L;
	
	private Id _id;
	
	public CdDeletedTasks(NDbm2 dbm) throws NDbmException {
		_id = new Id(dbm, "deletedtasks");
		read();
	}
	
	protected void read() throws NDbmException {
		super.clear();
		Vector<String> ids = _id.dbm().getVectorOfString(_id.id());
		Iterator<String> it = ids.iterator();
		while (it.hasNext()) {
			super.add(it.next());
		}
	}
	
	protected void write() throws NDbmException {
		_id.dbm().putVectorOfString(_id.id(), this);
	}
	
	public boolean add(String id) {
		boolean b = super.add(id);
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
		try {
			write();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public String remove(int index) {
		String s = super.remove(index);
		if (s != null) {
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
		try {
			write();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return prev;
	}
	
	public void clear() {
		super.clear();
		try {
			write();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
}
