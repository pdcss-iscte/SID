package mqtt;

import connectors.SQLConLocal;
import logic.IniReader;
import logic.Medicao;
import logic.Util;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.SQLException;

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
            System.out.println("receiver started");

        } catch (IOException e) {
            e.printStackTrace();
            client = null;
        }
    }

    @Override
    public void connectionLost(Throwable throwable) {
        throwable.printStackTrace();
    }

    @Override
    public void messageArrived(String s, MqttMessage mqttMessage) {
        System.err.println("message arrived");
        JSONObject object = new JSONObject(new String(mqttMessage.getPayload()));
            Medicao temp = Medicao.createMedicao(object);
        try {
            if (Util.isValid(object)) {
                conLocal.insertIntoDB(temp);
            } else {
                conLocal.insertErrorIntoDB(object);
            }
        }catch(SQLException e){
            conLocal.insertAvariaIntoDB(temp);
        }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

    }
}
