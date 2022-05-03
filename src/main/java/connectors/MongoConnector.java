package connectors;

import com.mongodb.*;
import com.mongodb.util.JSON;
import logic.Util;
import org.bson.types.ObjectId;
import org.json.JSONObject;

import javax.swing.text.Document;
import java.sql.SQLException;
import java.time.Instant;
import static com.mongodb.client.model.Filters.eq;

public class MongoConnector extends Thread {

    /*private String cloud = "mongodb://aluno:aluno@194.210.86.10:27017";
    private String local = "mongodb://localhost:27019,localhost:23019,localhost:25019";
    */
    private DB localDB;
    private DBCollection cloudCollection;

    private SQLConLocal sqlConLocal;

    private Instant instant;
    private int i = 0;

    private int periodicity;


    public MongoConnector(DB localDB, DBCollection cloudCollection,int periodicity,SQLConLocal sqlConLocal){

        this.cloudCollection = cloudCollection;
        this.localDB = localDB;
        this.periodicity = periodicity;
        this.sqlConLocal = sqlConLocal;

    }



    @Override
    public void run() {
        while(i<20){
            sendErrorToSQL();
            sendToSql();
            transfer();
            i +=periodicity;
            try {
                sleep(periodicity*1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void transfer(){
        //instant = logic.Util.getTime();
        if(i<10)
            instant = Instant.parse("2022-04-26T09:38:2"+i+"Z");
        else {
            int t = i-10;
            instant = Instant.parse("2022-04-26T09:38:3" + t + "Z");
        }
        BasicDBObject getQuery = new BasicDBObject();
        getQuery.put("Data", new BasicDBObject("$gte", Util.getTimeToString(Util.getTimeMinus(instant,periodicity))).append("$lt", Util.getTimeToString(instant)));

        try {
            sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        DBCursor cursor = cloudCollection.find(getQuery);

        DBCollection tempCol = localDB.getCollection("temp");
        DBCollection errorCol = localDB.getCollection("error");

        while(cursor.hasNext()) {
            DBObject temp = cursor.next();

            if(Util.isValid(temp)){
                tempCol.insert(temp);
                System.out.println("Inserted into Temp: " +temp.toString());

            }else{
                //adaptar formato
                sendErrorToMongo(temp,errorCol);
            }
        }
    }

    public void sendToSql(){
        DBCollection tempCol = localDB.getCollection("temp");
        DBCursor cursor = tempCol.find();
        DBCollection measurementsCol = localDB.getCollection("measurement");
        DBCollection errorCol = localDB.getCollection("error");
        while (cursor.hasNext()){
            DBObject temp = cursor.next();
            try {
                sqlConLocal.insertIntoDB(new JSONObject(JSON.serialize(temp)));
            } catch (SQLException throwables) {
                sendErrorToMongo(temp,errorCol);

            }
            measurementsCol.insert(temp);
            tempCol.remove(temp);
        }
    }


    public void sendErrorToMongo(DBObject temp,DBCollection erroCol){
        DBObject document = new BasicDBObject();
        document.put("error",temp);
        document.put("timestamp",Util.getTimeToString(Util.getTime()));
        document.put("sent", false);
        erroCol.insert(document);
        System.err.println("Inserted into Error: "+ temp.toString());


    }

    public void sendErrorToSQL(){
        DBCollection erroCol = localDB.getCollection("error");
        BasicDBObject getQuery = new BasicDBObject();
        getQuery.put("sent", false);
        DBCursor cursor = erroCol.find(getQuery);


        while(cursor.hasNext()){
            DBObject temp = cursor.next();
            try {
                sqlConLocal.insertErrorIntoDB(new JSONObject(JSON.serialize(temp)));
                String id = temp.get("_id").toString();
                changeError(id, erroCol);
            }catch (SQLException e){

            }
        }
    }


    public void changeError(String id,DBCollection errorCol){
        System.err.println(id);
        BasicDBObject query = new BasicDBObject();
        query.put("_id", new ObjectId(id));

        BasicDBObject newDocument = new BasicDBObject();
        newDocument.put("sent", "true");

        BasicDBObject updateObject = new BasicDBObject();
        updateObject.put("$set", newDocument);

        errorCol.update(query, updateObject);
        System.err.println("changed to true");
    }


    /*
    BasicDBObject query = new BasicDBObject();
query.put("name", "Shubham");

BasicDBObject newDocument = new BasicDBObject();
newDocument.put("name", "John");

BasicDBObject updateObject = new BasicDBObject();
updateObject.put("$set", newDocument);

collection.update(query, updateObject);
     */




    public static void main(String[] args) {
    }
}
