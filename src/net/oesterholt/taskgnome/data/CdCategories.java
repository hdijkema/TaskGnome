package net.oesterholt.taskgnome.data;

import java.util.Collections;
import java.util.Comparator;
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
	
	public CdCategory remove(CdCategory cat) throws NDbmException {
		CdCategory c = super.get(cat.id());
		if (c != null) {
			super.remove(cat.id());
			write();
		} 
		return c;
	}
	
	public void clear() {
		super.clear();
		try {
			write();
		} catch (Exception e) {
			e.printStackTrace();
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
				Id id = new Id(_id.dbm(),it.next());
				CdCategory cat = new CdCategory(_id.dbm(), id);
				super.put(id.id(), cat);
			}
		}
	}
	
	public Vector<CdCategory> getCategories() {
		Vector<CdCategory> cats = new Vector<CdCategory>();
		Enumeration<CdCategory> en = super.elements();
		while (en.hasMoreElements()) {
			cats.add(en.nextElement());
		}
		Collections.sort(cats,new Comparator<CdCategory>() {
			public int compare(CdCategory o1, CdCategory o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});
		return cats;
	}
	
}
