package electron.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Other {
	public static boolean isNum(String strNum) {
	    if (strNum == null) {
	        return false;
	    }
	    try {
	        int d = Integer.parseInt(strNum);
	    } catch (NumberFormatException nfe) {
	        return false;
	    }
	    return true;
	}
	/**
	 * From Project0_Server
	 * Duplicate deleter
	 * @param echos - list with duplicates
	 * @return list without duplicates
	 * @author Electron_prod
	 */
	  public static List<String> removeDuplicates(List<String> echos) {
		    Map<String, Integer> letters = new HashMap<>();
		    for (int i = 0; i < echos.size(); i++) {
		      String tempChar = echos.get(i);
		      if (!letters.containsKey(tempChar)) {
		        letters.put(tempChar, Integer.valueOf(1));
		      } else {
		        letters.put(tempChar, Integer.valueOf(((Integer)letters.get(tempChar)).intValue() + 1));
		      } 
		    } 
		    List<String> ans = new ArrayList<>();
		    for (Map.Entry<String, Integer> entry : letters.entrySet())
		      ans.add(entry.getKey()); 
		    
		    return ans;
		  }
}