package net.oesterholt.taskgnome.data;

import java.io.File;

import net.oesterholt.jndbm.NDbm2;

public class DataFactory {
	
	NDbm2 _dbm;
	CdCategories _categories;
	CdTasks _tasks;
	
	public DataFactory(File dbmDir) throws Exception {
		if (!dbmDir.exists()) {
			dbmDir.mkdirs();
		}
		
		if (dbmDir.isDirectory()) {
			File dbmFile = new File(dbmDir, "tasks.db");
			_dbm = NDbm2.openNDbm(dbmFile, false);
			
			_categories = new CdCategories(_dbm);
			_tasks = new CdTasks(_categories);
		} else {
			throw new Exception("Cannot open database");
		}
	}
	
	protected void finalize() throws Throwable {
	     try {
	    	 _categories = null;
	    	 _tasks = null;
	         _dbm.close();
	     } finally {
	         super.finalize();
	     }
	}
	
	public CdTasks tasks() {
		return _tasks;
	}
	
	public CdCategories categories() {
		return _categories;
	}
	
	public CdTask newTask() throws Exception {
		return new CdTask(_dbm);
	}
	
	public CdCategory newCategoryForceId(String name, String forceId) throws Exception {
		return new CdCategory(_dbm, name, forceId);
	}
}
