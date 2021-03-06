package connectors;

import com.mongodb.*;
import com.mongodb.util.JSON;
import logic.Main;
import logic.Medicao;
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

    private int periodicity;


    public MongoConnector(DB localDB, DBCollection cloudCollection,int periodicity,SQLConLocal sqlConLocal){

        this.cloudCollection = cloudCollection;
        this.localDB = localDB;
        this.periodicity = periodicity;
        this.sqlConLocal = sqlConLocal;

    }



    @Override
    public void run() {
        while(true){
            sendErrorToSQL();
            sendToSql();
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
        DBCollection errorCol = localDB.getCollection("error");

        while(cursor.hasNext()) {
            DBObject temp = cursor.next();

            if(Util.isValid(temp)){
                if(Util.isWithinRange(temp)) {
                    tempCol.insert(temp);
                    System.out.println("Inserted into Temp: " + temp.toString());
                }else{
                    Medicao medicao = null;
                    try {
                        medicao = Medicao.createMedicao(new JSONObject(JSON.serialize(temp)));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    sqlConLocal.insertIntoAvaria(medicao);
                }
            }else{
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
                Main.getINSTANCE().add(Medicao.createMedicao(new JSONObject(JSON.serialize(temp))));
            } catch (Exception throwables ) {
                System.out.println("nao enviei");
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
                e.printStackTrace();
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

    public static void main(String[] args) {
    }
}
