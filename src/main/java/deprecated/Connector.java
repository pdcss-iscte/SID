package deprecated;

import com.mongodb.BasicDBObject;
import com.mongodb.client.*;
import org.bson.Document;

import javax.print.Doc;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

//to do querries
//https://www.mongodb.com/developer/quickstart/java-setup-crud-operations/?utm_campaign=javainsertingdocuments&utm_source=facebook&utm_medium=organic_social



public class Connector {

    private static String cloud = "mongodb://aluno:aluno@194.210.86.10:27017";
    private static String local = "mongodb://localhost:27019";
    private static String lastTime;
    private static String Time;



    public static void connection(){

        FindIterable<Document> iterDoc;

        try(MongoClient client = MongoClients.create(cloud)){

            MongoDatabase database = client.getDatabase("sid2022");
            MongoCollection<Document> collection = database.getCollection("medicoes");
            iterDoc= collection.find();

            Iterator it = iterDoc.iterator();
            /*
            while (it.hasNext())
                System.out.println( it.next().toString());
*/

            try(MongoClient clientLocal = MongoClients.create(local)) {
                MongoDatabase databaselocal = clientLocal.getDatabase("stove");
                MongoCollection<Document> collectionlocal = databaselocal.getCollection("temp");
                while (it.hasNext()) {
                    Document temp = (Document) it.next();
                    System.out.println(temp.toString());
                    //collectionlocal.insertOne(temp);
                    break;
                }

            }

  //          return iterDoc;
        }


    }

    public static void insert(FindIterable<Document> toInsert){
        try(MongoClient client = MongoClients.create(local)) {
            MongoDatabase database = client.getDatabase("stove");
            MongoCollection<Document> collection = database.getCollection("temp");
            Iterator it = toInsert.iterator();
            while (it.hasNext())
                collection.insertOne((Document) it.next());

        }
        }

    public static void main(String[] args) {
        //insert(connection());
        connection();
    }

}
