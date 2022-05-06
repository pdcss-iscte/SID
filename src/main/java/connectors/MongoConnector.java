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
    private SQLConLocal sqlConLocal;
    private MQTTPublisher publisher;

    private Instant instant;

    private int periodicity;


    public MongoConnector(DB localDB, DBCollection cloudCollection,int periodicity,SQLConLocal sqlConLocal){
        this.sqlConLocal = sqlConLocal;
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
            if(Util.isValid(new JSONObject(JSON.serialize(temp)))){
                if(Util.isWithinRange(temp)) {
                    tempCol.insert(temp);
                    System.out.println("Inserted into Temp: " + temp.toString());
                }else{
                    Medicao medicao = null;
                    medicao = Medicao.createMedicao(new JSONObject(JSON.serialize(temp)));
                    sqlConLocal.insertIntoAvaria(medicao);
                }
            }else{
                sendErrorToMongo(temp);
            }
        }
    }

    public void sendErrorToMongo(DBObject temp){
        DBObject document = new BasicDBObject();
        document.put("error",temp);
        document.put("timestamp",Util.getTimeToString(Util.getTime()));
        document.put("sent", false);
        try {
            sqlConLocal.insertErrorIntoDB(new JSONObject(JSON.serialize(temp)));
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        System.err.println("Inserted into Error: "+ temp.toString());


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
