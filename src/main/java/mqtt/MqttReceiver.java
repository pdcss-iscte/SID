package mqtt;

import java.util.UUID;

import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttSecurityException;

public class MqttReceiver implements MqttCallback {
	static String cloudServer = "tcp://broker.mqtt-dashboard.com:1883";
	static String cloudTopic = "sid2022_g00";
	static IMqttClient mqttClient;

	public static void main(String[] args) throws InterruptedException, MqttSecurityException, MqttException {
		String clientId = UUID.randomUUID().toString();
		mqttClient = new MqttClient(cloudServer,clientId);
		
		MqttConnectOptions options = new MqttConnectOptions();
		options.setAutomaticReconnect(true);
		options.setCleanSession(true);
		options.setConnectionTimeout(10);
		mqttClient.connect(options);
		
		new MqttReceiver().subscribe();
	}
	
	public void subscribe() throws MqttSecurityException, MqttException {
		mqttClient.setCallback(this);
		mqttClient.subscribe(cloudTopic);
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
	public void messageArrived(String arg0, MqttMessage arg1) throws Exception {
		System.out.println(arg1.toString());
	}

}
