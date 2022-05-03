package connectors;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;

public abstract class SQLCon {

    private String url;
    private String user;
    private String password;

    private Connection connection;


    public SQLCon(String url, String user, String password) {
        this.url = url;
        this.user = user;
        this.password = password;

        try {
            connection = DriverManager.getConnection(this.url,this.user,this.password);
            System.out.println("Connection successfull");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public String getUrl() {
        return url;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    public Connection getConnection() {
        return connection;
    }

    public void closeConnection(){
        try {
            connection.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}
