package logic;

import connectors.SQLConCLoud;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {

    private static Main INSTANCE = null;

    private List<Zone> zones;
    private List<Sensor> sensors;
    private ArrayList<Fila> list;

    private Main(){
        try {
            list = new ArrayList<>();
            SQLConCLoud connector = IniReader.getSQLConCloud();
            zones = connector.getZones();
            sensors = connector.getSensors();
            connector.closeConnection();
            IniReader.connectToMongos();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void add(Medicao medicao){
        for(Fila fila:list){
            if(fila.getName().equals(medicao.getSensor().getId())){
                fila.add(medicao);
                return;
            }
        }
        Fila temp = new Fila(medicao.getSensor().getId());
        temp.add(medicao);
        list.add(temp);

    }

    public Fila getFila(String id){
        for (Fila fila: list){
            if(id.equals(fila.getName())){
                return fila;
            }
        }

        throw new IllegalArgumentException();
    }

    public Zone getZone(int id){
        Zone zone = null;
        for(Zone z:zones){
            if (z.getId() == id){
                zone = z;
            }
        }
        return zone;
    }


    public Sensor getSensor(String id){
        Sensor sensor = null;
        for(Sensor s:sensors){
            if (s.getId().equals(id)){
                sensor = s;
            }
        }
        return sensor;
    }

    public List<Zone> getZones() {
        return zones;
    }

    public List<Sensor> getSensors() {
        return sensors;
    }

    public static Main getINSTANCE(){
        if(INSTANCE==null)
            INSTANCE = new Main();
        return INSTANCE;
    }


    public static void main(String[] args) {
        Main.getINSTANCE();
    }


}
