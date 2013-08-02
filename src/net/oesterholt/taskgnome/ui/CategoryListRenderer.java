package net.oesterholt.taskgnome.ui;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

import net.oesterholt.taskgnome.data.CdCategory;

public class CategoryListRenderer extends DefaultListCellRenderer {

	private static final long serialVersionUID = 1L;
	
    @SuppressWarnings("rawtypes")
	public Component getListCellRendererComponent( JList list, Object value, int index, boolean isSelected, boolean cellHasFocus ) {
        if( value instanceof CdCategory ) {
            value = ((CdCategory) value).getName();     
        }
        return super.getListCellRendererComponent( list, value , index, isSelected, cellHasFocus);
    }	

}
