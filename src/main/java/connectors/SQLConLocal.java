package connectors;

import org.json.JSONObject;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

public class SQLConLocal extends SQLCon{

    private String url ;
    private String user;
    private String password;

    /*
    private String sqlConnectionLocal = "jdbc:mysql://127.0.0.1";
    private String localUsername = "admin";
    private String localPassword = "admin";
    */

    public SQLConLocal(String url, String user, String password) {
        super(url, user, password);
        this.url = url;
        this.user = user;
        this.password = password;
    }

    public void insertIntoDB(JSONObject toInsert){
        try {
            int id_zone = Integer.parseInt(String.valueOf(toInsert.get("Zona").toString().charAt(1)));
            String sensor = toInsert.get("Sensor").toString();
            String date_time = toInsert.get("Data").toString();
            double value = Double.parseDouble(toInsert.get("Medicao").toString());
            String date = date_time.split("T")[0];
            String time = date_time.split("T")[1].replace("Z", "");
            String timestamp_string = new String (date + " " + time);
            Timestamp timestamp = Timestamp.valueOf(timestamp_string);

            String query = "INSERT INTO estufa.medicao (Zona,IDSensor,Hora,Leitura) VALUES (?, ?, ?, ?)";

            PreparedStatement statement = getConnection().prepareStatement(query);
            statement.setInt(1,id_zone);
            statement.setString(2,sensor);
            statement.setTimestamp(3, timestamp);
            statement.setDouble(4,value);

            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
