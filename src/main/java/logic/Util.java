package logic;

import com.mongodb.DBObject;
import connectors.SQLConCLoud;
import org.json.JSONObject;

import java.io.IOException;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class Util {

    public static String getTimeToString(Instant time){
        DateTimeFormatter dtf = DateTimeFormatter.ISO_INSTANT;
        return dtf.format(time);
    }

    public static Instant getTime(){
        return Instant.now().truncatedTo(ChronoUnit.SECONDS);
    }

    public static Instant getTimeMinus(Instant time, int sec){
        return time.minusSeconds(sec);
    }

    public static void main(String[] args) {
        System.out.println(getTimeToString(getTime()));

        System.out.println(getTimeToString(getTimeMinus(getTime(),2)));
    }


    public static boolean isValid(JSONObject object){
        SQLConCLoud connector = null;
        connector = SQLConCLoud.getInstance();
        String[] temp = object.toString().split(",");
        if(temp.length != 5 || !connector.isSensorPresent(temp[3]) || !connector.isZonePresent(temp[0])) return false;
        return true;

    }

}
