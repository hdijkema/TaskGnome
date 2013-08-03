package net.oesterholt.taskgnome.ui;

import java.awt.Component;
import java.text.SimpleDateFormat;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class PriorityCellRenderer extends DefaultTableCellRenderer {
	
	private static final long serialVersionUID = 1L;

	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		if (value instanceof Integer) {
			if (((Integer) value).equals(0) || (((Integer) value).equals(9))) {
				value = "-";
			}
		}
		return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
	}

}
