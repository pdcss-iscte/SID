import java.util.List;

public class Main {

    private List<Zone> zones;
    private List<Sensor> sensors;

    public void init(){
        SQLConnector connector = new SQLConnector();
        zones = connector.getZones();
        sensors = connector.getSensors(zones);

    }



    public static void main(String[] args) {
        Main main = new Main();
        main.init();
    }


}
