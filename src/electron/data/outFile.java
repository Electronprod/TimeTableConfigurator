package electron.data;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import electron.utils.JSONSort;
import electron.utils.logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class outFile {
	private static File outf = new File("config.json");
	private static JSONObject config;
	
	/**
	 * Must be called before other methods from this file
	 */
	public static void load() {
		logger.log("[RESOURCE_SYSTEM]: loading...");
		if (!outf.exists()) {
			FileOptions.loadFile(outf);
			config = new JSONObject();
			write();
		}else {
			config = (JSONObject) FileOptions.ParseJs(FileOptions.getFileLine(outf));
			//Load settings for gui
			JSONObject site = (JSONObject) config.get("site");
		}
		logger.log("[RESOURCE_SYSTEM]: loaded.");
	}
	/**
	 * Get config JSONObject
	 * @return
	 */
	public static JSONObject getConfig() {
		return config;
	}
	public static void clear() {
		ObservableList<String> classes = getClasses();
		for(int i = 0;i<classes.size();i++) {
			config.remove(classes.get(i));
		}
		write();
		logger.log("[RESOURCE_SYSTEM]: deleted all database data.");
	}
	/**
	 * Write changes to database
	 */
	public static void write() {
		FileOptions.writeFile(config.toJSONString(), outf);
		logger.debug("[RESOURCE_SYSTEM] wrote changes to file.");
	}
	/**
	 * Create new class section in database
	 * @param classname
	 * @return success/fail
	 */
	public static boolean createClass(String classname) {
		if(config.containsKey(classname.toLowerCase())) {
			logger.error("[RESOURCE_SYSTEM]: this class already exists!");
			return false;
		}
		//Generating days
		JSONObject days = new JSONObject();
		days.put("1", new JSONArray());
		days.put("2", new JSONArray());
		days.put("3", new JSONArray());
		days.put("4", new JSONArray());
		days.put("5", new JSONArray());
		days.put("6", new JSONArray());
		days.put("7", new JSONArray());
		//Creating and adding class to config
		config.put(classname.toLowerCase(), days);
		write();	
		logger.debug("[RESOURCE_SYSTEM]: added new class.");
		return true;
	}
	/**
	 * Remove class section from database
	 * @param classname
	 * @return success/fail
	 */
	public static boolean removeClass(String classname) {
		if(!config.containsKey(classname.toLowerCase())) {
			logger.error("[RESOURCE_SYSTEM]: class not found: "+classname);
			return false;
		}
		JSONObject obj = (JSONObject) config.get(classname.toLowerCase());
		config.remove(classname.toLowerCase(), obj);
		write();
		logger.debug("[RESOURCE_SYSTEM]: removed class.");
		return true;
	}
	/**
	 * Get all classes
	 * @return classes list
	 */
	public static ObservableList<String> getClasses(){
		Set<String> keys = new HashSet();
		keys.addAll(config.keySet());
		keys.remove("site");
		keys.remove("api");
		ObservableList<String> classes = FXCollections.observableArrayList();
		classes.addAll(keys);
		return classes;
	}
	/**
	 * Get JSONArray of lessons for day
	 * @param day
	 * @param classname
	 * @return
	 */
	public static JSONArray getDay(int day,String classname) {
		JSONObject days =(JSONObject) config.get(classname.toLowerCase());
		JSONArray result = JSONSort.sort((JSONArray) days.get(String.valueOf(day)));
		return result;
	}
	public static JSONArray getDayRaw(int day,String classname) {
		JSONObject days =(JSONObject) config.get(classname.toLowerCase());
		return (JSONArray) days.get(String.valueOf(day));
	}
	/**
	 * Add lesson to database
	 * @param teacher
	 * @param lesson
	 * @param students
	 * @param time
	 * @param day
	 * @param classname
	 * @return success/fail
	 */
	public static boolean addLesson(String teacher, String lesson, String students, String time, int day, String classname) {
		if(!config.containsKey(classname.toLowerCase())) {
			logger.error("[RESOURCE_SYSTEM]: class not found: "+classname);
			return false;
		}
		if(day < 1 & day >7) {
			logger.error("[RESOURCE_SYSTEM]: incorrect day entered: "+day);
			return false;
		}
		if(!time.contains(":")) {
			logger.error("[RESOURCE_SYSTEM]: incorrect time entered: "+time);
			return false;
		}
		if(time.length()==4) {
			logger.debug("[RESOURCE_SYSTEM]: addLesson: fixing time: "+time);
			time = "0"+time;
			logger.debug("[RESOURCE_SYSTEM]: addLesson: fixed time: "+time);
		}
		JSONObject lessonobj = new JSONObject();
		lessonobj.put("time", time);
		lessonobj.put("students", students);
		lessonobj.put("teacher", teacher.toLowerCase());
		lessonobj.put("lesson", lesson);
		getDayRaw(day,classname).add(lessonobj);
		write();
		return true;
	}
	/**
	 * Remove lesson from database
	 * @param day
	 * @param classname
	 * @param time
	 * @param lesson
	 * @return success/fail
	 */
	public static boolean removeLesson(int day, String classname, String time, String lesson) {
		JSONArray dayarr = getDayRaw(day,classname);
		for(int i = 0;i<dayarr.size();i++) {
			JSONObject item = (JSONObject) dayarr.get(i);
			if(item.containsValue(lesson)) {
				if(item.containsValue(time)) {
					logger.debug("[RESOURCE_SYSTEM]: found lesson to delete: "+lesson);
					dayarr.remove(item);
					write();
					return true;
				}
			}
		}
		logger.error("[RESOURCE_SYSTEM]: lesson not found: "+lesson);
		return false;
	}
	/**
	 * Get items for list
	 * @param classname
	 * @param day
	 * @return list of items
	 */
	public static ObservableList<String> getItems(String classname,int day){
		//Checking for this class
		if(!config.containsKey(classname.toLowerCase())) {
			logger.error("[RESOURCE_SYSTEM]: class not found: "+classname);
			return null;
		}		
		JSONArray dayarr = getDay(day,classname);
		ObservableList<String> list = FXCollections.observableArrayList();
		if(dayarr==null) {return list;}
		for(int i = 0;i<dayarr.size();i++) {
			JSONObject lesson = (JSONObject) dayarr.get(i);
			//String lessonview = String.valueOf(lesson.get("time"))+" | "+String.valueOf(lesson.get("lesson"))+" | "+String.valueOf(lesson.get("teacher"))+" | "+String.valueOf(lesson.get("students"));
			String lessonview = String.valueOf(lesson.get("time"))+" | "+String.valueOf(lesson.get("lesson"))+" | "+String.valueOf(lesson.get("teacher"));
			list.add(lessonview);
		}
		return list;
	}
	public static Path getPath() {
		return Paths.get(outf.getPath());
	}
}