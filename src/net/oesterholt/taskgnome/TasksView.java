package net.oesterholt.taskgnome;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import net.miginfocom.swing.MigLayout;
import net.oesterholt.JXTwoLevelSplitTable;
import net.oesterholt.splittable.AbstractSplitTableModel.ColumnWidthListener;
import net.oesterholt.taskgnome.data.DataFactory;

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
				_table.rightTable().getColumnExt(col).setPreferredWidth(width);
			}
		});
		
		super.setLayout(new MigLayout("insets 1,fill"));
		
		this.add(_table, "growx, growy");
		
	}
	

}
