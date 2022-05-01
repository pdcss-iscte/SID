package mqtt;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

public class MongoConnection {
	
	public static void main(String[] args) {
		String url = "mongodb://aluno:aluno@194.210.86.10:27017/?authSource=admin";	
//		String url = "mongodb://localhost:27017,localhost:25017,localhost:23017/?replicaSet=replicademo";
//		String url = "mongodb://localhost:27017";
		
		String database = "sid2022";
		String collection = "sensort1";
		
		MongoClient localMongoClient = new MongoClient(new MongoClientURI(url));
	    MongoDatabase localMongoDatabase = localMongoClient.getDatabase(database);
	    MongoCollection<Document> localMongoCollection = localMongoDatabase.getCollection(collection);
	    
	    MongoCursor<Document> cursor = localMongoCollection.find().iterator();
		try {
		    while (cursor.hasNext()) {
		        System.out.println(cursor.next().toJson());
		    }
		} finally {
		    cursor.close();
		}
		
//		Document document = new Document();
//	    document.append("Zona", "Z1");
//	    document.append("Sensor", "T1");
//	    document.append("Data", "01/02/2022");
//	    document.append("Hora", "14:30");
//	    document.append("Medicao", "16.40");
//	    
//	    localMongoCollection.insertOne(document);
	}

}
