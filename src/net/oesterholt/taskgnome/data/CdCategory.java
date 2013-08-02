package net.oesterholt.taskgnome.data;

import org.apache.commons.codec.digest.DigestUtils;
import net.oesterholt.jndbm.NDbm2;
import net.oesterholt.jndbm2.exceptions.NDbmException;

public class CdCategory extends Id {
	
	String  	_name;
	String  	_md5;
	boolean 	_updated_locally;

	public CdCategory(NDbm2 dbm, Id id)  throws NDbmException
	{
		super(dbm, id);
		read();
	}

	public CdCategory(NDbm2 dbm, String name) throws NDbmException {
		super(dbm);
		setName(name);
	}

	public CdCategory(NDbm2 dbm, String name, String forceId) throws NDbmException {
		super(dbm, forceId);
		setName(name);
	}

	protected void read() throws NDbmException {
		dbm().begin();
		_name = dbm().getStr(id("name"));
		_updated_locally = dbm().getBoolean(id("updated_locally"));
		calcMd5();
		dbm().commit();
	}
	
	public String getName() {
		return _name;
	}
	
	private void calcMd5() {
		_md5 = DigestUtils.md5Hex(_name);
	}
	
	public void setName(String name) throws NDbmException {
		_name = name;
		calcMd5();
		_updated_locally = true;
		dbm().begin();
		dbm().putStr(id("name"), _name);
		dbm().putBoolean(id("updated_locally"), _updated_locally);
		dbm().commit();
	}
	
	public String getMd5() {
		return _md5;
	}
	
	public boolean isUpdatedLocally() {
		return _updated_locally;
	}
	
	public void remove() throws NDbmException {
		dbm().begin();
		dbm().remove(id("name"));
		dbm().remove(id("updated_locally"));
		dbm().commit();
	}
	
}
