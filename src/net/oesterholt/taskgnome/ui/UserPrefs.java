package net.oesterholt.taskgnome.ui;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;

import java.awt.Color;

import net.miginfocom.swing.MigLayout;
import net.oesterholt.taskgnome.data.DataFactory;
import net.oesterholt.taskgnome.sync.Synchronizer;
import net.oesterholt.taskgnome.utils.Swing;
import net.oesterholt.taskgnome.utils.Config;

import org.jdesktop.swingx.JXTextField;

public class UserPrefs extends JDialog {
	
	private static final long serialVersionUID = 1L;
	
	private JXTextField    _name;
	private JPasswordField _pass;
	private JLabel		   _result;
	private boolean _ok = true;
	
	public boolean ok() {
		return _ok;
	}
	
	@SuppressWarnings("serial")
	public UserPrefs(JFrame window, final DataFactory f) {
		super(window);
		_name = new JXTextField();
		_pass = new JPasswordField();
		_result = new JLabel("-");
		_result.setBorder(BorderFactory.createEtchedBorder());
		
		JPanel pan = new JPanel();
		pan.setLayout(new MigLayout("fill"));;
		
		pan.add(new JLabel("eMail : "));
		pan.add(_name, "span, growx, wrap");
		pan.add(new JLabel("Password : "));
		pan.add(_pass, "span, growx, wmin 300, wrap");
		
		pan.add(_result, "span, growx, wrap");
		_result.setHorizontalTextPosition(SwingConstants.CENTER);
		
		pan.add(new JSeparator(JSeparator.HORIZONTAL), "growx, span");
		
		Config cfg = new Config();
		_name.setText(cfg.getUserId());
		_pass.setText(cfg.getPassword());
		
		pan.add(new JButton(new AbstractAction("Check") {
			public void actionPerformed(ActionEvent e) {

				final Config cfg = new Config();
				final String cuser = cfg.getUserId();
				final String cpass = cfg.getPassword();
				cfg.setUserId(_name.getText().trim());
				char[] c = _pass.getPassword();
				String p = new String(c);
				cfg.setPassword(p.trim());
				
				_result.setForeground(Color.gray);
				_result.setText("Checking...");
				final Synchronizer S = new Synchronizer(f);
				S.checkAccount(new Runnable() {
					public void run() {
						try {
							Thread.sleep(500);
						} catch (InterruptedException e) {
							e.printStackTrace();
						};
						if (S.getErrorMessage() != null) {
							_result.setForeground(Color.red);;
							_result.setText(S.getErrorMessage());
						} else {
							_result.setForeground(Color.blue);;
							_result.setText("Authorization OK");
						}
						cfg.setPassword(cpass);;
						cfg.setUserId(cuser);
					}
				});
			}
		}), "left");
		
		pan.add(new JButton(new AbstractAction("Create") {
			public void actionPerformed(ActionEvent e) {
				final Config cfg = new Config();
				final String cuser = cfg.getUserId();
				final String cpass = cfg.getPassword();
				cfg.setUserId(_name.getText().trim());
				char[] c = _pass.getPassword();
				String p = new String(c);
				cfg.setPassword(p.trim());
				
				_result.setForeground(Color.gray);
				_result.setText("Checking...");
				final Synchronizer S = new Synchronizer(f);
				S.createAccount(new Runnable() {
					public void run() {
						try {
							Thread.sleep(500);
						} catch (InterruptedException e) {
							e.printStackTrace();
						};
						if (S.getErrorMessage() != null) {
							_result.setForeground(Color.red);;
							_result.setText(S.getErrorMessage());
						} else {
							_result.setForeground(Color.blue);;
							_result.setText("Authorization OK");
						}
						cfg.setPassword(cpass);;
						cfg.setUserId(cuser);
					}
				});
			}
		}), "left");
		
		JPanel co = new JPanel();
		
		co.add(new JButton(new AbstractAction("Cancel") {
			public void actionPerformed(ActionEvent e) {
				_ok = false;
				setVisible(false);
			}
		}));
		
		co.add(new JButton(new AbstractAction("Ok") {
			public void actionPerformed(ActionEvent e) {
				Config cfg = new Config();
				cfg.setUserId(_name.getText().trim());
				char[] c = _pass.getPassword();
				String p = new String(c);
				cfg.setPassword(p.trim());

				_ok = true;
				setVisible(false);
			}
		}));
		
		pan.add(co, "right, wrap");
		
		super.setTitle("User Preferences");
		super.add(pan);
		super.pack();
		super.setModal(true);
		Swing.centerOnParent(this, window);
	}
		
		
	
		
	
	
	
	

}
