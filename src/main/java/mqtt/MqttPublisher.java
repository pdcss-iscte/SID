package mqtt;

import java.sql.Timestamp;
import java.util.Random;
import java.util.Timer;
import java.util.UUID;

import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONObject;

public class MqttPublisher {

	public static void main(String[] args) throws MqttException, InterruptedException {
		String cloudServer = "tcp://broker.mqtt-dashboard.com:1883";
		String cloudTopic = "sid2022_g05";
		
		String clientId = "sid2022_g05";
		IMqttClient mqttClient = new MqttClient(cloudServer,clientId);
		
		MqttConnectOptions options = new MqttConnectOptions();
		options.setAutomaticReconnect(true);
		options.setCleanSession(true);
		options.setConnectionTimeout(10);
		mqttClient.connect(options);

		DBCollection tempCol = localDB.getCollection("temp");
		DBCursor cursor = tempCol.find();
		DBCollection measurementsCol = localDB.getCollection("measurement");

		long elapsedTime=0;
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

		}
		Thread.sleep(2000-elapsedTime);


	}

}
