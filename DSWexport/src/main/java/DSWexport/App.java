package DSWexport;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import kong.unirest.HttpResponse;
import kong.unirest.Unirest;

public final class App {
    private App() {
    }

    
    /**TODO
     * Get Document in RDF or Turtle
     * Send a Document to Fuseki
     * Only download new/modified Documents
     * Polling system
     */


    /**
     * 
     * 
     * @param args The arguments of the program.
     */
    public static void main(String[] args) {

        String BioDataAPI = "https://api.biodata-pt.ds-wizard.org";
        String BioDataAuth = "{\n\t\"email\": \"antonio.terra@tecnico.ulisboa.pt\",\n\t\"password\": \"aterra\"\n}";

        try {
            JSONObject token = POSTRequest(BioDataAPI + "/tokens", BioDataAuth);

            Map<String, Object> getParams = new HashMap<>();
            JSONObject questionnaires = GETRequest(BioDataAPI + "/questionnaires", token.get("token").toString(), getParams);
            System.out.println(questionnaires);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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
        HttpResponse<String> response = Unirest.get(URL)
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + Auth)
                .queryString(params)
                .asString();

        JSONParser parser = new JSONParser();
        JSONObject res = (JSONObject) parser.parse(response.getBody().toString());
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
        HttpResponse<String> response = Unirest.post(URL)
            .header("Content-Type", "application/json")
            .body(POST_PARAMS)
            .asString();

        JSONParser parser = new JSONParser();
        JSONObject res = (JSONObject) parser.parse(response.getBody().toString());
        return res;
        
    }
}

