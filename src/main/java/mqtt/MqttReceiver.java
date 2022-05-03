package mqtt;

import java.sql.SQLException;
import java.util.Stack;
import java.util.UUID;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import connectors.SQLConLocal;
import logic.IniReader;
import logic.Util;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttSecurityException;
import org.json.JSONObject;

public class MqttReceiver implements MqttCallback {
	static String cloudServer = "tcp://broker.mqtt-dashboard.com:1883";
	static String cloudTopic = "sid2022_g05";
	static String clientId = "sid2022_g05";
	static IMqttClient mqttClient;
	static SQLConLocal sql;



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
	private void subscribe() throws MqttSecurityException, MqttException {
		mqttClient.setCallback(this);
		mqttClient.subscribe(cloudTopic);
	}

	public static  void recieve(){
		sql = IniReader.getSQLConLocal();
		mqttConnector();
		try {
		new MqttReceiver().subscribe();
		System.out.println("Recieving data...");
		} catch(MqttException e){
			e.printStackTrace();
		}
			
	}



	public static void main(String[] args) throws InterruptedException, MqttSecurityException, MqttException {
		recieve();

	}
	


	@Override
	public void connectionLost(Throwable arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deliveryComplete(IMqttDeliveryToken arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void messageArrived(String arg0, MqttMessage message) throws Exception {
		System.out.println("Inserting: " + message.toString()+ "into SqlDB...");
		try {
			sql.insertIntoDB(new JSONObject(JSON.serialize(message)));
		} catch (SQLException throwables) {
			sql.insertErrorIntoDB(new JSONObject(JSON.serialize(message)));
		}
		System.out.println("Inserted!");

	}
	

}
