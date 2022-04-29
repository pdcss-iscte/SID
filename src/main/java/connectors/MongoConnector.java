package connectors;

import com.mongodb.*;
import com.mongodb.util.JSON;
import logic.Util;
import org.json.JSONObject;

import java.time.Instant;

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
                System.err.println("Inserted into Error: "+ temp.toString());
                //adaptar formato
                errorCol.insert(temp);
            }
        }
    }

    public void sendToSql(){
        DBCollection tempCol = localDB.getCollection("temp");
        DBCursor cursor = tempCol.find();
        DBCollection measurementsCol = localDB.getCollection("measurement");

        while (cursor.hasNext()){
            DBObject temp = cursor.next();
            sqlConLocal.insertIntoDB(new JSONObject(JSON.serialize(temp)));
            measurementsCol.insert(temp);
            tempCol.remove(temp);
        }
    }





    public static void main(String[] args) {
    }
}
