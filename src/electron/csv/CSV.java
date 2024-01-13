package electron.csv;

import java.io.File;
import java.util.List;

import electron.Controls;
import electron.data.FileOptions;
import electron.data.outFile;
import electron.utils.Other;
import electron.utils.logger;
import javafx.scene.control.Alert;

public class CSV {

	public static void parse(File f, Controls c) {
		logger.log("-----------------[CSV]-----------------");
		//Deleting all data from database
		outFile.clear();
		List<String> lines = FileOptions.getFileLines(f.getPath());
		if(lines == null) {
			error("File is empty or unreadable.");
			return;
		}
		//Variables
		String classname = null;
		String day = null;
		String splitter = ";";
		//Parsing all lines
		for(int i = 0;i<lines.size();i++) {
			//Getting current line
			String line = lines.get(i);
			//Class flag
			if(line.toLowerCase().contains("!class")) {
				String[] spl = line.split(splitter);
				logger.log("[CSV]: found class to add: "+spl[1]);
				if(!outFile.createClass(spl[1])) {
					error("Error creating new class.");
					return;
				}
				classname = spl[1].toLowerCase();
			}else if(line.toLowerCase().contains("!day")) {
				if(classname == null) {
					error("Error in file format: classname isn't defined before day.");
					return;
				}
				
				String[] spl = line.split(splitter);
				logger.log("[CSV]: found day to add: "+spl[1]);
				day = spl[1].toLowerCase();
			}else {
				if(classname == null) {
					error("Error in file format: classname isn't defined before day.");
					return;
				}
				if(day == null) {
					error("Error in file format: day isn't defined before lessons.");
					return;
				}
				String[] spl = line.split(splitter);
				try {
				logger.log("[CSV]: found lesson to add: "+spl[1]);
				String time = spl[0];
				String lesson = spl[1];
				String teacher = spl[2];
				if(!Other.isNum(day)) {
					error("Day must be number! Line: "+i);
					return;
				}
				int d = Integer.parseInt(day);
				if(!outFile.addLesson(teacher, lesson, "all", time, d, classname)) {
					error("Error adding lesson to database. Line: "+i);
					return;
				}
				}catch(ArrayIndexOutOfBoundsException e) {
					
				}
			}
		}
		new Alert(Alert.AlertType.INFORMATION,"Import done.").show();
		c.updateList();
		logger.log("-----------------[CSV]-----------------");
	}
	private static void error(String msg) {
		logger.log("-----------------[CSV]-----------------");
		new Alert(Alert.AlertType.ERROR,msg).showAndWait();
		new Alert(Alert.AlertType.ERROR,"CSV import failed.").show();;
	}
}