package net.oesterholt.taskgnome.utils;

import java.util.prefs.Preferences;

import net.oesterholt.taskgnome.TaskGnome;

public class Config {
	
	Preferences prefs;
	
	static public String Version() {
		return "1.0";
	}
	
	static public String Author() {
		return "Hans Oesterholt";
	}
	
	static public String Copyright() {
		return "v"+Version()+" - (c) 2013 " + Author();
	}
	
	public Config() {
		prefs = Preferences.userNodeForPackage(TaskGnome.class);
	}
	
	public String getUserId() {
		return prefs.get("userid", "no@one.com");
		
	}
	
	public void setUserId(String id) {
		prefs.put("userid", id);
		
	}
	
	public String getPassword() {
		return prefs.get("pass", "");
	}
	
	public void setPassword(String p) {
		prefs.put("pass", p);
	}

}
