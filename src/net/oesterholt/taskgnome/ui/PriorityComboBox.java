package net.oesterholt.taskgnome.ui;

import javax.swing.JComboBox;

public class PriorityComboBox extends JComboBox<Integer> {
	
	private static final long serialVersionUID = 1L;

	public int getPrio() {
		int p = super.getSelectedIndex();
		if (p == 0) { return 9; }
		else { return p; }
		
	}
	
	public void setPrio(int p) {
		if (p == 9) { super.setSelectedIndex(0); }
		else { super.setSelectedIndex(p); }
	}
	
	public PriorityComboBox() {
		super(new Integer[] {0, 1, 2, 3, 4});
		super.setSelectedIndex(0);
		this.setRenderer(new PriorityListRenderer());
	}
	

}
