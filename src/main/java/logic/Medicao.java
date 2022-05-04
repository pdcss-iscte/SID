package logic;

import org.json.JSONObject;

import java.sql.Time;
import java.sql.Timestamp;

public class Medicao {

        private Zone zone;
        private Sensor sensor;
        private Timestamp timestamp;
        private double leitura;
        private boolean error=false;

    public Medicao(Zone zone, Sensor sensor, Timestamp timestamp, double leitura) {
        this.zone = zone;
        this.sensor = sensor;
        this.timestamp = timestamp;
        this.leitura = leitura;
    }

    public static Medicao createMedicao(JSONObject toInsert) throws Exception{
        int id_zone = Integer.parseInt(String.valueOf(toInsert.get("Zona").toString().charAt(1)));
        String id_sensor = toInsert.get("Sensor").toString();
        double value = Double.parseDouble(toInsert.get("Medicao").toString());
        String date_time = toInsert.get("Data").toString();
        String date = date_time.split("T")[0];
        String time = date_time.split("T")[1].replace("Z", "");
        String timestamp_string = date + " " + time;
        Timestamp timestamp = Timestamp.valueOf(timestamp_string);

        Zone zone = Main.getINSTANCE().getZone(id_zone);
        Sensor sensor = Main.getINSTANCE().getSensor(id_sensor);


        return new Medicao(zone,sensor,timestamp,value);
    }


    public boolean isError() {
        return error;
    }

    public void setError(boolean isError){
        error =isError;
    }

    public Zone getZone() {
        return zone;
    }

    public Sensor getSensor() {
        return sensor;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public double getLeitura() {
        return leitura;
    }
}
