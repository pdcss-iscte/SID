import com.mongodb.*;

public class MongoToJava {

    private String mongo = "mongodb://localhost:27019,localhost:23019,localhost:25019";


    public void transfer(){
        MongoClient client = new MongoClient(new MongoClientURI(mongo));
        DB database = client.getDB("stove");
        DBCollection collection = database.getCollection("temp");

        DBCursor cursor = collection.find();

        while (cursor.hasNext()){
            DBObject object = cursor.next();
        }



    }

}
