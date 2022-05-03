package logic;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import connectors.MongoConnector;
import connectors.SQLConCLoud;
import connectors.SQLConLocal;
import org.ini4j.Ini;

import java.io.FileReader;
import java.io.IOException;

public class IniReader {

    public static Ini loadConfigFile() throws IOException {
        Ini reader = new Ini();
        reader.load(new FileReader("config.ini"));
        return reader;
    }

    private static String[] getSQLLocalFields() throws IOException {
        Ini reader = IniReader.loadConfigFile();
        String sql_host = reader.get("SQL Connection Local", "host", String.class);
        String sql_port = reader.get("SQL Connection Local", "port", String.class);
        String sql_database_name = reader.get("SQL Connection Local", "database_name", String.class);
        String user =  reader.get("SQL Connection Local", "user", String.class);
        String pass =  reader.get("SQL Connection Local", "pass", String.class);
        return new String[] {sql_host, sql_port, sql_database_name,user,pass};
    }

    private static String[] getSQLCloudFields() throws IOException {
        Ini reader = IniReader.loadConfigFile();
        String sql_host = reader.get("SQL Connection Cloud", "host", String.class);
        String sql_database_name = reader.get("SQL Connection Cloud", "database_name", String.class);
        String user = reader.get("SQL Connection Cloud","username", String.class);
        String password = reader.get("SQL Connection Cloud","password", String.class);
        return new String[] {sql_host,user,password, sql_database_name};
    }

    public static SQLConCLoud getSQLConCloud() throws IOException {
        String[] sqlCloudFields = IniReader.getSQLCloudFields();

        String url = "jdbc:mysql://" + sqlCloudFields[0] + "/" + sqlCloudFields[3];

        return new SQLConCLoud(url, sqlCloudFields[1], sqlCloudFields[2]);
    }



    private static String[] getMongoCloudFields() throws IOException {
        Ini reader = IniReader.loadConfigFile();
        String host = reader.get("Cloud Mongo", "host", String.class);
        String port = reader.get("Cloud Mongo", "port", String.class);
        String database_name = reader.get("Cloud Mongo", "database_name", String.class);
        String collections_names = reader.get("Cloud Mongo", "collection_name", String.class);
        String has_password = reader.get("Cloud Mongo", "has_password", String.class);
        String username = reader.get("Cloud Mongo", "username", String.class);
        String password = reader.get("Cloud Mongo", "password", String.class);
        return new String[] {host, port, database_name, collections_names, has_password, username, password};
    }

    public static DB getLocalDatabase() throws IOException {
        Ini reader = IniReader.loadConfigFile();
        String ip1 = reader.get("Local Mongo", "ip1", String.class);
        String port1 = reader.get("Local Mongo", "port2", String.class);
        String ip2 = reader.get("Local Mongo", "ip2", String.class);
        String port2 = reader.get("Local Mongo", "port2", String.class);
        String ip3 = reader.get("Local Mongo", "ip3", String.class);
        String port3 = reader.get("Local Mongo", "port3", String.class);
        String database_name = reader.get("Local Mongo", "database_name", String.class);
        String uri = "mongodb://" + ip1 + ":" + port1 + "," + ip2 + ":" + port2 + "," + ip3 + ":" + port3;
        MongoClient client = new MongoClient(new MongoClientURI(uri));
        return client.getDB(database_name);
    }

    public static void startServers(){
//TO DO
        ProcessBuilder processBuilderMongo = new ProcessBuilder("batchFiles/startimdbreplica.bat");
        ProcessBuilder processBuilderOpenCmd = new ProcessBuilder("batchFiles/startCmd.bat");
        try {
            processBuilderMongo.start();
            processBuilderOpenCmd.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static int getPeriodicity() throws IOException {
        Ini reader = IniReader.loadConfigFile();
        return reader.get("Periodicity", "periodicity",Integer.class);
    }


    public static void connectToMongos(){

        try {

            DB localDatabase = getLocalDatabase();

            String[] configFields = IniReader.getMongoCloudFields();

            boolean has_password = configFields[4].equals("true");
            String uri;
            if (has_password) {
                uri = "mongodb://" + configFields[5] + ":" + configFields[6] + "@" + configFields[0] + ":" + configFields[1] + "/?authSource=admin&authMechanism=SCRAM-SHA-1";
            } else {
                uri = "mongodb://" + configFields[0] + ":" + configFields[1];
            }

            MongoClient client = new MongoClient(new MongoClientURI(uri));

            DB cloudDB = client.getDB(configFields[2]);
            DBCollection cloudCollection = cloudDB.getCollection(configFields[3]);

            SQLConLocal sqlConLocal = getSQLConLocal();

            MongoConnector mongoConnector = new MongoConnector(localDatabase, cloudCollection,getPeriodicity(),sqlConLocal);
            mongoConnector.start();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public static SQLConLocal getSQLConLocal(){
        String[] sqlFields = new String[0];
        try {
            sqlFields = getSQLLocalFields();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String url = "jdbc:mysql://" + sqlFields[0] + ":" + sqlFields[1] + "/" + sqlFields[2];
        //String url = "jdbc:mysql://" + sqlFields[0] + ":" + sqlFields[1] ;
        //automatizar
        String user = sqlFields[3];
        String pass = sqlFields[4];
        return new SQLConLocal(url,user, pass);

    }

}
