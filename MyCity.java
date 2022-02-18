import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import com.google.gson.*;

/**
 * University of Washington - CSS436 Prog.2
 * 10/29/2021
 * @author Connie Seungwon Lee
 */
public class MyCity {

    public static void main(String[] args) throws Exception {
        String city = "";       // for first query
        String location = "";   // for second query

        // get inpuit
        for (String s: args) {
            city += s;
            city += "%20";
        }
        String stringURL = "http://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=KEY";
        try {
            URL url = new URL(stringURL);                               // converting stringURL to real url
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();   // connecting
            int statusCode = connection.getResponseCode();

            // Redirection
            while (statusCode >= 300 && statusCode < 400) {
                System.out.println("STATUS " + statusCode + ", Redirecting...");
                try {    
                    url = new URL(connection.getHeaderField("Location"));
                    connection = (HttpURLConnection)url.openConnection(); 
                    statusCode = connection.getResponseCode();
                }
                catch (MalformedURLException e) {
                    System.out.println("Redirection Failed");
                    System.out.println("URL Connection Failed - Please input a name of the city\n");
                    return;
                }   
            }

            // Client Error
            if (statusCode >= 400 && statusCode < 500) {
                System.out.println("4XX Error");
                System.out.println("URL Connection Failed - Please input a name of the city\n");
                return;
            }

            // Server Error
            if (statusCode >= 500 && statusCode < 600) {
                int retries = 3;
                int count = 0;
                long waiting = 1000;
                while (statusCode >= 500 && (count < retries) ) {
                    waiting *= 2; 
                    try {
                        System.out.println("STATUS " + statusCode + ", Retrying... (wating time: " + waiting + ")");
                        Thread.sleep(waiting);
                        connection = (HttpURLConnection)url.openConnection(); 
                        statusCode = connection.getResponseCode();
                    }
                    catch (InterruptedException e) {
                        System.out.println("Retry Failed\n");
                    }
                    count++;
                }
            }

            // read from url
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String stringJSON = "";
            String line = "";
            while ((line = reader.readLine()) != null) {
                stringJSON += line;
            }

            // parsing
            JsonElement element = JsonParser.parseString(stringJSON);
            JsonObject obj = element.getAsJsonObject();

            // main information
            JsonArray weatherArr = obj.get("weather").getAsJsonArray();
            JsonObject weatherObj = weatherArr.get(0).getAsJsonObject();
            String weather = weatherObj.getAsJsonPrimitive("main").getAsString();
            String description = weatherObj.getAsJsonPrimitive("description").getAsString();

            // detail information
            JsonObject mainObj = obj.get("main").getAsJsonObject();
            String temp = mainObj.getAsJsonPrimitive("temp").getAsString();
            String feels_like = mainObj.getAsJsonPrimitive("feels_like").getAsString();
            String temp_min = mainObj.getAsJsonPrimitive("temp_min").getAsString();
            String temp_max = mainObj.getAsJsonPrimitive("temp_max").getAsString();
            String pressure = mainObj.getAsJsonPrimitive("pressure").getAsString();
            String humidity = mainObj.getAsJsonPrimitive("humidity").getAsString();

            // latitude & longitude (for second api - city info)
            JsonObject coordObj = obj.get("coord").getAsJsonObject();
            String lon = coordObj.getAsJsonPrimitive("lon").getAsString();
            String lat = coordObj.getAsJsonPrimitive("lat").getAsString();
            location += lat;
            if (lon.charAt(0) != '-') {
                location = lat + "%2B" + lon;
            }
            else {
                location = lat + lon;
            }
            
            // output
            System.out.println("\n--------  Weather Information : " + city.replace("%20", " ").toUpperCase() + "--------");
            System.out.println("\n * Latitude: " + lat + ", Longitude: " + lon);
            System.out.println("\n * Main information:  " + weather + " (" + description + ")") ;
            System.out.println("\n * Detail information: \n") ;
            System.out.println("   - Current Temperature: " + temp);
            System.out.println("   - Feeling Temperature: " + feels_like);
            System.out.println("   - Minimum Temperature: " + temp_min);
            System.out.println("   - Maximum Temperature: " + temp_max);
            System.out.println("   - Pressure: " + pressure);
            System.out.println("   - Humidity: " + humidity);

        }    
        catch (IOException e) {
            System.out.println("URL Connection Failed - Please input a name of the city\n");
            return;
        }
        catch (IllegalStateException e) {
            System.out.println("URL Connection Failed - Please input a name of the city\n");
            return;
        }
        catch (NullPointerException e) {
            System.out.println("URL Connection Failed - Please input a name of the city\n");
            return;
        }

        // second api
        System.out.println("\n---------  City Information : " + city.replace("%20", " ").toUpperCase() + "----------");
    
        stringURL = "https://wft-geo-db.p.rapidapi.com/v1/geo/cities?location=" + location + "&limit=1";
        try {
            HttpRequest request = HttpRequest.newBuilder()
		        .uri(URI.create(stringURL))
		        .header("x-rapidapi-host", "wft-geo-db.p.rapidapi.com")
		        .header("x-rapidapi-key", "KEY")
		        .method("GET", HttpRequest.BodyPublishers.noBody())
		        .build();
            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            int statusCode = response.statusCode();

            // Client Error
            if (statusCode >= 400 && statusCode < 500) {
                System.out.println("4XX Error");
                System.out.println("URL Connection Failed - cannot find the city information\n");
                return;
            }

            // Server Error
            if (statusCode >= 500 && statusCode < 600) {
                int retries = 3;
                int count = 0;
                long waiting = 1000;
                while (statusCode >= 500 && (count < retries) ) {
                    waiting *= 2; 
                    try {
                        System.out.println("STATUS " + statusCode + ", Retrying... (wating time: " + waiting + ")");
                        Thread.sleep(waiting);
                        response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
                        statusCode = response.statusCode();
                    }
                    catch (InterruptedException e) {
                        System.out.println("Retry Failed\n");
                    }
                    count++;
                }
            }

            // parsing
            JsonElement element = JsonParser.parseString(response.body());
            JsonObject obj = element.getAsJsonObject();

            // city info
            JsonArray dataArr = obj.get("data").getAsJsonArray();
            JsonObject dataObj = dataArr.get(0).getAsJsonObject();
            String country = dataObj.getAsJsonPrimitive("country").getAsString();
            String countryCode = dataObj.getAsJsonPrimitive("countryCode").getAsString();
            String region = dataObj.getAsJsonPrimitive("region").getAsString();
            String regionCode = dataObj.getAsJsonPrimitive("regionCode").getAsString();
            String population = dataObj.getAsJsonPrimitive("population").getAsString();

            // output
            System.out.println("\n * Country: " + country + " (" + countryCode + ")");
            System.out.println("\n * Region: " + region + " (" + regionCode + ")");
            System.out.println("\n * Population: " + population);
            System.out.println();

        }
        catch (IOException e) {
            System.out.println("URL Connection Failed - cannot find the city information\n");
            return;
        }
        catch (IllegalStateException e) {
            System.out.println("URL Connection Failed - cannot find the city information\n");
            return;
        }
        catch (NullPointerException e) {
            System.out.println("URL Connection Failed - cannot find the city information\n");
            return;
        }
        
    }

}