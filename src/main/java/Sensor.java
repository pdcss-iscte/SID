public class Sensor {

    private Zone zona;
    private double infLimit;
    private double upperLimit;
    private String id;

    public Sensor(Zone zona, double infLimit, double upperLimit, String id) {
        this.zona = zona;
        this.infLimit = infLimit;
        this.upperLimit = upperLimit;
        this.id = id;
    }


    public Zone getZona() {
        return zona;
    }

    public double getInfLimit() {
        return infLimit;
    }

    public double getUpperLimit() {
        return upperLimit;
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Sensor{" +
                "zona=" + zona +
                ", infLimit=" + infLimit +
                ", upperLimit=" + upperLimit +
                ", id='" + id + '\'' +
                '}';
    }
}
