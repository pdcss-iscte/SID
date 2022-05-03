package mqtt;

import java.io.IOException;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import logic.IniReader;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;


public class MqttPublisher {
	private static IMqttClient mqttClient;
	private static DBCursor cursor;
	private final static String cloudServer = "tcp://broker.mqtt-dashboard.com:1883";
	private final static String cloudTopic = "sid2022_g05";
	private final static String clientId = "sid2022_g05";



	public static void mqttConnector(){
		try {
		System.out.println("Connecting to MQTT...");
		mqttClient = new MqttClient(cloudServer,clientId);
		MqttConnectOptions options = new MqttConnectOptions();
		options.setAutomaticReconnect(true);
		options.setCleanSession(true);
		options.setConnectionTimeout(10);
		mqttClient.connect(options);
		System.out.println("Connected!");
		} catch (MqttException e) {
			e.printStackTrace();
		}
	}

	public static void dbConnector() {
		try {
			DB localDB = IniReader.getLocalDatabase();
			DBCollection tempCol = localDB.getCollection("temp");
			cursor = tempCol.find();
			DBCollection measurementsCol = localDB.getCollection("measurement");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static void publish(){
		mqttConnector();
		dbConnector();
		System.out.println("Sending data...");
		while (cursor.hasNext()){
			long start = System.nanoTime();
			DBObject temp = cursor.next();
			String rawMsg = temp.toString();
			byte[] payload = rawMsg.getBytes();
			MqttMessage msg = new MqttMessage(payload);
			msg.setQos(0);
			msg.setRetained(false);
			try {
				mqttClient.publish(cloudTopic,msg);
			} catch (MqttException e) {
				e.printStackTrace();
			}
			System.out.println("Data sended succesfully");
		}
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}
	public static void main(String[] args) throws MqttException, InterruptedException {
	publish();

	}

}
