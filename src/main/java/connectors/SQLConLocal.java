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

    public void insertErrorIntoDB(JSONObject toInsert) throws SQLException{
        PreparedStatement statement = null;

        String query = "INSERT Into estufa.error (Descricao, Hora) VALUES (?,?)";

            statement = getConnection().prepareStatement(query);
            statement.setString(1,toInsert.get("error").toString());
            String date_time = toInsert.get("timestamp").toString();
            String date = date_time.split("T")[0];
            String time = date_time.split("T")[1].replace("Z", "");
            String timestamp_string = date + " " + time;
            Timestamp timestamp = Timestamp.valueOf(timestamp_string);
            statement.setTimestamp(2,timestamp);
            statement.execute();

    }

    public void insertIntoDB(JSONObject toInsert) throws SQLException {
        PreparedStatement statement = null;

            Medicao medicao = Medicao.createMedicao(toInsert);

            String query = "INSERT INTO estufa.medicao (Zona,IDSensor,Hora,Leitura) VALUES (?, ?, ?, ?)";

            statement = getConnection().prepareStatement(query);
            statement.setInt(1,medicao.getZone().getId());
            statement.setString(2,medicao.getSensor().getId());
            statement.setTimestamp(3, medicao.getTimestamp());
            statement.setDouble(4,medicao.getLeitura());
            statement.execute();
            Main.getINSTANCE().add(medicao);

    }
}
