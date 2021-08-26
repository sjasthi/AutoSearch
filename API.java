import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;

/**
 * This class is responsible for calling the web services to parse the input
 * string into logical characters
 * 
 * @author srj
 *
 */

public class API {

	/**
	 * Wrapper for the getLength
	 * 
	 * @param phraseChars
	 * @return
	 * @throws UnsupportedEncodingException
	 */

	public int getLength(char[] phraseChars) throws UnsupportedEncodingException {
		String quote1 = String.valueOf(phraseChars);
		String URL = "http://indic-wp.thisisjava.com/api/getLength.php?string=" + URLEncoder.encode(quote1, "UTF-8")
				+ "&language='telugu'";
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

	/**
	 * for determining the language of input string
	 * 
	 * @param input_string
	 * @return
	 */

	public String chooseLang(String input_string) {
		if (input_string.matches(".*[a-zA-Z]+.*")) {
			return "English";
		} else {
			return "Telugu";
		}
	}

	/**
	 * Wrapper for getting the filler character
	 * 
	 * @param count
	 * @param language
	 * @return
	 */
	public String[] getFillerCharacters(int count, String language) {
		String[] theChars = new String[count];

		String URL = "https://indic-wp.thisisjava.com/api/getFillerCharacters.php?count=" + count
				+ "&type=CONSONANT&language=" + language;
		String newURL = URL.replaceAll(" ", "%20");

		Client client = Client.create();
		WebResource resource = client.resource(newURL);
		String response = resource.get(String.class);

		int index = response.indexOf("{");
		response = response.substring(index);
		JSONObject myObject = new JSONObject(response.trim());

		JSONArray jsonArray = myObject.getJSONArray("data");

		for (int j = 0; j < count; j++) {
			theChars[j] = jsonArray.getString(j);
		}

		return theChars;
	}

	/**
	 * Wrapper for parsing a string into logical characters
	 * 
	 * @param input_string
	 * @return
	 * @throws SQLException
	 * @throws UnsupportedEncodingException
	 */

	public String[] getLogicalChars(String input_string) throws SQLException, UnsupportedEncodingException {
		String lang = Preferences.LANGUAGE;
		String URL = "http://indic-wp.thisisjava.com/api/getLogicalChars.php?string="
				+ URLEncoder.encode(input_string, "UTF-8") + "&language='" + lang + "'";
		String newURL = URL.replaceAll(" ", "%20");
		Client client = Client.create();
		WebResource resource = client.resource(newURL);
		String response = resource.get(String.class);
		int index = response.indexOf("{");
		response = response.substring(index);
		JSONObject myObject = new JSONObject(response.trim());

		JSONArray jsonArray = myObject.getJSONArray("data");
		String[] temp_array = new String[jsonArray.length()];

		for (int j = 0; j < jsonArray.length(); j++) {
			temp_array[j] = jsonArray.getString(j);
		}

		return temp_array;
	}

}
