package net.oesterholt.taskgnome.data;

import java.io.File;

import net.oesterholt.jndbm.NDbm2;
import net.oesterholt.jndbm2.exceptions.NDbmException;

public class DataFactory {
	
	NDbm2 _dbm;
	File  _dbmDir;
	CdCategories _categories;
	CdTasks _tasks;
	CdDeletedTasks _deleted_tasks;
	boolean _closed;
	
	public DataFactory(File dbmDir) throws Exception {
		_dbmDir = dbmDir;
		
		if (!dbmDir.exists()) {
			dbmDir.mkdirs();
		}
		
		if (dbmDir.isDirectory()) {
			File dbmFile = new File(dbmDir, "tasks.db");
			_dbm = NDbm2.openNDbm(dbmFile, false);
			
			_categories = new CdCategories(_dbm);
			_tasks = new CdTasks(_categories);
			_deleted_tasks = new CdDeletedTasks(_dbm);
		} else {
			throw new Exception("Cannot open database");
		}
		
		_closed = false;
	}
	
	protected void finalize() throws Throwable {
	     try {
	    	 _categories = null;
	    	 _tasks = null;
	    	 _deleted_tasks = null;
	         if (!_closed) { _dbm.close(); }
	     } finally {
	         super.finalize();
	     }
	}
	
	public void begin() throws NDbmException {
		_dbm.begin();
	}
	
	public void commit() throws NDbmException {
		_dbm.commit();
	}
	
	public void clear() throws NDbmException {
		begin();
		_tasks.clear();
		_categories.clear();
		_deleted_tasks.clear();
		commit();
	}
	
	public void destroy() {
		try {
			_dbm.close();
			_closed = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public DataFactory copy() throws Exception {
		return new DataFactory(_dbmDir);
	}
	
	public CdTasks tasks() {
		return _tasks;
	}
	
	public CdDeletedTasks deletedTasks() {
		return _deleted_tasks;
	}
	
	public CdCategories categories() {
		return _categories;
	}
	
	public CdTask newTask() throws Exception {
		CdTask task = new CdTask(_dbm);
		task.setCategory(categories().get("-"));
		return task;
	}
	
	public CdTask newTask(String id) throws Exception {
		Id task_id = new Id(_dbm, id);
		CdTask task = new CdTask(task_id);
		task.setCategory(categories().get("-"));
		return task;
	}
	
	public CdCategory newCategoryForceId(String name, String forceId) throws Exception {
		return new CdCategory(_dbm, name, forceId);
	}
	
	public void refresh() throws NDbmException {
		_categories = new CdCategories(_dbm);
		_tasks = new CdTasks(_categories);
		_deleted_tasks = new CdDeletedTasks(_dbm);
	}
}
