package electron.utils;

public class logger {
	public static boolean enDebug = false;
	public static void log(String msg) {
		System.out.println(msg);
	}
	public static void error(String msg) {
		System.err.println(msg);
	}
	public static void debug(String msg) {
		if(!enDebug) {return;}
		System.out.println("[DEBUG]"+msg);
	}
}