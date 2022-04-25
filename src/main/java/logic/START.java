package logic;

import com.mongodb.*;
import connectors.SQLConLocal;

public class START {

    public static DBCollection getCloudCollection(){
        String cloud = "mongodb://aluno:aluno@194.210.86.10:27017";
        String databaseName = "sid2022";
        String collectionName = "medicoes";

        MongoClient client = new MongoClient(new MongoClientURI(cloud));
        DB db = client.getDB(databaseName);
        DBCollection collection = db.getCollection(collectionName);
        return collection;
    }

    public static DB getLocalDB(){
        String local = "mongodb://localhost:27019,localhost:23019,localhost:25019";
        String databaseName = "stove";

        MongoClient client = new MongoClient(new MongoClientURI(local));
        DB db = client.getDB(databaseName);
        return db;
    }


    public static SQLConLocal getSQLConLocal(){
        String url = "jdbc:mysql://127.0.0.1";
        String user = "admin";
        String password = "admin";

        SQLConLocal con = new SQLConLocal(url, user, password);
        return con;
    }


}
