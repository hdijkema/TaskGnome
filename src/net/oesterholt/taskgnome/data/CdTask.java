package net.oesterholt.taskgnome.data;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.codec.digest.DigestUtils;

import net.oesterholt.jndbm.NDbm2;
import net.oesterholt.jndbm2.exceptions.NDbmException;

public class CdTask extends Id {
	
	public static int KIND_ACTIVE = 1;
	public static int KIND_FINISHED = 2;
	
	private String 		_name = "";
	private String 		_more_info = "";
	private int    		_priority = 9;
	private CdCategory 	_category = null;
	private Date		_due = new Date();
	private int         _kind = KIND_ACTIVE;
	
	private CdCategories _categories;
	
	private String		_md5;
	private boolean		_updated_locally; 
	
	private void calcMd5()
	{
		//NSString stringWithFormat: @"%@%@%@%@%@%@",[task name],cat_id,dt,[task more_info],[task priority],[task kind]];
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		String dt = format.format(_due);
		CdCategory c = getCategory();
		String cs;
		if (c == null) { cs = ""; }
		else { cs = c.id(); }
		String input = getName()+cs+dt+_more_info+_priority+_kind;
		_md5 = DigestUtils.md5Hex(input);
	}

	public CdTask(NDbm2 dbm) throws NDbmException {
		super(dbm);
		dbm().begin();
		setName("");
		setMoreInfo("");
		setPriority(9);
		setCategory(null);
		setKind(KIND_ACTIVE);
		setDue(new Date());
		dbm().commit();
	}
	
	public CdTask(CdCategories cats, NDbm2 dbm, Id id) throws NDbmException {
		super(dbm,id);
		_categories = cats;
		read();
	}
	
	protected void read() throws NDbmException {
		dbm().begin();
		_name = dbm().getStr(id("name"));
		_more_info = dbm().getStr(id("more_info"));
		_priority = dbm().getInt(id(("priority")));
		String cat_id = dbm().getStr(id("category"));
		if (cat_id == "null") {
			_category = null;
		} else {
			_category = _categories.get(cat_id);
		}
		_due = dbm().getDate(id("due"));
		_kind = dbm().getInt(id("kind"));
		_updated_locally = dbm().getBoolean(id("updated_locally"));
		calcMd5();
		dbm().commit();
	}
	
	public void setUpdated(boolean u) throws NDbmException {
		super.begin();
		_updated_locally = u;
		dbm().putBoolean(id("updated_locally"), _updated_locally);
		super.commit();
	}
	
	public void setName(String name) throws NDbmException {
		dbm().begin();
		_name = name;
		calcMd5();
		dbm().putStr(id("name"),_name);
		setUpdated(true);
		dbm().commit();
	}
	
	public String getName() {
		return _name;
	}
	
	public void setCategory(CdCategory cat) throws NDbmException  {
		dbm().begin();
		_category = cat;
		calcMd5();
		String cat_id = (_category == null) ? "null" : _category.id();
		dbm().putStr(id("category"), cat_id);
		setUpdated(true);
		dbm().commit();
	}
	
	public CdCategory getCategory() {
		return _category;
	}
	
	public void setPriority(int priority) throws NDbmException {
		dbm().begin();
		_priority = priority;
		calcMd5();
		dbm().putInt(id("priority"), _priority);
		setUpdated(true);
		dbm().commit();
	}
	
	public int getPriority() {
		return _priority;
	}
	
	public void setMoreInfo(String more_info) throws NDbmException {
		super.begin();
		_more_info = more_info;
		calcMd5();
		dbm().putStr(id("more_info"), _more_info);
		setUpdated(true);
		super.commit();
	}
	
	public String getMoreInfo() {
		return _more_info;
	}
	
	public void setKind(int kind) throws NDbmException {
		super.begin();
		_kind = kind;
		calcMd5();
		dbm().putInt(id("kind"), _kind);
		setUpdated(true);
		super.commit();
	}
	
	public int getKind() {
		return _kind;
	}
	
	public void setDue(Date due) throws NDbmException {
		super.begin();
		_due = due;
		calcMd5();
		dbm().putDate(id("due"), _due);
		setUpdated(true);
		super.commit();
	}
	
	public Date getDue() {
		return _due;
	}
	
	public String getMd5() {
		return _md5;
	}
	
	public boolean isEqualMd5(CdTask task) {
		return task.getMd5().equals(getMd5());
	}

	public void remove() throws NDbmException {
		super.begin();
		dbm().remove(id("name"));
		dbm().remove(id("updated_locally"));
		dbm().remove(id("due"));
		dbm().remove(id("kind"));
		dbm().remove(id("priority"));
		dbm().remove(id("kind"));
		dbm().remove(id("category"));
	}
	
}
