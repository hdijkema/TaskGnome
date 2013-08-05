/*
 * $Id: zc3Logger.java,v 1.2 2009/04/15 21:22:46 cvs Exp $
 * 
 * This source code is copyright of Hans Oesterholt-Dijkema.
 * © 2008-2009 Hans Oesterholt-Dijkema. All rights reserved.
 * 
 * This source code is property of it's author: Hans Oesterholt-Dijkema.
 * Nothing of this code may be copied, (re)used or multiplied without
 * permission of the author. 
*/
package net.oesterholt.taskgnome.utils;

import java.util.Date;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggerFactory;

class Factory implements LoggerFactory {
	
	   Factory() { }
	   
	   public org.apache.log4j.Logger makeNewLoggerInstance(String name) {
	       return new TgLogger(name);
	   }    
}


public class TgLogger extends Logger {

	static String FQCN = TgLogger.class.getName() + ".";
	private static LoggerFactory factory = new Factory();

	public static Object LOGMEM=new Object();
	
	public static Logger getLogger(Class c) {
		return Logger.getLogger(c.getName(),factory);
	}

	public static Logger getLogger(String name) {
		return Logger.getLogger(name,factory);
	}
	
	String getmsg(Exception E) {
		String msg=E.getMessage();
		if (msg==null) {
			msg=E.getLocalizedMessage();
			if (msg==null) {
				msg="";
			}
		}
		return msg;
	}
	
	// Exception handlers
	
	public void _debug(Exception E) {
		super.log(FQCN,Level.DEBUG, getmsg(E), E);  
	}
	
	public void _info(Exception E) {
		super.log(FQCN,Level.INFO,getmsg(E),E);
	}

	public void _warn(Exception E) {
		super.log(FQCN,Level.WARN,getmsg(E),E);
	}
	
	public void _error(Exception E) {
		super.log(FQCN,Level.ERROR,getmsg(E),E);
	}
	
	public void _fatal(Exception E) {
		super.log(FQCN,Level.FATAL,getmsg(E),E);
	}
	
	// Generic exception handlers
	
	public void debug(Object o) {
		if (o instanceof Exception) {
			_debug((Exception) o);
		} else if (o==LOGMEM){
			super.log(FQCN,Level.DEBUG,meminfo(),null);
		} else {
			super.log(FQCN,Level.DEBUG, o, null);    
		}
	}

	public void info(Object o) {
		if (o instanceof Exception) {
			_info((Exception) o);
		} else if (o==LOGMEM){
			super.log(FQCN,Level.INFO,meminfo(),null);
		} else {
			super.log(FQCN,Level.INFO,o,null);
		}
	}
	
	public void warn(Object o) {
		if (o instanceof Exception) {
			_warn((Exception) o);
		} else if (o==LOGMEM){
			super.log(FQCN,Level.WARN,meminfo(),null);
		} else {
			super.log(FQCN,Level.WARN,o,null);
		}
	}

	public void error(Object o) {
		if (o instanceof Exception) {
			_error((Exception) o);
		} else if (o==LOGMEM){
			super.log(FQCN,Level.ERROR,meminfo(),null);
		} else {
			super.log(FQCN,Level.ERROR,o,null);
		}
	}

	public void fatal(Object o) {
		if (o instanceof Exception) {
			_fatal((Exception) o);
		} else if (o==LOGMEM){
			super.log(FQCN,Level.FATAL,meminfo(),new Throwable());
		} else {
			super.log(FQCN,Level.FATAL,o,new Throwable());
		}
	}
	
	private String meminfo() {
		Date d=new Date();
		return String.format("Time: %.2f, Free memory: %d, total memory: %d, max memory: %d",
				(d.getTime()/1000.0),
				Runtime.getRuntime().freeMemory(),
				Runtime.getRuntime().totalMemory(),
				Runtime.getRuntime().maxMemory());
	}
	
	// constructor
	
	public TgLogger(Class c) {
		super(c.getName());
	}
	
	public TgLogger(String n) {
		super(n);
	}
}
