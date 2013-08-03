package net.oesterholt.taskgnome.ui;

import java.awt.Component;
import java.text.SimpleDateFormat;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import net.oesterholt.taskgnome.data.CdCategory;

public class CategoryCellRenderer extends DefaultTableCellRenderer {
	
	private static final long serialVersionUID = 1L;

	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		if (value instanceof CdCategory) {
			value = ((CdCategory) value).getName();
		}
		return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
	}

}

