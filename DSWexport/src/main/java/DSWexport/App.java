package DSWexport;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import org.json.simple.JSONObject;

import java.util.Date;

import java.io.File;

import java.lang.Thread;

import DSWComm.DSWComm;
import FusekiComm.FusekiComm;

public final class App {
    private App() {
    }

    // Save the most recent document date uploaded for each questionnaire
    static Map<String, Date> DocsLastDate = new HashMap<>();

    /**
     * 
     * 
     * @param args The arguments of the program.
     */
    public static void main(String[] args) throws InterruptedException {

        String BioDataAPI = "https://api.biodata-pt.ds-wizard.org";
        String BioDataAuth = "{\n\t\"email\": \"antonio.terra@tecnico.ulisboa.pt\",\n\t\"password\": \"aterra\"\n}";
        String fusekiServer = "http://192.92.147.18:3030/";

        // Save what Documents was been uploaded
        List<String> documentsUploaded = new ArrayList<>();

        String authToken = null;

        try {
            JSONObject token = HTTPRequests.POSTRequest(BioDataAPI + "/tokens", BioDataAuth);
            authToken = token.get("token").toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        

        while (true) {
            try {
                
    
                List<String> questionnaires = DSWComm.GETQuestionnairesUUID(BioDataAPI, authToken);
                //System.out.println(questionnaires);
                

                
                String questionnaireName;
    
                for (String quest : questionnaires) {

                    questionnaireName = DSWComm.GETQuestionnaireName(BioDataAPI, authToken, quest).replaceAll("\\s+","");
                    System.out.println(questionnaireName);
                    Map<String, Date> docUUID = DSWComm.GETDocumentsUUID(BioDataAPI, authToken, quest);

                    if(!docUUID.isEmpty()) {

                        String recentDoc = mostRecentDoc(docUUID);
                        if (DocsLastDate.containsKey(quest)) {
                            if ( DocsLastDate.get(quest).compareTo(docUUID.get(recentDoc)) < 0 ) { 
                                createAndUpdateFuseki(BioDataAPI, fusekiServer, authToken, recentDoc, questionnaireName, quest, docUUID.get(recentDoc));
                            }       
                        }
                        else {
                            createAndUpdateFuseki(BioDataAPI, fusekiServer, authToken, recentDoc, questionnaireName, quest, docUUID.get(recentDoc));
                        }               
                    }
                }
    
    
            } catch (Exception e) {
                e.printStackTrace();
            }

            //Pause for 10 min
            Thread.sleep(600000);
        }  
        
    }


    /**
     * Returns the most recent DocUUID
     * @param docs Map<String, Date> with the DocUUID and is Date of creation
     * @return The most recent DocUUID
     */
    public static String mostRecentDoc(Map<String, Date> docs) {
        String mostRecent = null;
        
        for (String doc: docs.keySet()) {
            if (mostRecent == null) {
                mostRecent = doc;
            }
            else {
                if( docs.get(mostRecent).compareTo(docs.get(doc)) < 0 ) {
                    mostRecent = doc;
                }
            }
        }

        return mostRecent;
    }

    /**
     * Verifies if is need to create a new dataset and Update it
     * @param BioDataAPI URL of BioData.pt
     * @param fusekiServer Fuseki Server URL
     * @param authToken bearer Token
     * @param recentDoc The most recent DocUUID
     * @param questionnaireName Questionnaire Name
     * @param QuestionnairUUID Questionnaire UUID
     * @param docDate The most recent Doc Date
     */
    public static void createAndUpdateFuseki(String BioDataAPI, String fusekiServer, String authToken, String recentDoc, String questionnaireName, String QuestionnairUUID,Date docDate) {
        try {
            String fileName = DSWComm.GETDocumentDownload(BioDataAPI, authToken, recentDoc);
            if (fileName != null) {
                File file = new File("./Documents/" + fileName);

                if (!FusekiComm.datasetExists(fusekiServer + "$/datasets" + questionnaireName)) {
                    FusekiComm.createDataset(fusekiServer + "$/datasets", questionnaireName);
                }

                FusekiComm.uploadModel(file, fusekiServer + questionnaireName);
                DocsLastDate.put(QuestionnairUUID, docDate);

                System.out.println(questionnaireName + " uploaded");
            }             
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }
}

