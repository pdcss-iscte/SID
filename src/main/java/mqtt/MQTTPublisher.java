package mqtt;

import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import logic.IniReader;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.json.JSONObject;

import java.io.IOException;

public class MQTTPublisher {

    private MqttClient mqttClient;
    private String topic;

    public MQTTPublisher(){
        try {
            mqttClient = IniReader.getMQTTConnection();
            topic = IniReader.getMQTTTopic();
            System.out.println("publisher started");
        }catch(IOException e) {
            e.printStackTrace();
        }
    }

    public void send(DBObject toSend){

        MqttMessage message = new MqttMessage();
        message.setPayload(JSON.serialize(toSend).getBytes());
        try {
            mqttClient.publish(topic,message);
            System.out.println(toSend);
            System.out.println("message sent");
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

}
