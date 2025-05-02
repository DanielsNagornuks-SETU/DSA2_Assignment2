package applied_computing.setu;

import javafx.scene.control.RadioButton;

import java.io.*;

import java.util.ArrayList;
import java.util.HashMap;

public class StationManager {

    public static ArrayList<Station> loadStations() {
        ArrayList<Station> stations = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader("src/main/resources/applied_computing/setu/stations.csv"))) {
            String line;
            while ((line = br.readLine()) != null) {
                // Split at the first comma only (station name vs rest)
                int commaIndex = line.indexOf(",");
                if (commaIndex == -1) continue; // Skip malformed lines

                String name = line.substring(0, commaIndex).trim();
                String lanesStr = line.substring(commaIndex + 1).trim();

                // Remove square brackets if present
                lanesStr = lanesStr.replace("[", "").replace("]", "");

                String[] laneTokens = lanesStr.split(",");
                byte[] lanes = new byte[laneTokens.length];

                for (int i = 0; i < laneTokens.length; i++) {
                    lanes[i] = Byte.parseByte(laneTokens[i].trim());
                }

                stations.add(new Station(name, lanes));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return stations;
    }

    public static HashMap<RadioButton, GraphNode<Station>> getRadioButtonMap(RadioButton[] radioButtonArray, ArrayList<Station> stations) {
        HashMap<RadioButton, GraphNode<Station>> radioButtonMap = new HashMap<>();
        for (RadioButton radioButton : radioButtonArray) {
            Station stationFound = null;
            for (Station station : stations) {
                if (station.getName().equals(radioButton.getTooltip().getText())) {
                    stationFound = station;
                    break;
                }
            }
            radioButtonMap.put(radioButton, new GraphNode<>(stationFound));
        }
        return radioButtonMap;
    }

}