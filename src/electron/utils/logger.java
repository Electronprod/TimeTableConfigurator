package electron.utils;

import java.util.Scanner;

import electron.data.outFile;

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
	public static void loadConsole() {
		if(!enDebug) {return;}
		Console c = new Console();
		Thread thread = c;
		thread.start();
		logger.debug(" Console started.");
	}
}
class Console extends Thread{
	public Console() {	
	}
	public void run() {
		while(true) {
		Scanner sc = new Scanner(System.in);
		if(sc.hasNextLine()) {
			String command = String.valueOf(sc.nextLine().toLowerCase());
			executeCommand(command);
			}
		}
	}
	
	private void executeCommand(String command) {
		if(isMultiCommand("/database",command)) {
			String[] DB_args = getCommandArgs(command);
			if(DB_args[1].equals("createclass")) {
				outFile.createClass(DB_args[2]);
				return;
			}else if(DB_args[1].equals("removeclass")) {
				outFile.removeClass(DB_args[2]);
				return;
			}else if(DB_args[1].equals("getday")) {
				logger.log(outFile.getDay(Integer.parseInt(DB_args[2]), DB_args[3]).toJSONString());
				return;
			}else {
				logger.debug(" Unknown command.");
				return;
			}
		}
	}
	
	private static boolean isMultiCommand(String commandname,String command) {
		if(!command.contains(" ")) {return false;}
		if(!command.contains(commandname)) {return false;}
		if(!command.contains(commandname+" ")) {return false;}
		return true;
	}
	private static String[] getCommandArgs(String in) {
		String[] spl = in.split(" ");
		return spl;
	}

}