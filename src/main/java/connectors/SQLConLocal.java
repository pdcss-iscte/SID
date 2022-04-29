package connectors;

import logic.Main;
import logic.Medicao;
import org.json.JSONObject;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

public class SQLConLocal extends SQLCon{
    /*
    private String sqlConnectionLocal = "jdbc:mysql://127.0.0.1";
    private String localUsername = "admin";
    private String localPassword = "admin";
    */

    public SQLConLocal(String url, String user, String password) {
        super(url, user, password);

    }



    public void insertIntoDB(JSONObject toInsert){
        PreparedStatement statement = null;
        try {
            /*
            int id_zone = Integer.parseInt(String.valueOf(toInsert.get("Zona").toString().charAt(1)));
            String sensor = toInsert.get("Sensor").toString();
            String date_time = toInsert.get("Data").toString();
            double value = Double.parseDouble(toInsert.get("Medicao").toString());
            String date = date_time.split("T")[0];
            String time = date_time.split("T")[1].replace("Z", "");
            String timestamp_string = new String (date + " " + time);
            Timestamp timestamp = Timestamp.valueOf(timestamp_string);
*/
            Medicao medicao = Medicao.createMedicao(toInsert);

            String query = "INSERT INTO estufa.medicao (Zona,IDSensor,Hora,Leitura) VALUES (?, ?, ?, ?)";

            statement = getConnection().prepareStatement(query);
            statement.setInt(1,medicao.getZone().getId());
            statement.setString(2,medicao.getSensor().getId());
            statement.setTimestamp(3, medicao.getTimestamp());
            statement.setDouble(4,medicao.getLeitura());
            statement.execute();
            Main.getINSTANCE().add(medicao);

        } catch (SQLException e) {
            System.out.println(statement.toString());
            e.printStackTrace();
        }
    }
}
