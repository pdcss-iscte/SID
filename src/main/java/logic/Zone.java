package logic;

public class Zone {
    private int id;
    private double temperatura;
    private double humidade;
    private double luz;

    public Zone(int id, double temperatura, double humidade, double luz) {
        this.id = id;
        this.temperatura = temperatura;
        this.humidade = humidade;
        this.luz = luz;
    }

    public int getId() {
        return id;
    }

    public double getTemperatura() {
        return temperatura;
    }

    public double getHumidade() {
        return humidade;
    }

    public double getLuz() {
        return luz;
    }

    @Override
    public String toString() {
        return "logic.Zone{" +
                "id=" + id +
                ", temperatura=" + temperatura +
                ", humidade=" + humidade +
                ", luz=" + luz +
                '}';
    }
}
