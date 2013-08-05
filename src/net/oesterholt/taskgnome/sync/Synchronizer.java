package net.oesterholt.taskgnome.sync;


import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.jdesktop.swingworker.SwingWorker;

import net.oesterholt.jndbm.NDbm2;
import net.oesterholt.jndbm2.exceptions.NDbmException;
import net.oesterholt.taskgnome.data.CdDeleted;
import net.oesterholt.taskgnome.data.CdDeletedTasks;
import net.oesterholt.taskgnome.data.CdTask;
import net.oesterholt.taskgnome.data.CdTasks;
import net.oesterholt.taskgnome.data.DataFactory;
import net.oesterholt.taskgnome.utils.TgLogger;

public class Synchronizer {

	static Logger logger=TgLogger.getLogger(Synchronizer.class);

	class TaskIdInfo {
		public boolean deleted;
		public boolean local;
		public String  md5;
		public String  task_id;
		public boolean updated_locally;
	};
	
	class KeyValue {
		public String key;
		public String value;
		
		public String toString() {
			return key + "=" + value + " ";
		}
		
		public KeyValue(String kv) {
			kv = kv.trim();
			String [] kkvv = kv.split("\\s*[=]\\s*");
			key = kkvv[0];
			if (kkvv.length > 1) {
				value = kkvv[1];
			} else {
				value = "";
			}
		}
	}
	
	public interface Callback {
		public void callback(Synchronizer s);
	};
	
	interface FetchInterpreter {
		public void interpret(String line) throws Exception;
	};
	
	private DataFactory 	_factory;
	private boolean    		_is_syncing;
	private String			_server = "http://taskgnome.oesterholt.net/taskgnome.php";
	private String			_user = "hans@oesterholt.net";
	private String			_pass = "rotop2";
	private String			_last_error = null;
	
	public Synchronizer(DataFactory fac) {
		try {
			_factory = fac.copy();
		} catch (Exception e) {
			e.printStackTrace();
			_factory = null;
		}
		_is_syncing = false;
	}
	
	public void destroy() {
		_factory.destroy();
	}
	
	public String getErrorMessage() {
		return _last_error;
	}
	
	private synchronized boolean canSync() {
		if (_is_syncing) {
			return false;
		} else {
			_is_syncing = true;
		}
		return true;
	}
	
	public void SyncNow(final Callback cb) {
		if (canSync()) {
			final Synchronizer S = this;
			SwingWorker<Boolean, Object> w = new SwingWorker<Boolean, Object>() {
				protected Boolean doInBackground() throws Exception {
					return doSync();
				}
				
				protected void done() {
					cb.callback(S);
				}
				
			};
			w.execute();
		}
	}

	/**
	 * Here the synchronization algorithm needs to be run
	 */
	public boolean doSync() {
		boolean result;
		try {
			this._last_error = null;
			Hashtable<String, TaskIdInfo> info = fetchIds();
			processIds(info);
			System.out.println(info);
			result = (this._last_error == null);
		} catch (Exception e) {
			e.printStackTrace();
			if (this._last_error == null) {
				_last_error = e.getMessage();
			}
			result = false;
		}
		
		_is_syncing = false;
		this.destroy();
		
		return result;
	}
	
