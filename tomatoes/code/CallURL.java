
import java.net.*;
import java.io.*;
import java.util.Arrays;
import java.util.ArrayList;

//import java.util.HashMap;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

public class CallURL {
    public static void main(String[] args) throws Exception {
    	final ArrayList<String> movies = new ArrayList<String>();
    	final ArrayList<String> pair_0 = new ArrayList<String>();
    	final ArrayList<String> pair_1 = new ArrayList<String>();
    	final String API_KEY = "s4vmc7td6jsbf2pfzj63vs48";
    	
    	String inputLine;
    	Gson gson = new Gson();
    	JsonParser parser = new JsonParser();
    	
    	// read IDs
        InputStream txt = new FileInputStream("../movie_ID_name.txt");
        PrintWriter output = new PrintWriter("../movie_ID_name_similar.txt");
        BufferedReader in = new BufferedReader(new InputStreamReader(txt));
        while ((inputLine = in.readLine()) != null){
        	String ID = Arrays.asList(inputLine.split(",")).get(0);
            movies.add(ID);
        }
        in.close();
        
        // call URL
        for (int i = 0; i<movies.size(); i++){
        	String link = "http://api.rottentomatoes.com/api/public/v1.0/movies/"+movies.get(i)+"/similar.json?apikey="+API_KEY+"&limit=5";
        	System.out.println(link); 
        	try {
                 // get URL content
                 URL url = new URL(link);
                 StringBuilder builder = new StringBuilder();
                 URLConnection conn = url.openConnection();
                 // open the stream and put it into BufferedReader
                 InputStreamReader reader = new InputStreamReader(conn.getInputStream());
                 //System.out.println(reader.getEncoding());
                 BufferedReader br = new BufferedReader(
                                    new InputStreamReader(conn.getInputStream()));
                 while((inputLine = br.readLine())!=null)
                	 builder.append(inputLine);
                 br.close();
                 String toParse = builder.toString();
                 //System.out.println(toParse);
                 JsonArray array = parser.parse(toParse).getAsJsonObject().getAsJsonArray("movies");
                 if (array.size()==0){
                	 output.println(movies.get(i)+",null");
                	 //System.out.println(movies.get(i)+",null");
                 }
                 else{
                	 for (int j = 0; j<array.size(); j++){                	
                		 String temp = array.get(j).getAsJsonObject().get("id").toString();
                		 temp=temp.replaceAll("^\"|\"$","");
                		 output.println(movies.get(i)+","+temp);
                		 //System.out.println(movies.get(i)+","+temp);
                	 }
                 }
             } catch (MalformedURLException e) {
                 e.printStackTrace();
             } catch (IOException e) {
                 e.printStackTrace();
             }
        	 Thread.sleep(200);
        }
        output.close();
        
        //cleanup
        txt = new FileInputStream("../movie_ID_name_similar_raw.txt");
        in = new BufferedReader(new InputStreamReader(txt));
        output = new PrintWriter("../movie_ID_name_similar.txt");
        while ((inputLine = in.readLine()) != null){
        	String temp_0 = Arrays.asList(inputLine.split(",")).get(0);
        	String temp_1 = Arrays.asList(inputLine.split(",")).get(1);
        	boolean isAdded = false;
        	if(temp_1.equalsIgnoreCase("null")){
        		//pair_0.add(temp_0);
        		//pair_1.add(temp_1);
        		System.out.println("no match!");
        		continue;
        	}
        	for (int i = 0; i<pair_0.size(); i++){
        		if(temp_1.equalsIgnoreCase(pair_0.get(i)) && temp_0.equalsIgnoreCase(pair_1.get(i))){
        			System.out.println("duplicates Detected!");
        			isAdded = true;
        			break;
        		}
        	}
        	if (!isAdded){
        		pair_0.add(temp_0);
        		pair_1.add(temp_1);
        		isAdded=true;
        	}else{
        		isAdded=false;
        	}
        }
        for (int i = 0; i<pair_0.size(); i++){
        	output.println(pair_0.get(i)+","+pair_1.get(i));
        }
        in.close();
        output.close();
    }
}
