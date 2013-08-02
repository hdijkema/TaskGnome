package net.oesterholt.taskgnome.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {
	
	public static Date today() {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Date d = null;
		try {
			d = format.parse(format.format(new Date()));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return d;
	}
	
	public static Date tomorrow() {
		Date now = today();
		long t = now.getTime() + 24*3600*1000;
		return new Date(t);
	}
	
	public static Date nextweek() {
		Date now = today();
		long t = now.getTime() + 25*3600*7*1000;
		return new Date(t);
	}

}
