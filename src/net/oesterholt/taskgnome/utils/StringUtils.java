package net.oesterholt.taskgnome.utils;

public class StringUtils {
	public static String makeString(int repeat, String withInput) {
		StringBuilder b = new StringBuilder();
		while (repeat > 0) {
			if ((repeat%2) == 1) {
				repeat -= 1;
				b.append(withInput);
			} else {
				repeat /= 2;
				b.append(b.toString());
			}
		}
		return b.toString();
	}
}
