import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SQLConnector {

    private String sqlConnectionUrl = "jdbc:mysql://194.210.86.10";
    private String username = "aluno";
    private String password = "aluno";



    public Connection getConnection(){
        Connection connection = null;
        try{
            connection = DriverManager.getConnection(sqlConnectionUrl,username,password);
            System.out.println("Connection successfull");
        }catch (SQLException e){
            e.printStackTrace();
        }
        return connection;
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
        return temp;
    }

}
