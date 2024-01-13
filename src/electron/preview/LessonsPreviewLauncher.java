package electron.preview;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import electron.data.FileOptions;
import electron.data.outFile;
import electron.utils.Other;
import electron.utils.logger;

public class LessonsPreviewLauncher {
	public LessonsPreviewLauncher() throws IOException {
		File showfile = new File("previewLessons.html");
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
		InputStream url = getClass().getResourceAsStream("lessonstable.html");
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
			return "";
		}
		Map<String,List<String>> lessons = new HashMap();
		List<String> times = new ArrayList();
		for(int i=0;i<lessonsarr.size();i++) {
			JSONObject lesson = (JSONObject) lessonsarr.get(i);
			String lessonname = String.valueOf(lesson.get("lesson"));
			String time = String.valueOf(lesson.get("time"));
			if(lessons.containsKey(time)) {
				List<String> ls = lessons.get(time);
				ls.add(lessonname);
				lessons.put(time, ls);
			}else {
				List<String> ls = new ArrayList();
				ls.add(lessonname);
				times.add(time);
				lessons.put(time, ls);
			}
		}
		String html = "";
		int num=1;
		Collections.sort(times);
		for(int i = 0;i<times.size();i++) {
			List<String> ls = lessons.get(times.get(i));
			ls=Other.removeDuplicates(ls);
			String lsname="";
			for(int a=0;a<ls.size();a++) {
				if(ls.size()>1) {
					lsname=lsname+ls.get(a)+"/";
					continue;
				}
				lsname=lsname+ls.get(a);
			}
			if(lsname.endsWith("/")) {
			lsname = lsname.substring(0,lsname.length() - 1);
			}
			html = html+"<tr><th>"+num+"</th><th>"+times.get(i)+"</th><th>"+lsname+"</th></tr>";
			num++;
		}
		return html;
	}
}
