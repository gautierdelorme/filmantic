package parser_owl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;

import org.json.JSONObject; // penser à rajouter la bibliothèque json-20141113 disponible dans le dossier lib

public class OMDBProxy {

	private String baseUrl = "http://www.omdbapi.com/?y=&plot=short&r=json&t=";//base de l'url correspondant à la requête get qui devra être compléter avec le nom du film à considérer
	
	public OMDBProxy()
	{
		
	}
	
	
	public HashMap<String, String> getMovieInfos(String movieTitle)
	{ //permet pour un titre de film de récupérer un hachage contenant les couples (propriété du film / valeur) retournés par OMDB
		HashMap<String, String> ret = new HashMap<>();
		
		 URL url;
	      HttpURLConnection conn;
	      BufferedReader rd;
	      String line;
	      String result = "";
	      try {
	         url = new URL(this.baseUrl+URLEncoder.encode(movieTitle, "UTF-8"));
	         conn = (HttpURLConnection) url.openConnection();
	         conn.setRequestMethod("GET");
	         rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
	         while ((line = rd.readLine()) != null) {
	            result += line;
	         }
	         rd.close();
	         
         JSONObject obj = new JSONObject(result);
         for(String key : obj.keySet())
         {
        	 String val = obj.getString(key);
        	 ret.put(key, val);
         }
	         
	         
	      } catch (IOException e) {
	         e.printStackTrace();
	      } catch (Exception e) {
	         e.printStackTrace();
	      }
		
		return ret;
	}
	
}
