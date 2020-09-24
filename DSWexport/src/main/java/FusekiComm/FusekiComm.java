package FusekiComm;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.jena.query.DatasetAccessor;
import org.apache.jena.query.DatasetAccessorFactory;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;

import DSWexport.HTTPRequests;

public class FusekiComm {
	
	/**
	 * Verfies if a given Dataset already exists
	 * @param serviceURI Fuseki Server Datasets URL
	 * @param DatasetName New Dataset Name
	 * @return True if exists, False if does not
	 */
	public static Boolean datasetExists(String serviceURI) throws IOException {

		Integer statusCode = HTTPRequests.GETRequestStatus(serviceURI, null, null);

		if (statusCode == 200) {
			return true;
		}

		return false;
	}

	/**
	 * Deletes the data associated with a given Dataset
	 * @param serviceURI Fuseki Server Datasets URL
	 * @param DatasetName Dataset Name
	 */
	public static void deleteDataFromDataset(String serviceURI, String DatasetName) throws IOException {
		
	}



	/**
	 * Creates a new empty Dataset in Fuseki Server
	 * @param serviceURI Fuseki Server Datasets URL
	 * @param DatasetName New Dataset Name
	 * @throws IOException
	 */
    public static void createDataset(String serviceURI, String DatasetName) throws IOException {
		String newDataset = "?dbName=" + DatasetName + "&dbType=tdb";
		try {
			HTTPRequests.POSTRequest(serviceURI + newDataset, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
	/**
	 * Upload a file for a given Dataset
	 * @param file File to be uploaded
	 * @param serviceURI Fuseki Server Dataset URL
	 * @throws IOException
	 */
    public static void uploadModel(File file, String serviceURI)
			throws IOException {

		String fileName = file.getName();
		Model m = ModelFactory.createDefaultModel();
		Boolean updateFlag = false;

		switch (fileName.substring(fileName.length() - 3)) {
			case "rdf":
				try (FileInputStream in = new FileInputStream(file)) {
					m.read(in, null, "RDF/XML");
				}
				updateFlag = true;
				break;
				
			case "ttl":
				try (FileInputStream in = new FileInputStream(file)) {
					m.read(in, null, "Turtle");
				}
				updateFlag = true;
				break;

			default:
				System.out.println("File must be rdf or ttl");
				break;
		}

		if (updateFlag) {
			// upload the resulting model
			DatasetAccessor accessor = DatasetAccessorFactory
				.createHTTP(serviceURI);
			accessor.putModel(m);
			updateFlag = false;
		}
    }
}