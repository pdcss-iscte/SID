package mqtt;

import java.util.Random;
import java.util.UUID;

import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MqttPublisher {

	public static void main(String[] args) throws MqttException, InterruptedException {
		String cloudServer = "tcp://broker.mqtt-dashboard.com:1883";
		String cloudTopic = "sid2022_g00";
		
		String clientId = UUID.randomUUID().toString();
		IMqttClient mqttClient = new MqttClient(cloudServer,clientId);
		
		MqttConnectOptions options = new MqttConnectOptions();
		options.setAutomaticReconnect(true);
		options.setCleanSession(true);
		options.setConnectionTimeout(10);
		mqttClient.connect(options);
		
		for (;;) {
			Random rnd = new Random();
			double temp =  10 + rnd.nextDouble() * 15.0;
			String tempString = String.format("%04.2f",temp);
			String rawMsg = "Zona:Z1; Sensor:T1; Data:01/02/2022; Hora:14h30; Medicao:" + tempString;
		    byte[] payload = rawMsg.getBytes();
		    
		    MqttMessage msg = new MqttMessage(payload);
		    msg.setQos(0);
		    msg.setRetained(false);
		    mqttClient.publish(cloudTopic,msg);
		    
		    Thread.sleep(2000);
		}
	}

}
