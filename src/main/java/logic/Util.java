package logic;

import com.mongodb.DBObject;
import com.mongodb.util.JSON;
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


    public static boolean isValid(DBObject object){

        SQLConCLoud connector = SQLConCLoud.getInstance();
        Medicao medicao;
        try {
            medicao=Medicao.createMedicao(new JSONObject(JSON.serialize(object)));
            if (!connector.isSensorPresent(medicao.getSensor()) || !connector.isZonePresent(medicao.getZone().getId()))
                return false;
            return true;
        }catch (Exception e){
            return false;
        }
    }

    public static boolean isWithinRange(DBObject object) {//adicionar ao mqtt
        Medicao medicao = null;
        try {
            medicao = Medicao.createMedicao(new JSONObject(JSON.serialize(object)));
            Sensor sensor = Main.getINSTANCE().getSensor(medicao.getSensor().getId());
            double value = medicao.getLeitura();
            if (value < sensor.getInfLimit() || value > sensor.getUpperLimit()) {
                return false;
            } else
                return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
