package electron.utils;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class DayMethods {
	public static String getDayByID(int id) {
		switch (id) {
		case 1:
			return "Monday";
		case 2:
			return "Tuesday";//Tuesday
		case 3:
			return "Wednesday";//Wednesday
		case 4:
			return "Thursday";//Thursday
		case 5:
			return "Friday";//Friday
		case 6:
			return "Saturday";
		case 7:
			return "Sunday";
		}
		logger.error("getDayByID: incorrect input.");
		return "Error";
	}
	public static int getDayByName(String name) {
		switch (name) {
		case "Monday":
			return 1;
		case "Tuesday":
			return 2;//Tuesday
		case "Wednesday":
			return 3;//Wednesday
		case "Thursday":
			return 4;//Thursday
		case "Friday":
			return 5;//Friday
		case "Saturday":
			return 6;
		case "Sunday":
			return 7;
		}
		logger.error("getDayByName: incorrect input.");
		return 0;
	}
	public static ObservableList<String> generateDays(){
		ObservableList<String> list = FXCollections.observableArrayList();
		for(int i = 1;i<8;i++) {
			list.add(getDayByID(i));
		}
		return list;
	}
}
