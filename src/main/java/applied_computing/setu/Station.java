package applied_computing.setu;

import java.util.Objects;

public class Station {

    private String name;
    private byte lane;

    public Station(String name, byte lane) {
        this.name = name;
        this.lane = lane;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public byte getLane() {
        return lane;
    }

    public void setLane(byte lane) {
        this.lane = lane;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Station station = (Station) o;
        return lane == station.lane && Objects.equals(name, station.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, lane);
    }

}
