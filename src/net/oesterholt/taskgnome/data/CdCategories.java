package net.oesterholt.taskgnome.data;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import net.oesterholt.jndbm.NDbm2;
import net.oesterholt.jndbm2.exceptions.NDbmException;

public class CdCategories extends Hashtable<String, CdCategory> {
	
	private static final long serialVersionUID = 1L;
	
	private Id _id;
	private NDbm2 _dbm;
	
	public CdCategories(NDbm2 dbm) throws NDbmException {
		_id = new Id(dbm, "categories");
		_dbm = dbm;
		read();
	}
	
	public NDbm2 dbm() {
		return _dbm;
	}
	
	public void put(CdCategory cat) throws NDbmException {
		if (!super.containsKey(cat.id())) {
			super.put(cat.id(), cat);
			write();
		}
	} 
	
	public CdCategory get(String id) {
		return super.get(id);
	}
	
	protected void write() throws NDbmException {
		Vector<String> cat_ids = new Vector<String>();
		Enumeration<CdCategory> en = super.elements();
		while (en.hasMoreElements()) {
			CdCategory cat = en.nextElement();
			cat_ids.add(cat.id());
		}
		_id.dbm().putVectorOfString(_id.id(), cat_ids);
	}
	
	protected void read() throws NDbmException {
		super.clear();
		Vector<String> cat_ids = _id.dbm().getVectorOfString(_id.id());
		if (cat_ids!=null) {
			Iterator<String> it = cat_ids.iterator();
			while (it.hasNext()) {
				String id = it.next();
				CdCategory cat = new CdCategory(_id.dbm(), id);
				super.put(id, cat);
			}
		}
	}
	
}
