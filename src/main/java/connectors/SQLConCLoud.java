package connectors;

import logic.Sensor;
import logic.Zone;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;


public class SQLConCLoud extends SQLCon {




    public SQLConCLoud() {
        super("jdbc:mysql://194.210.86.10",  "aluno",  "aluno");

    }


    public List<Zone> getZones(){
        List<Zone> zones = new ArrayList<>();
        Connection con = getConnection();
        String query = "Select * from sid2022.zona";
        try {
            Statement statement = con.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            while(resultSet.next()){
                int id = resultSet.getInt("idzona");
                double temperatura = resultSet.getDouble("temperatura");
                double humidade = resultSet.getDouble("humidade");
                double luz = resultSet.getDouble("luz");
                Zone temp =new Zone(id,temperatura,humidade,luz);
                System.out.println(temp.toString());
                zones.add(temp);
            }
            con.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return zones;
    }

    public List<Sensor> getSensors(List<Zone> zones){
        List<Sensor> sensors = new ArrayList<>();
        Connection con = getConnection();
        String query = "Select * from sid2022.sensor";
        try {
            Statement statement = con.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            while(resultSet.next()){
                int idsensor = resultSet.getInt("idsensor");
                String tipo = resultSet.getString("tipo");
                double limiteinferior = resultSet.getDouble("limiteinferior");
                double limitesuperior = resultSet.getDouble("limitesuperior");
                int idzona = resultSet.getInt("idzona");
                Sensor temp =new Sensor(getZone(idzona,zones),limiteinferior,limitesuperior,tipo+idsensor);
                System.out.println(temp.toString());
                sensors.add(temp);
            }
            con.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return sensors;
    }


    public Zone getZone(int id, List<Zone> zones){
        Zone temp= null;
        for(Zone zone: zones){
            if(zone.getId() == id){
                temp = zone;
                break;
            }
        }
        if(temp == null) throw new IllegalArgumentException("logic.Zone doesn't exist");
        return temp;
    }


    public boolean isSensorPresent(String id){
        Connection connection = getConnection();
        int idsensor = Integer.parseInt(String.valueOf(id.charAt(13)));
        char tipo = id.charAt(12);
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT COUNT(1) from sid2022.sensor where idsensor ="+ idsensor+" and tipo = '"+tipo+"'");

            resultSet.next();
            if(resultSet.getInt(1) == 1)
                return true;
            else
                return false;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }


    public boolean isZonePresent( String id){
        Connection connection = getConnection();
        int idzona = Integer.parseInt(String.valueOf(id.charAt(11)));
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT COUNT(1) from sid2022.zona where idzona ="+idzona);

            resultSet.next();
            if(resultSet.getInt(1) == 1)
                return true;
            else
                return false;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }


        return false;
    }
}
