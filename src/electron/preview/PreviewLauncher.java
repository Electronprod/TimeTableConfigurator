package electron.preview;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import electron.data.FileOptions;
import electron.data.outFile;

public class PreviewLauncher {

	public PreviewLauncher() throws IOException {
		File showfile = new File("preview.html");
		FileOptions.loadFile(showfile);
		FileOptions.writeFile(generatePage(), showfile);
		Desktop.getDesktop().open(showfile);
		showfile.deleteOnExit();
	}
	private String generatePage() throws IOException {
		InputStream url = getClass().getResourceAsStream("index.html");
		String index = FileOptions.getInternalFileLineWithSeparator(url, "");
		String gen="";
		for(int i=0;i<outFile.getClasses().size();i++) {
			gen=gen+"<div>";
			gen=gen+generateTable(outFile.getClasses().get(i));
			gen=gen+"</div>";
		}
		index = index.replace("%body%", gen);
		index = index.replaceAll("null", "");
		return index;
	}
	
	private String generateTable(String classname) throws IOException {
		InputStream url = getClass().getResourceAsStream("table.html");
		String table = FileOptions.getInternalFileLineWithSeparator(url, "");
		table=table.replace("%classname%", classname);
		table=table.replace("%monday%", generateLessons(1,classname));
		table=table.replace("%tuesday%", generateLessons(2,classname));
		table=table.replace("%wednesday%", generateLessons(3,classname));
		table=table.replace("%thursday%", generateLessons(4,classname));
		table=table.replace("%friday%", generateLessons(5,classname));
		table=table.replace("%saturday%", generateLessons(6,classname));
		table=table.replace("%sunday%", generateLessons(7,classname));
		return table;		
	}
	private String generateLessons(int dayID,String classname) {
		JSONArray lessonsarr = outFile.getDay(dayID, classname.toLowerCase());
		if(lessonsarr == null) {
			return "<th colspan=\"4\">There aren't lessons</th>";
		}
		String lessons=null;
		for(int i=0;i<lessonsarr.size();i++) {
			lessons=lessons+generateLesson((JSONObject) lessonsarr.get(i),i+1);
		}
		if(lessons==null) {return "<th colspan=\"4\">There aren't lessons</th>";}
		return lessons;
	}
	private String generateLesson(JSONObject lesson,int num) {
		String time = String.valueOf(lesson.get("time"));
		String name = String.valueOf(lesson.get("lesson"));
		String teacher = String.valueOf(lesson.get("teacher"));
		String btn="<form action=\"/teacher:"+teacher+" \">\r\n"
				+ "            <button type=\"submit\">"+teacher+"</button>\r\n"
				+ "        </form>";
		return "<tr><th>"+num+"</th><th>"+time+"</th><th>"+name+"</th><th>"+btn+"</th></tr>";
	}
}