import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

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

}
