package mqtt;

import com.mongodb.*;
import com.mongodb.util.JSON;
import connectors.SQLConLocal;
import logic.IniReader;
import logic.START;
import logic.Util;
import org.json.JSONObject;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.IOException;
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


    public MongoConnector(DB localDB, DBCollection cloudCollection, int periodicity){

        this.cloudCollection = cloudCollection;
        this.localDB = localDB;
        this.periodicity = periodicity;

    }



    @Override
    public void run() {
        while(true){
            sendToBroker();

        }
    }

    public void transfer(){
        instant = Instant.parse("2022-04-19T15:08:1"+i+"Z");

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
                errorCol.insert(temp);
            }
        }
    }

    public void sendToBroker(){
        System.out.println("Connecting to MQTT...");
        String cloudServer = "tcp://broker.mqtt-dashboard.com:1883";
        String cloudTopic = "sid2022_g05";

        DB localDB= null;
        try {
            localDB = IniReader.getLocalDatabase();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String clientId = "sid2022_g05";
        IMqttClient mqttClient = null;
        try {
        mqttClient = new MqttClient(cloudServer,clientId);
        MqttConnectOptions options = new MqttConnectOptions();
        options.setAutomaticReconnect(true);
        options.setCleanSession(true);
        options.setConnectionTimeout(10);
        mqttClient.connect(options);
        System.out.println("Connected!");
        DBCollection tempCol = localDB.getCollection("temp");
        DBCursor cursor = tempCol.find();
        DBCollection measurementsCol = localDB.getCollection("measurement");
        long elapsedTime=0;
        System.out.println("Sending data...");
        while (cursor.hasNext()){
            long start = System.nanoTime();
            DBObject temp = cursor.next();
            String rawMsg = temp.toString();
            byte[] payload = rawMsg.getBytes();
            MqttMessage msg = new MqttMessage(payload);
            msg.setQos(0);
            msg.setRetained(false);
            mqttClient.publish(cloudTopic,msg);
            elapsedTime= System.nanoTime() - start;
            System.out.println("Data sended succesfully in:" + elapsedTime + "seconds");
        }
            Thread.sleep(2000-elapsedTime);
        } catch (InterruptedException | MqttException e) {
            e.printStackTrace();
        }
    }





    public static void main(String[] args) {
        IniReader.startServers();
        MongoConnector db = new MongoConnector(START.getLocalDB(), START.getCloudCollection(),2);
        db.start();
    }
}
