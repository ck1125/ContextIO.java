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
		ContextIOV2 dokdok = new ContextIOV2("YOUR API KEY", "YOUR SECRET");
		
		Map<String, String> params = new HashMap<String, String>();
//		params.put("since", "0");
		params.put("limit", "1");

		System.out.println(dokdok.getMessages("YOUR ACCOUNT ID", params).rawResponse.getBody());
	}
}
