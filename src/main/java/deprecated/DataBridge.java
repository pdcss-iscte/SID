package deprecated;

import com.mongodb.*;
import com.mongodb.util.JSON;
import connectors.SQLConLocal;
import logic.Util;
import org.json.JSONObject;

import java.time.Instant;


public class DataBridge extends Thread{

    private String cloud = "mongodb://aluno:aluno@194.210.86.10:27017";
    private String local = "mongodb://localhost:27019,localhost:23019,localhost:25019";

    private int elapseTime = 2;
    private int i = 0;

    private Instant lastInstant;
    private Instant instant;







    @Override
    public void run() {
        while(i<10){
            transfer();

            i +=elapseTime;
            try {
                sleep(elapseTime*1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void transfer(){
        //instant = logic.Util.getTime();
        instant = Instant.parse("2022-04-19T15:08:1"+i+"Z");

        MongoClient client = new MongoClient(new MongoClientURI(cloud) );
            DB database = client.getDB("sid2022");
            DBCollection collection = database.getCollection("medicoes");

            BasicDBObject getQuery = new BasicDBObject();
            //getQuery.put("Data", new BasicDBObject("$lt",logic.Util.getTimeToString(logic.Util.getTimeMinus(instant,elapseTime))).append("$gt", "2022-04-04T23:54:35Z"));
            getQuery.put("Data", new BasicDBObject("$gte",Util.getTimeToString(Util.getTimeMinus(instant,elapseTime))).append("$lt", Util.getTimeToString(instant)));
        try {
            sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        DBCursor cursor = collection.find(getQuery);

        try(MongoClient localClient = new MongoClient(new MongoClientURI(local));){
            DB localDatabase = localClient.getDB("stove");
            DBCollection localColection = localDatabase.getCollection("temp");
            DBCollection errorCollection = localDatabase.getCollection("error");
                System.out.println("entrei");
            while(cursor.hasNext()) {
                DBObject temp = cursor.next();


                if(Util.isValid(temp)){
                    localColection.insert(temp);
                    JSONObject output = new JSONObject(JSON.serialize(temp));
                    SQLConLocal con = new SQLConLocal("jdbc:mysql://127.0.0.1", "admin", "admin");
                    con.insertIntoDB(output);
                    System.out.println("Inserted into Temp: " +temp.toString());

                }else{
                    System.err.println("Inserted into Error: "+ temp.toString());
                    errorCollection.insert(temp);
                }
            }


        }
            lastInstant = instant;

    }



    public static void main(String[] args) {
        DataBridge db = new DataBridge();
        db.start();
    }



}
