package DSWexport;

import java.io.IOException;

import java.util.Map;


import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


import kong.unirest.HttpResponse;
import kong.unirest.Unirest;


public class HTTPRequests {
      /**
     * Execute a HTTP GET Request
     * @param URL url where to send the Request
     * @param Auth bearer Token
     * @param params Map<String, Object> with the query parameters
     * @return JSONObject with the response Body
     * @throws IOException
     * @throws ParseException
     */
    public static JSONObject GETRequest(String URL, String Auth, Map<String, Object> params) throws IOException, ParseException {
        HttpResponse<String> response;
        
        if (Auth != null) {
            response = Unirest.get(URL)
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + Auth)
                .queryString(params)
                .asString();
        }
        else {
            response = Unirest.get(URL)
                .header("Content-Type", "application/json")
                .queryString(params)
                .asString();
        }

        JSONParser parser = new JSONParser();
        JSONObject res = (JSONObject) parser.parse(response.getBody().toString());
        return res;
    }

     /**
     * Execute a HTTP GET Request and returns the Status Code
     * @param URL url where to send the Request
     * @param Auth bearer Token
     * @param params Map<String, Object> with the query parameters
     * @return Returns the Status Code
     * @throws IOException
     * @throws ParseException
     */
    public static Integer GETRequestStatus(String URL, String Auth, Map<String, Object> params) throws IOException {
        HttpResponse<String> response;
        
        if (Auth != null) {
            response = Unirest.get(URL)
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + Auth)
                .queryString(params)
                .asString();
        }
        else {
            response = Unirest.get(URL)
                .header("Content-Type", "application/json")
                .queryString(params)
                .asString();
        }
        
        Integer res = response.getStatus();
        return res;
    }


    /**
     * Execute a HTTP POST Request
     * @param URL url where to send the Request
     * @param POST_PARAMS body of the Request
     * @return JSONObject with the response Body
     * @throws IOException
     * @throws ParseException
     */
    public static JSONObject POSTRequest(String URL, String POST_PARAMS) throws IOException, ParseException {
        HttpResponse<String> response;
        if (POST_PARAMS == null) {
            response = Unirest.post(URL)
            .asString();
        }
        else {
            response = Unirest.post(URL)
            .header("Content-Type", "application/json")
            .body(POST_PARAMS)
            .asString();
        }
        
        JSONObject res = null;
        JSONParser parser = new JSONParser();


        res = (JSONObject) parser.parse(response.getBody().toString());
        
        return res;
        
    }
}