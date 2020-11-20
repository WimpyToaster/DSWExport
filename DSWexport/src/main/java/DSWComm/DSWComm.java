package DSWComm;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.text.SimpleDateFormat;
import java.util.Date;

import kong.unirest.HttpResponse;
import kong.unirest.Unirest;

import java.io.BufferedWriter;
import java.io.FileWriter;

import DSWexport.HTTPRequests;

public class DSWComm {
     /**
     * Get Document in RDF format and writes to a file named with the Document UUID
     * @param BioDataAPI URL of BioData.pt
     * @param Auth bearer Token
     * @param docUUID Document UUID
     * @return String with the File Name
     * @throws IOException
     * @throws ParseException
     * @throws java.text.ParseException
     */
    public static String GETDocumentDownload(String BioDataAPI, String Auth, String docUUID) throws IOException, ParseException, java.text.ParseException {
        HttpResponse<String> response = Unirest.get(BioDataAPI + "/documents/" + docUUID + "/download")
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + Auth)
                .asString();
        
        boolean isRDF = response.getBody().indexOf("<rdf") !=-1? true: false;
        boolean isTurtle = response.getBody().indexOf("@prefix") !=-1? true: false;

        String fileName = null;

        if (isTurtle) {
            fileName = docUUID + ".ttl";
            BufferedWriter writer = new BufferedWriter(new FileWriter("./Documents/" + fileName));
            writer.write(response.getBody());
            writer.close();
        }
        else if (isRDF) {
            fileName = docUUID + ".rdf";
            BufferedWriter writer = new BufferedWriter(new FileWriter("./Documents/" + fileName));
            writer.write(response.getBody());
            writer.close();
        }
        else{
            System.out.println("Document must be RDF or Turtle");
            return null;
        }

        return fileName;
        
        
    }

    /**
     * Get ald the Documents for a given Questionnaire
     * @param BioDataAPI URL of BioData.pt
     * @param Auth bearer Token
     * @param questUUID Questionnaire UUID
     * @return Map<String, Date> with Document UUID and is Date of creation
     * @throws IOException
     * @throws ParseException
     * @throws java.text.ParseException
     */
    public static Map<String, Date> GETDocumentsUUID(String BioDataAPI, String Auth, String questUUID) throws IOException, ParseException, java.text.ParseException {
        Map<String, Object> params = new HashMap<>();
        params.put("questionnaireUuid", questUUID);

        JSONObject documents = HTTPRequests.GETRequest(BioDataAPI + "/documents", Auth, params);
        
        Map<String, Date> documentsUUID = new HashMap();

        JSONObject embedded = (JSONObject) documents.get("_embedded");
        JSONArray docs = (JSONArray) embedded.get("documents");
        
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.SSS'Z'");

        if (docs != null) {
            for (Object doc : docs) {
                JSONObject docInfo = (JSONObject) doc;
                documentsUUID.put(docInfo.get("uuid").toString(), dateFormat.parse(docInfo.get("createdAt").toString()) );
            }
        }

        return documentsUUID;
    }


    /**
     * Get all questionnaires UUIDs
     * @param BioDataAPI URL of BioData.pt
     * @param Auth bearer Token
     * @return A List of UUIDs
     * @throws IOException
     * @throws ParseException
     */
    public static List<String> GETQuestionnairesUUID(String BioDataAPI, String Auth) throws IOException, ParseException {
        JSONObject questionnaires = HTTPRequests.GETRequest(BioDataAPI + "/questionnaires", Auth, null);
        
        List<String> questionnaireUUID = new ArrayList<>();

        JSONObject embedded = (JSONObject) questionnaires.get("_embedded");
        JSONArray quests = (JSONArray) embedded.get("questionnaires");

        for (Object quest : quests) {
            JSONObject questInfo = (JSONObject) quest;
            questionnaireUUID.add(questInfo.get("uuid").toString());
        }

        return questionnaireUUID;
    }

    /**
     * Returns the name of the Questionnaire
     * @param BioDataAPI URL of BioData.pt
     * @param Auth bearer Token
     * @param UUID Questionnaire UUID
     * @return String with Questionnaire Name
     * @throws IOException
     * @throws ParseException
     */
    public static String GETQuestionnaireName(String BioDataAPI, String Auth, String UUID) throws IOException, ParseException {
        JSONObject questionnaire = HTTPRequests.GETRequest(BioDataAPI + "/questionnaires/" + UUID, Auth, null);
        String name = questionnaire.get("name").toString();

        return name;
    }
}