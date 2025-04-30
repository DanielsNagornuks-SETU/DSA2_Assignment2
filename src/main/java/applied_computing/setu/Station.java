package applied_computing.setu;

import java.util.Arrays;
import java.util.Objects;

public class Station {

    private String name;
    private byte[] lanes;

    public Station(String name, byte[] lanes) {
        this.name = name;
        this.lanes = lanes;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public byte[] getLanes() {
        return lanes;
    }

    public void setLanes(byte[] lanes) {
        this.lanes = lanes;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Station station = (Station) o;
        return Objects.equals(name, station.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, Arrays.hashCode(lanes));
    }

    @Override
    public String toString() {
        String lanesStr = "";
        for (byte lane : lanes) {
            lanesStr += lane + " ";
        }
        return "Station{" +
                "name='" + name + '\'' +
                ", lanes=" + lanesStr +
                '}';
    }

}
