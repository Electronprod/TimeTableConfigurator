package electron.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class JSONSort {

	public static JSONArray sort(JSONArray array) {
		if(array==null) {return null;}
        ArrayList<JSONObject> list = new ArrayList<>();
        for (int i = 0; i < array.size(); i++) {
            list.add((JSONObject) array.get(i));
        }
        Collections.sort(list, new MyJSONComparator());
        JSONArray result = new JSONArray();
        for (JSONObject obj : list) {
            result.add(obj);
        }
        return result;
	}

}
class MyJSONComparator implements Comparator<JSONObject> {

@Override
public int compare(JSONObject o1, JSONObject o2) {
    String v1 = String.valueOf(o1.get("time"));
    String v3 = String.valueOf(o2.get("time"));
    return v1.compareTo(v3);
}

}
