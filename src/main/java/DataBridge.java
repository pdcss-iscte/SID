import com.mongodb.*;
import org.bson.Document;

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
        //instant = Util.getTime();
        instant = Instant.parse("2022-04-04T23:54:2"+i+"Z");

        MongoClient client = new MongoClient(new MongoClientURI(cloud) );
            DB database = client.getDB("sid2022");
            DBCollection collection = database.getCollection("medicoes");

            BasicDBObject getQuery = new BasicDBObject();
            //getQuery.put("Data", new BasicDBObject("$lt",Util.getTimeToString(Util.getTimeMinus(instant,elapseTime))).append("$gt", "2022-04-04T23:54:35Z"));
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
            while(cursor.hasNext()) {
                DBObject temp = cursor.next();
                //add to local db
                localColection.insert(temp);
                System.out.println(temp);
            }


        }
            lastInstant = instant;

    }

    public static void main(String[] args) {
        DataBridge db = new DataBridge();
        db.start();
    }



}
