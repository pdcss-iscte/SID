package mqtt;

import connectors.SQLConLocal;
import logic.IniReader;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONObject;

import java.io.IOException;

public class MQTTReceiver extends Thread implements MqttCallback {

    private MqttClient client;
    private SQLConLocal conLocal;

    public MQTTReceiver(SQLConLocal conLocal){
        this.conLocal = conLocal;
    }

    @Override
    public void run() {
        try {
            client = IniReader.getMQTTConnection();
            assert client != null;
            client.setCallback(this);
        } catch (IOException e) {
            e.printStackTrace();
            client = null;
        }
    }

    @Override
    public void connectionLost(Throwable throwable) {

    }

    @Override
    public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
        conLocal.insertIntoDB(new JSONObject(new String(mqttMessage.getPayload())));
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

    }
}
