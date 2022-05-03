package connectors;

import com.mongodb.*;
import com.mongodb.util.JSON;
import logic.Main;
import logic.Medicao;
import logic.Util;
import mqtt.MQTTPublisher;
import org.bson.types.ObjectId;
import org.json.JSONObject;

import javax.swing.text.Document;
import java.sql.SQLException;
import java.time.Instant;
import static com.mongodb.client.model.Filters.eq;

public class MongoConnector extends Thread {

    private DB localDB;
    private DBCollection cloudCollection;

    private MQTTPublisher publisher;

    private Instant instant;

    private int periodicity;


    public MongoConnector(DB localDB, DBCollection cloudCollection,int periodicity){
        this.publisher = new MQTTPublisher();
        this.cloudCollection = cloudCollection;
        this.localDB = localDB;
        this.periodicity = periodicity;

    }



    @Override
    public void run() {
        while(true){
            sendToMQTT();
            transfer();
            try {
                sleep(periodicity*1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void transfer(){
        instant = logic.Util.getTime();

        BasicDBObject getQuery = new BasicDBObject();
        getQuery.put("Data", new BasicDBObject("$gte", Util.getTimeToString(Util.getTimeMinus(instant,periodicity))).append("$lt", Util.getTimeToString(instant)));

        try {
            sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        DBCursor cursor = cloudCollection.find(getQuery);

        DBCollection tempCol = localDB.getCollection("temp");

        while(cursor.hasNext()) {
            DBObject temp = cursor.next();
            tempCol.insert(temp);

        }
    }

    public void sendToMQTT(){
        DBCollection tempCol = localDB.getCollection("temp");
        DBCursor cursor = tempCol.find();
        DBCollection measurementsCol = localDB.getCollection("measurement");
        while (cursor.hasNext()){
            DBObject temp = cursor.next();
            publisher.send(temp);

            measurementsCol.insert(temp);
            tempCol.remove(temp);
        }
    }


    public static void main(String[] args) {
    }
}
