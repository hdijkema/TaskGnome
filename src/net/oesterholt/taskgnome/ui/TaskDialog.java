package net.oesterholt.taskgnome.ui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.Date;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.ListCellRenderer;
import javax.swing.border.Border;

import net.miginfocom.swing.MigLayout;
import net.oesterholt.taskgnome.data.CdCategories;
import net.oesterholt.taskgnome.data.CdCategory;
import net.oesterholt.taskgnome.data.CdTask;
import net.oesterholt.taskgnome.utils.DateUtils;
import net.oesterholt.taskgnome.utils.Swing;

import org.jdesktop.swingx.JXDatePicker;
import org.jdesktop.swingx.JXTextField;

public class TaskDialog extends JDialog {
	
	private static final long serialVersionUID = 1L;
	
	private JXTextField  			_name;
	private JRadioButton 			_prio[];
	private JRadioButton			_active, _finished;
	private JComboBox<CdCategory>   _category;
	private JTextArea    			_more_info;
	private JXDatePicker 			_due;
	
	private boolean _canceled;
	
	public void setName(String n) {
		_name.setText(n);
	}
	
	public String getName() {
		return _name.getText();
	}
	
	public void setPrio(int p) {
		if (p == 9) { p = 0; }
		if (p > 4) { p = 0; }
		int i;
		for(i = 0; i < _prio.length; ++i) {
			_prio[i].setSelected(i == p);
		}
	}
	
	public int getPrio() {
		int i;
		for(i = 0;i < _prio.length && !_prio[i].isSelected(); i++);
		return (i == 0) ? 9 : i;
	}
	
	public void setMoreInfo(String mi) {
		_more_info.setText(mi);
	}
	
	public String getMoreInfo() {
		return _more_info.getText();
	}
	
	public void setCategory(CdCategory c) {
		_category.setSelectedItem(c);
	}
	
	public CdCategory getCategory() {
		return (CdCategory) _category.getSelectedItem();
	}
	
	public void setDue(Date d) {
		_due.setDate(d);
	}
	
	public Date getDue() {
		return _due.getDate();
	}
	
	public void setKind(int k) {
		if (CdTask.KIND_ACTIVE == k) {
			_active.setSelected(true);
		} else {
			_finished.setSelected(true);
		}
	}
	
	public int getKind() {
		if (_active.isSelected()) {
			return CdTask.KIND_ACTIVE;
		} else {
			return CdTask.KIND_FINISHED;
		}
	}
	
	public boolean ok() {
		return !_canceled;
	}
	
	public void close() {
		super.setVisible(false);
	}
	
	@SuppressWarnings("serial")
	public TaskDialog(JFrame window, CdCategories cats) {
		super(window);
		_name = new JXTextField();
		_prio = new JRadioButton[5];
		
		String [] labels = { "-", "1", "2", "3", "4" };
		
		ButtonGroup grp = new ButtonGroup();
		int i;
		for(i = 0; i < labels.length; i++) {
			String s = "-";
			if (i > 0) { s = String.valueOf(i); }
			_prio[i] = new JRadioButton(new AbstractAction(s) {
				public void actionPerformed(ActionEvent e) {
				}
			});
			grp.add(_prio[i]);
		}

		_prio[0].setSelected(true);
		_category = new CategoryComboBox(cats);
		_category.setSelectedIndex(0);
		_more_info = new JTextArea();
		_due = new JXDatePicker();
		_due.setDate(DateUtils.today());
		_due.setFormats(DateUtils.format());
		
		_active = new JRadioButton(new AbstractAction("Active") {
			public void actionPerformed(ActionEvent e) {
			}
		});
		_finished = new JRadioButton(new AbstractAction("Finished") {
			public void actionPerformed(ActionEvent e) {
			}
		});
		ButtonGroup grp1 = new ButtonGroup();
		grp1.add(_active);
		grp1.add(_finished);
		_active.setSelected(true);
		
		
		// Layout
		
		JPanel panel = new JPanel();
		panel.setLayout(new MigLayout("fill"));
		panel.add(new JLabel("Task name:"), "");panel.add(_name,"span, growx, wrap");
		panel.add(new JLabel("Priority:"), "");
		for(i = 0; i < _prio.length; ++i) {
			panel.add(_prio[i], (i == (_prio.length - 1)) ? "wrap" : "");
		}
		panel.add(new JLabel("Due:"), "");panel.add(_due, "span, growx, wrap");
		panel.add(new JLabel("Category:"), "");panel.add(_category, "span, growx, wrap");
		panel.add(_active);panel.add(_finished,"wrap");
		JScrollPane spane = new JScrollPane(_more_info, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		panel.add(new JLabel("More info:"),"");panel.add(spane, "span, growx, growy, hmin 200, wrap");
		
		JPanel dlgPanel = new JPanel();
		dlgPanel.setLayout(new MigLayout("fill"));;
		dlgPanel.add(panel, "growx, growy, wrap");
		dlgPanel.add(new JSeparator(),"growx,span,wrap");
		dlgPanel.add(new JButton(new AbstractAction("Cancel") {
			public void actionPerformed(ActionEvent e) {
				_canceled = true;
				close();
			}
		}),"right");
		dlgPanel.add(new JButton(new AbstractAction("Ok") {
			public void actionPerformed(ActionEvent e) {
				_canceled = false;
				close();
			}
		}), "right, wrap");

		
		super.add(dlgPanel);
		super.pack();
		super.setModal(true);
		Swing.centerOnParent(this, window);
	}
	
	

}
