package net.oesterholt.taskgnome.ui;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JComboBox;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import net.oesterholt.taskgnome.data.CdCategories;
import net.oesterholt.taskgnome.data.CdCategories.Listener;
import net.oesterholt.taskgnome.data.CdCategory;

class Model implements ComboBoxModel<CdCategory>, Listener {

	private CdCategories 			_cats;
	private Vector<CdCategory> 		_vcats;
	private Set<ListDataListener> 	_listeners;
	private int						_selected;

	public int getSize() {
		return _vcats.size();
	}

	public CdCategory getElementAt(int index) {
		return _vcats.get(index);
	}

	public void addListDataListener(ListDataListener l) {
		_listeners.add(l);
	}

	public void removeListDataListener(ListDataListener l) {
		_listeners.remove(l);
	}

	public void setSelectedItem(Object anItem) {
		int i;
		for(i = 0; i < _vcats.size() && _vcats.get(i)!=anItem; i++);
		if (i == _vcats.size()) {
			_selected = -1;
		} else {
			_selected = i;
		}
	}

	public Object getSelectedItem() {
		if (_selected < 0) { return null; }
		else { return _vcats.get(_selected); }
	}

	public Model(CdCategories cats) {
		_cats = cats;
		_vcats = _cats.getCategories();
		_listeners = new HashSet<ListDataListener>();
		_selected = -1;
		_cats.addListener(this);
	}
	
	protected void finalize() throws Throwable {
		_cats.removeListener(this);
	}

	public void changed() {
		_vcats = _cats.getCategories();
		Iterator<ListDataListener> it = _listeners.iterator();
		ListDataEvent e = new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, 0, _vcats.size());
		while (it.hasNext()) {
			it.next().contentsChanged(e);
		}
	}
}

public class CategoryComboBox extends JComboBox<CdCategory> {

	private static final long serialVersionUID = 1L;
	
	public CategoryComboBox(CdCategories cats) {
		super(new Model(cats));
		this.setRenderer(new CategoryListRenderer());
	}

}
