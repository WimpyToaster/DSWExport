package DSWexport;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;

import java.util.Date;

import java.io.File;

import java.lang.Thread;

import DSWComm.DSWComm;
import FusekiComm.FusekiComm;

public final class App {
    private App() {
    }

    
    /**TODO
     * Send a Document to Fuseki
     * Polling system
     */


    /**
     * 
     * 
     * @param args The arguments of the program.
     */
    public static void main(String[] args) throws InterruptedException {

        String BioDataAPI = "https://api.biodata-pt.ds-wizard.org";
        String BioDataAuth = "{\n\t\"email\": \"antonio.terra@tecnico.ulisboa.pt\",\n\t\"password\": \"aterra\"\n}";
        String fusekiServer = "http://localhost:3030/";

        // Save what Documents was been uploaded
        List<String> documentsUploaded = new ArrayList<>();

        while (true) {
            try {
                JSONObject token = HTTPRequests.POSTRequest(BioDataAPI + "/tokens", BioDataAuth);
    
                List<String> questionnaires = DSWComm.GETQuestionnairesUUID(BioDataAPI, token.get("token").toString());
                //System.out.println(questionnaires);
    
                List<Map<String, Date>> DocsUUIDs = new ArrayList<>(); 
                File file;
                String fileName;
    
                for (String quest : questionnaires) {
                    Map<String, Date> docUUID = DSWComm.GETDocumentsUUID(BioDataAPI, token.get("token").toString(), quest);
                    if(!docUUID.isEmpty()) {
                        DocsUUIDs.add(docUUID);
                        for (String doc: docUUID.keySet()) {
                            if (!documentsUploaded.contains(doc)) {
                                fileName = DSWComm.GETDocumentDownload(BioDataAPI, token.get("token").toString(), doc);
                            
                                file = new File("./DSWexport/Documents/" + fileName);
                                FusekiComm.createDataset(fusekiServer + "$/datasets", doc);
                                FusekiComm.uploadModel(file, "http://localhost:3030/" + doc);
                                documentsUploaded.add(doc);
                            }
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
}

