package connectors;

import logic.IniReader;
import logic.Main;
import logic.Medicao;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

public class SQLConLocal extends SQLCon{


    public SQLConLocal(String url, String user, String password) {
        super(url, user, password);

    }


    public void insertIntoAvaria(Medicao medicao) {
        Connection con = getConnection();
        PreparedStatement statement = null;
        try {
            String insertMedicao = "insert into avariasensor (Zona, IDSensor, Hora, Leitura) values (?,?,?,?);";
            statement = con.prepareStatement(insertMedicao);
            statement.setInt(1,medicao.getZone().getId());
            statement.setString(2,medicao.getSensor().getId());
            statement.setTimestamp(3,medicao.getTimestamp());
            statement.setDouble(4,medicao.getLeitura());
            statement.execute();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void insertErrorIntoDB(JSONObject toInsert) throws SQLException{
        PreparedStatement statement = null;

        String query = "INSERT Into error (Descricao, Hora) VALUES (?,?)";

            statement = getConnection().prepareStatement(query);
            statement.setString(1,toInsert.get("error").toString());
            String date_time = toInsert.get("timestamp").toString();
            String date = date_time.split("T")[0];
            String time = date_time.split("T")[1].replace("Z", "");
            String timestamp_string = date + " " + time;
            Timestamp timestamp = Timestamp.valueOf(timestamp_string);
            statement.setTimestamp(2,timestamp);
        System.out.println(statement.toString());
            statement.execute();

    }

    public void insertIntoDB(Medicao medicao) throws SQLException {
        PreparedStatement statement = null;


            String query = "INSERT INTO medicao (Zona,IDSensor,Hora,Leitura) VALUES (?, ?, ?, ?)";

            statement = getConnection().prepareStatement(query);
            statement.setInt(1,medicao.getZone().getId());
            statement.setString(2,medicao.getSensor().getId());
            statement.setTimestamp(3, medicao.getTimestamp());
            statement.setDouble(4,medicao.getLeitura());
            statement.execute();
    }
}
