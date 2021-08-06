import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONObject;

public class API {
	
	public int getLength(char[] phraseChars) throws UnsupportedEncodingException {
		String quote1 = String.valueOf(phraseChars);
		String URL = "http://indic-wp.thisisjava.com/api/getLength.php?string=" + URLEncoder.encode(quote1, "UTF-8") + "&language='telugu'";
		String newURL = URL.replaceAll(" ", "%20");
    
		Client client = Client.create();
    	WebResource resource = client.resource(newURL);
    	String response = resource.get(String.class);
    	
    	int index = response.indexOf("{");
    	response = response.substring(index);
    	JSONObject myObject = new JSONObject(response.trim()); 

    	Number length = myObject.getNumber("data");
    	
    	int q_length = length.intValue();

    	return q_length;
	}
	
	public String chooseLang(String quote) {
		if (quote.matches(".*[a-zA-Z]+.*")) {
			return "English";
		}
		else {
			return "Telugu";
		}
	}
	
	public ArrayList<String> parseLogicalChars(ArrayList<String> quote_array_db) throws SQLException, UnsupportedEncodingException {
		
		//puts arraylist into array
		String[] quote_arraydb = quote_array_db.toArray(new String[0]);
		
		//creates new arraylist to be returned
		ArrayList<String> quote_array = new ArrayList<String>();
		
		for (int i = 0; i < quote_arraydb.length; i++) {
			
			String lang = chooseLang(quote_arraydb[i]);
			String URL = "http://indic-wp.thisisjava.com/api/getLogicalChars.php?string=" + URLEncoder.encode(quote_arraydb[i], "UTF-8") + "&language='" + lang + "'";
			String newURL = URL.replaceAll(" ", "%20");
        
			Client client = Client.create();
        	WebResource resource = client.resource(newURL);
        	String response = resource.get(String.class);
        
        	int index = response.indexOf("{");
        	response = response.substring(index);
        	JSONObject myObject = new JSONObject(response.trim()); 
        	
        	System.out.println(response);
        	
        	JSONArray jsonArray = myObject.getJSONArray("data");
        
        	ArrayList<String> temp_array = new ArrayList<String>();
            
        	for (int j = 0; j < jsonArray.length(); j++) {
        		temp_array.add(jsonArray.getString(j));
        	}
        	
        	String quote = String.join("", temp_array);
        	quote_array.add(quote);
		}
		
		return quote_array;
	}

}

