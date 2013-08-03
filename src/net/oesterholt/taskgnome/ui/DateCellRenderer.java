package net.oesterholt.taskgnome.ui;

import java.awt.Component;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class DateCellRenderer extends DefaultTableCellRenderer {

	private static final long serialVersionUID = 1L;
	
	SimpleDateFormat _format = new SimpleDateFormat("dd-MM-yyyy");

	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		if (value instanceof Date) {
			value = _format.format((Date) value);
		}
		return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
	}
}
