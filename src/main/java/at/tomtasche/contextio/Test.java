package at.tomtasche.contextio;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author Thomas Taschauer | tomtasche.at
 *
 */
public class Test {

	public static void main(String[] args) {
		ContextIO dokdok = new ContextIO("vt9cuif4", "CtGBnwzZdHukmcEt");
		
		Map<String, String> params = new HashMap<String, String>();
		params.put("since", "0");
		
		System.out.println(dokdok.allMessages("cezeugoh@gmail.com", params).rawResponse.getBody());
	}
}
