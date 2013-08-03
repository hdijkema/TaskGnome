package net.oesterholt.taskgnome;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.jdesktop.swingx.JXDatePicker;
import org.jdesktop.swingx.autocomplete.ComboBoxCellEditor;
import org.jdesktop.swingx.decorator.ColorHighlighter;
import org.jdesktop.swingx.decorator.ComponentAdapter;
import org.jdesktop.swingx.decorator.FontHighlighter;
import org.jdesktop.swingx.decorator.HighlightPredicate;
import org.jdesktop.swingx.decorator.Highlighter;
import org.jdesktop.swingx.table.DatePickerCellEditor;
import org.jdesktop.swingx.table.TableColumnExt;

import net.miginfocom.swing.MigLayout;
import net.oesterholt.JXTwoLevelSplitTable;
import net.oesterholt.splittable.AbstractTwoLevelSplitTableModel;
import net.oesterholt.splittable.AbstractSplitTableModel.ColumnWidthListener;
import net.oesterholt.taskgnome.data.DataFactory;
import net.oesterholt.taskgnome.ui.CategoryCellRenderer;
import net.oesterholt.taskgnome.ui.CategoryComboBox;
import net.oesterholt.taskgnome.ui.DateCellRenderer;
import net.oesterholt.taskgnome.ui.PriorityCellRenderer;
import net.oesterholt.taskgnome.ui.PriorityComboBox;
import net.oesterholt.taskgnome.utils.DateUtils;

public class TasksView extends JPanel {
	
	private static final long serialVersionUID = 1L;
	
	private TasksController 		_ctrl;
	private JXTwoLevelSplitTable 	_table; 
	
	public TasksView(TasksController ctrl) {
		super();
		_ctrl = ctrl;
		
		_table = new JXTwoLevelSplitTable("tasks",
										  _ctrl, 
										  JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, 
										  JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED
										  );
		_table.addSelectionListener(ctrl);
		
		_ctrl.addColumnWidthListener(new ColumnWidthListener() {
			public void prefferedWidthForColumn(int col, int width) {
				if (col == 0) {
					_table.getColumnExt(col, true).setPreferredWidth(width);
				} else {
					_table.getColumnExt(col - 1, false).setPreferredWidth(width);
				}
			}
		});
		
		ComboBoxCellEditor cat_editor = new ComboBoxCellEditor(new CategoryComboBox(_ctrl.dataFactory().categories()));
		TableColumnExt tc_cat = _table.getColumnExt(2, false);
		tc_cat.setCellEditor(cat_editor);
		tc_cat.setCellRenderer(new CategoryCellRenderer());
		
		ComboBoxCellEditor prio_editor = new ComboBoxCellEditor(new PriorityComboBox());
		TableColumnExt tc_prio = _table.getColumnExt(0, true);
		tc_prio.setCellEditor(prio_editor);
		tc_prio.setCellRenderer(new PriorityCellRenderer());
		
		DatePickerCellEditor due_editor = new DatePickerCellEditor(DateUtils.format());
		TableColumnExt tc_due = _table.getColumnExt(1, false);
		tc_due.setCellEditor(due_editor);
		tc_due.setCellRenderer(new DateCellRenderer());

		_table.addHighlighter(new ColorHighlighter(new HighlightPredicate() {
			public boolean isHighlighted(Component c, ComponentAdapter a) {
				return _ctrl.isActive() && !_ctrl.isNodeIndex(a.row) && _ctrl.isToday(_ctrl.getNodeIndex(a.row));
			}
		}, null, Color.blue), false);

		_table.addHighlighter(new ColorHighlighter(new HighlightPredicate() {
			public boolean isHighlighted(Component c, ComponentAdapter a) {
				return _ctrl.isActive() && !_ctrl.isNodeIndex(a.row) && _ctrl.isPast(_ctrl.getNodeIndex(a.row));
			}
		}, null, Color.red), false);
		
		Color details = new Color(223, 248, 230);
		_table.addHighlighter(new ColorHighlighter(new HighlightPredicate() {
			public boolean isHighlighted(Component c, ComponentAdapter a) {
				AbstractTwoLevelSplitTableModel.CNodeIndex idx = _ctrl.getCNodeIndex(a.row, a.column);
				return !_ctrl.isNodeIndex(a.row) && _ctrl.hasDetails(idx.nodeIndex, idx.nodeRow);
			}
		}, details, null), false);
		_table.addHighlighter(new ColorHighlighter(new HighlightPredicate() {
			public boolean isHighlighted(Component c, ComponentAdapter a) {
				AbstractTwoLevelSplitTableModel.CNodeIndex idx = _ctrl.getCNodeIndex(a.row, a.column);
				return !_ctrl.isNodeIndex(a.row) && _ctrl.hasDetails(idx.nodeIndex, idx.nodeRow);
			}
		}, details, null), true);

		_table.addHighlighter(new ColorHighlighter(new HighlightPredicate() {
			public boolean isHighlighted(Component c, ComponentAdapter a) {
				AbstractTwoLevelSplitTableModel.CNodeIndex idx = _ctrl.getCNodeIndex(a.row, a.column);
				return !_ctrl.isActive();
			}
		}, null, Color.gray), false);

		super.setLayout(new MigLayout("insets 0,fill"));
		
		this.add(_table, "growx, growy");
		
	}
	

}