	private Hashtable<String, TaskIdInfo> fetchIds() throws Exception {
		final Hashtable<String, TaskIdInfo> result = new Hashtable<String, TaskIdInfo>();
		
		// remote ids
		Hashtable<String, Object> dict = new Hashtable<String, Object>();
		boolean ok = fetch("getids", dict, new FetchInterpreter() {
			public void interpret(String line) throws Exception {
				String [] parts = line.split("\\s*[,]\\s*");
				KeyValue kv_id = new KeyValue(parts[0]);
				KeyValue kv_md5 = new KeyValue(parts[1]);
				KeyValue kv_deleted = new KeyValue(parts[2]);
				TaskIdInfo info = new TaskIdInfo();
				info.task_id = kv_id.value;
				info.md5 = kv_md5.value;
				info.deleted = (kv_deleted.equals("T")) ? true : false;
				info.local = false;
				info.updated_locally = false;
				result.put(info.task_id, info);
			}
		});
		
		if (!ok) {
			// error message already set
			return result;
		}
		
		// local ids
		CdTasks tasks = _factory.tasks();
		int i;
		for(i = 0; i < tasks.size(); ++i) {
			CdTask task = tasks.get(i);
			if (!result.containsKey(task.id())) {
				TaskIdInfo info = new TaskIdInfo();
				info.task_id = task.id();
				info.md5 = task.getMd5();
				info.deleted = false;
				info.local = true;
				info.updated_locally = task.getUpdated();
				result.put(info.task_id, info);
			}
		}
			
		// deleted ids
		CdDeletedTasks deleted = _factory.deletedTasks();
		for(i = 0; i < deleted.size(); ++i) {
			String id = deleted.get(i);
			if (!result.containsKey(id)) { // which would be a case where the server had this key
				TaskIdInfo info = new TaskIdInfo();
				info.task_id = id;
				info.deleted = true;
				info.local = true;
				info.md5 = "";
				info.updated_locally = false;
			}
		}
		
		return result;
	}
	
	
	private boolean fetch(String cmd, Hashtable<String, Object> dict, FetchInterpreter fi) throws Exception {
		URL requrl = new URL(_server);
		Hashtable<String, Object> pars = (Hashtable<String, Object>) dict.clone();
		pars.put("command", cmd);
		pars.put("user", _user);
		pars.put("password", _pass);
		String parameters = urlencode(pars); 
		HttpURLConnection conn = (HttpURLConnection) requrl.openConnection();
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		conn.setRequestProperty("Content-Length", Integer.toString(parameters.getBytes("UTF-8").length));
		conn.setRequestProperty("Content-Language", "en-US");
		conn.setUseCaches(false);
		conn.setDoInput(true);
		conn.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
		wr.writeBytes(parameters);
		wr.flush();
		wr.close();
		
		InputStream in = conn.getInputStream();
		BufferedReader rd = new BufferedReader(new InputStreamReader(in));
		String line;
		boolean ok = true;
		while ((line = rd.readLine()) != null) {
			line = line.trim();
			if (line.isEmpty()) {
				logger.debug(line);
			} else if (line.substring(0,1).equals("#")) {
				logger.debug(line);
			} else if (line.equals("OK")) {
				ok = true;
				logger.debug(line);
			} else if (line.equals("NOK")) {
				ok = false;
				logger.debug(line);
				_last_error = rd.readLine();
				logger.debug(_last_error);
			} else {
				fi.interpret(line);;
			}
		}
		rd.close();
		
		conn.disconnect();
		
		return ok;
	}
	
	
	private void processIds(Hashtable<String, TaskIdInfo> ids) throws NDbmException {
	    // Precondition: called with al ids: local and on server
	    //
	    // For each id, check if:
	    // if id is deleted
	    // if id is locally deleted
	    // do nothing
	    // else
	    // delete locally (also if it doesn't exist anywhere)
	    // else if id does not exist
	    // if id exists in deleted table
	    // delete on server
	    // else
	    // fetch and add
	    // else if md5 unequal
	    // if locally changed
	    // update to server (// preferred to fetching from server)
	    // else
	    // fetch and update from server
	    // else
	    // do nothing
		
		Enumeration<String> en = ids.keys();
		while (en.hasMoreElements()) {
			String key = en.nextElement();
			TaskIdInfo info = ids.get(key);
			
			if (info.local) {
				if (info.deleted) {
					this.deleteTaskOnServer(info);
				} else {
					this.updateTaskToServer(info);
				}
			} else {
				if (info.deleted) {
					if (this.taskIsDeletedLocally(info)) {
						// ok, do nothing
					} else {
						this.deleteTaskLocally(info);
					}
				} else {
					if (this.taskExistsLocally(info)) {
						if (this.getLocalMd5(info).equals(info.md5)) {
							// do nothing
						} else {
							if (this.taskIdUpdatedLocally(info)) {
								this.updateTaskToServer(info);
							} else {
								this.updateTaskFromServer(info);
							}
						}
					} else {
						if (this.taskIsDeletedLocally(info)) {
							this.deleteTaskOnServer(info);
						} else {
							this.insertTaskFromServer(info);
						}
					}
				}
			}
		}
	}
	
	private void insertTaskFromServer(TaskIdInfo info) {
		// TODO Auto-generated method stub
		
	}

	private void updateTaskFromServer(TaskIdInfo info) {
		// TODO Auto-generated method stub
		
	}

	private boolean taskIdUpdatedLocally(TaskIdInfo info) {
		// TODO Auto-generated method stub
		return false;
	}

	private void updateTaskToServer(TaskIdInfo info) {
		// TODO Auto-generated method stub
		
	}

	private void deleteTaskOnServer(TaskIdInfo info) {
		// TODO Auto-generated method stub
		
	}

	private String getLocalMd5(TaskIdInfo info) {
		CdTasks tasks = _factory.tasks();
		CdTask t = tasks.getTask(info.task_id);
		if (t != null) {
			return t.getMd5();
		} else {
			return "";
		}
	}

	private boolean taskExistsLocally(TaskIdInfo info) {
		CdTasks tasks = _factory.tasks();
		return tasks.getTask(info.task_id) != null;
	}

	private void deleteTaskLocally(TaskIdInfo info) throws NDbmException {
		CdDeletedTasks d = _factory.deletedTasks();
		CdTasks t = _factory.tasks();
		_factory.begin();
		t.removeTask(info.task_id);
		d.add(info.task_id);
		_factory.commit();
	}

	private boolean taskIsDeletedLocally(TaskIdInfo info) {
		CdDeletedTasks d = _factory.deletedTasks();
		return d.containsTask(info.task_id);
	}


	private String urlencode(Hashtable<String, Object> dict) throws Exception {
		Enumeration<String> en = dict.keys();
		StringBuilder b = new StringBuilder();
		String delim = "";
		while (en.hasMoreElements()) {
			String key = en.nextElement();
			String value = dict.get(key).toString();
			b.append(delim);
			b.append(key);
			b.append("=");
			b.append(URLEncoder.encode(value, "UTF-8"));
			delim = "&";
		}
		return b.toString();
	}

}
