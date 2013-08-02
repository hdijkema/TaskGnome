package net.oesterholt.taskgnome.data;

import java.util.UUID;
import net.oesterholt.jndbm.NDbm2;
import net.oesterholt.jndbm2.exceptions.NDbmException;

public class Id extends Object {
	
	private String _id;
	private NDbm2  _dbm;
	
	private String makeId() {
		UUID u=UUID.randomUUID();
		String id=u.toString();
		return id;
	}
	
	public String id(String attribute) {
		return _id+":"+attribute;
	}
	
	public void begin() throws NDbmException {
		dbm().begin();
	}
	
	public void commit() throws NDbmException {
		dbm().commit();
	}
	
	public String id() {
		return _id;
	}
	
	public NDbm2 dbm() {
		return _dbm;
	}
	
	public Id(NDbm2 dbm) {
		_dbm = dbm;
		_id = makeId();
	}
	
	public Id(NDbm2 dbm, Id id) {
		_dbm = dbm;
		_id = id._id;
	}
	
	public Id(NDbm2 dbm, String rootId) {
		_dbm = dbm;
		_id = rootId;
	}

}
