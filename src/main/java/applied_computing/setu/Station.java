package applied_computing.setu;

import java.util.LinkedList;
import java.util.Objects;

public class Station {

    private String name;
    private LinkedList<Byte> lanes;

    public Station(String name, LinkedList<Byte> lanes) {
        this.name = name;
        this.lanes = lanes;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LinkedList<Byte> getLanes() {
        return lanes;
    }

    public void setLanes(LinkedList<Byte> lanes) {
        this.lanes = lanes;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Station station = (Station) o;
        return Objects.equals(name, station.name) && Objects.equals(lanes, station.lanes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, lanes);
    }

}
