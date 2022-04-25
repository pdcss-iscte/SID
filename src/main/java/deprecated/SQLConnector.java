/*package deprecated;

import org.json.JSONObject;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SQLConnector {

    private String sqlConnectionUrl = "jdbc:mysql://194.210.86.10";
    private String cloudUsername = "aluno";
    private String cloudPassword = "aluno";

    private String sqlConnectionLocal = "jdbc:mysql://127.0.0.1";
    private String localUsername = "admin";
    private String localPassword = "admin";

    public Connection getConnectionToCloud(){
        Connection connection = null;
        try{
            connection = DriverManager.getConnection(sqlConnectionUrl, cloudUsername, cloudPassword);
            //System.out.println("Connection successfull");
        }catch (SQLException e){
            e.printStackTrace();
        }
        return connection;
    }


    public Connection getConnectionToLocal(){
        Connection connection = null;
        try{
            connection = DriverManager.getConnection(sqlConnectionLocal,localUsername,localPassword);
            System.out.println("Connection successfull");
        }catch (SQLException e){
            e.printStackTrace();
        }
        return connection;
    }

    public List<logic.Zone> getZones(){
        List<logic.Zone> zones = new ArrayList<>();
        Connection con = getConnectionToCloud();
        String query = "Select * from sid2022.zona";
        try {
            Statement statement = con.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            while(resultSet.next()){
                int id = resultSet.getInt("idzona");
                double temperatura = resultSet.getDouble("temperatura");
                double humidade = resultSet.getDouble("humidade");
                double luz = resultSet.getDouble("luz");
                logic.Zone temp =new logic.Zone(id,temperatura,humidade,luz);
                System.out.println(temp.toString());
                zones.add(temp);
            }
            con.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return zones;
    }

    public List<logic.Sensor> getSensors(List<logic.Zone> zones){
        List<logic.Sensor> sensors = new ArrayList<>();
        Connection con = getConnectionToCloud();
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
                logic.Sensor temp =new logic.Sensor(getZone(idzona,zones),limiteinferior,limitesuperior,tipo+idsensor);
                System.out.println(temp.toString());
                sensors.add(temp);
            }
            con.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return sensors;
    }


    public logic.Zone getZone(int id, List<logic.Zone> zones){
        logic.Zone temp= null;
        for(logic.Zone zone: zones){
            if(zone.getId() == id){
                temp = zone;
                break;
            }
        }
        if(temp == null) throw new IllegalArgumentException("logic.Zone doesn't exist");
        return temp;
    }


    public boolean isSensorPresent(String id){
        Connection connection = getConnectionToCloud();
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
        Connection connection = getConnectionToCloud();
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


    public void insertIntoDB(JSONObject toInsert){
        try {
            int id_zone = Integer.parseInt(String.valueOf(toInsert.get("Zona").toString().charAt(1)));
            String sensor = toInsert.get("logic.Sensor").toString();
            String date_time = toInsert.get("Data").toString();
            double value = Double.parseDouble(toInsert.get("Medicao").toString());
            String date = date_time.split("T")[0];
            String time = date_time.split("T")[1].replace("Z", "");
            String timestamp_string = new String (date + " " + time);
            Timestamp timestamp = Timestamp.valueOf(timestamp_string);

            String query = "INSERT INTO estufa.medicao (Zona,IDSensor,Hora,Leitura) VALUES (?, ?, ?, ?)";

            PreparedStatement pstmt = getConnectionToLocal().prepareStatement(query);
            pstmt.setInt(1,id_zone);
            pstmt.setString(2,sensor);
            pstmt.setTimestamp(3, timestamp);
            pstmt.setDouble(4,value);

            pstmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}
*/