package applied_computing.setu;

import javafx.animation.TranslateTransition;
import javafx.geometry.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;


public class PrimaryController {
    @FXML
    private Pane pane; // The main Pane containing everything
    @FXML
    private Button startPointButton;
    @FXML
    private Button endPointButton;
    @FXML
    private Button findRouteButton;
    @FXML
    private Button visitedStationsButton;
    @FXML
    private Button avoidingStationsButton;
    @FXML
    private Label startPointLabel;
    @FXML
    private Label endPointLabel;
    @FXML
    private VBox stationsVBox;
    @FXML
    private VBox stationsAvoidVbox;
    private Graph graph=new Graph();
    private boolean isStartPointSelected = false;
    private boolean isMultipleSelectionAllowed = false;
    private boolean isVisitedButtonSelected = false;
    private boolean isAvoidingButtonSelected = false;
    private ArrayList<Station> stationList;
    private HashMap<RadioButton,GraphNode<Station>> stationHashMap;

    @FXML
    private void initialize() {
        setAllRadioButtonsDisabled(true);
        centerMap();
        stationList=StationManager.loadStations();
        stationHashMap=StationManager.getRadioButtonMap(getAllRadioButtons(),stationList);
        connectNodes();
    }


    @FXML
    private RadioButton[] getAllRadioButtons() {
        List<RadioButton> radioButtons = new ArrayList<>();

        for (Node node : pane.getChildren()) {
            if (node instanceof RadioButton) {
                radioButtons.add((RadioButton) node);
            }
        }

        return radioButtons.toArray(new RadioButton[0]);
    }

    private void connectNodes(){
        for (GraphNode<Station> node : stationHashMap.values()) {
            node.setAdjacencyList(getAdjacencyListForANode(node));
        }
    }

    private HashMap<GraphNode<Station>, Double> getAdjacencyListForANode(GraphNode<Station> node) {
        HashMap<GraphNode<Station>, Double> adjacencyList = new HashMap<>();

        Station currentStation = node.getValue();
        if (currentStation == null) {
            System.err.println("Null node in:  " + node.getNodeValue());
            return adjacencyList;
        }

        try (BufferedReader br = new BufferedReader(new FileReader("src/main/resources/applied_computing/setu/adjacencies.csv"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length < 3) continue;

                String from = parts[0].trim();
                String to = parts[1].trim();
                double weight = Double.parseDouble(parts[2].trim());

                if (currentStation.getName().equals(from)) {
                    GraphNode<Station> neighbor = findGraphNodeByName(to);
                    if (neighbor != null) {
                        adjacencyList.put(neighbor, weight);
                    }
                } else if (currentStation.getName().equals(to)) {
                    GraphNode<Station> neighbor = findGraphNodeByName(from);
                    if (neighbor != null) {
                        adjacencyList.put(neighbor, weight);
                    }
                }
            }
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
        }

        return adjacencyList;
    }



    private GraphNode<Station> findGraphNodeByName(String fxId) {
        String stationName = fxId.replace("_", " ");

        for (GraphNode<Station> node : stationHashMap.values()) {
            Station station = node.getValue();
            if (station != null && station.getName().equalsIgnoreCase(stationName)) {
                return node;
            }
        }

        return null; // if no match found
    }



    private void centerMap() {
        pane.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.windowProperty().addListener((obsWindow, oldWindow, newWindow) -> {
                    if (newWindow != null) {
                        Stage stage = (Stage) newWindow;
                        stage.widthProperty().addListener((o, oldWidth, newWidth) -> {
                            double currentWidth = newWidth.doubleValue();
                            double defaultMargin = 1250;
                            double newLeftMargin;
                            if (currentWidth < defaultMargin) {
                                newLeftMargin = 0;
                            } else {
                                newLeftMargin = (currentWidth - defaultMargin) * 0.5;
                            }
                            TranslateTransition translate = new TranslateTransition(Duration.millis(1), pane);
                            translate.setToX(newLeftMargin);
                            translate.play();
                        });
                    }
                });
            }
        });
    }


    public void setAllRadioButtonsDisabled(boolean disable) {
        setRadioButtonsDisabledRecursive(pane, disable);
    }

    private void setRadioButtonsDisabledRecursive(Pane parent, boolean disable) {
        for (Node node : parent.getChildren()) {
            if (node instanceof RadioButton) {
                RadioButton radioButton = (RadioButton) node;
                radioButton.setDisable(disable);
            } else if (node instanceof Pane) {
                Pane nestedPane = (Pane) node;
                setRadioButtonsDisabledRecursive(nestedPane, disable);
            }
        }
    }

    @FXML
    private void selectingOneStation(javafx.event.ActionEvent event) {
        // Disable buttons as needed
        startPointButton.setDisable(true);
        endPointButton.setDisable(true);
        findRouteButton.setDisable(true);

        // Enable radio buttons for station selection
        setAllRadioButtonsDisabled(false);

        // Handle visited and avoiding stations button toggle
        if (event.getSource() == startPointButton) {
            isStartPointSelected = true;
            isMultipleSelectionAllowed = false; // Allow only one selection
        } else if (event.getSource() == endPointButton) {
            isStartPointSelected = false;
            isMultipleSelectionAllowed = false; // Allow only one selection
        } else if (event.getSource() == visitedStationsButton) {
            // Toggle visited stations selection
            if (isVisitedButtonSelected) {
                // Stop selecting
                isVisitedButtonSelected = false;
                visitedStationsButton.setText("Start Selecting Visited Stations");
                setAllRadioButtonsDisabled(true);
                startPointButton.setDisable(false);
                endPointButton.setDisable(false);
                findRouteButton.setDisable(false);
                visitedStationsButton.setDisable(false);
                avoidingStationsButton.setDisable(false);
            } else {
                // Start selecting
                isVisitedButtonSelected = true;
                isMultipleSelectionAllowed = true;
                visitedStationsButton.setText("Stop Selecting");
                avoidingStationsButton.setDisable(true); // Disable avoiding button while selecting
            }
        } else if (event.getSource() == avoidingStationsButton) {
            // Toggle avoiding stations selection
            if (isAvoidingButtonSelected) {
                // Stop selecting
                isAvoidingButtonSelected = false;
                avoidingStationsButton.setText("Start Selecting Avoided Stations");
                setAllRadioButtonsDisabled(true);
                startPointButton.setDisable(false);
                endPointButton.setDisable(false);
                findRouteButton.setDisable(false);
                visitedStationsButton.setDisable(false);
                avoidingStationsButton.setDisable(false);

            } else {
                // Start selecting
                isAvoidingButtonSelected = true;
                isMultipleSelectionAllowed = true;
                avoidingStationsButton.setText("Stop Selecting");
                visitedStationsButton.setDisable(true); // Disable visited button while selecting
            }
        }
    }



    @FXML
    private void oneRadioButtonPressingWaiting(javafx.event.ActionEvent event) {
        // Get the source of the event (the clicked RadioButton)
        RadioButton selectedRadioButton = (RadioButton) event.getSource();

        // Format the station name (replace underscores with spaces)
        String formattedStationName = selectedRadioButton.getId().replace("_", " ");

        VBox targetVBox;
        // Check the source of the event and determine the corresponding VBox
        if (isVisitedButtonSelected) {
            targetVBox = stationsVBox; // Use stationsVBox if visitedStationsButton is pressed
            selectedRadioButton.setStyle("-fx-mark-color: green;");
        }
        else {
            targetVBox = stationsAvoidVbox;
            selectedRadioButton.setStyle("-fx-mark-color: red;");
        }

        // If multiple selection is allowed
        if (isMultipleSelectionAllowed) {
            // Create a new label for the station
            Label stationLabel = new Label(formattedStationName);

            // Check if the RadioButton is selected or deselected
            if (selectedRadioButton.isSelected()) {
                // Add the label to the corresponding VBox (if not already added)
                if (!isLabelInVBox(stationLabel, targetVBox)) {
                    targetVBox.getChildren().add(stationLabel);
                }
            } else {
                // Remove the label from the corresponding VBox if the RadioButton is deselected
                removeLabelFromVBox(formattedStationName, targetVBox);
            }
        } else {
            // If multiple selection is not allowed, update the start or end point label
            if (isStartPointSelected) {
                startPointLabel.setText(formattedStationName);
                selectedRadioButton.setSelected(false);
            } else {
                endPointLabel.setText(formattedStationName);
                selectedRadioButton.setSelected(false);
            }

            // Disable radio buttons when only one selection is allowed
            setAllRadioButtonsDisabled(true);

            // Re-enable buttons after one radio button is selected
            startPointButton.setDisable(false);
            endPointButton.setDisable(false);
            findRouteButton.setDisable(false);
            visitedStationsButton.setDisable(false);
            avoidingStationsButton.setDisable(false);
        }
    }

    // Utility method to check if the label is already in the VBox
    private boolean isLabelInVBox(Label stationLabel, VBox targetVBox) {
        for (javafx.scene.Node node : targetVBox.getChildren()) {
            if (node instanceof Label) {
                Label label = (Label) node;
                if (label.getText().equals(stationLabel.getText())) {
                    return true;
                }
            }
        }
        return false;
    }

    // Utility method to remove a label from the VBox based on its text
    private void removeLabelFromVBox(String labelText, VBox targetVBox) {
        for (javafx.scene.Node node : targetVBox.getChildren()) {
            if (node instanceof Label) {
                Label label = (Label) node;
                if (label.getText().equals(labelText)) {
                    targetVBox.getChildren().remove(label);
                    break;  // Exit the loop once the label is removed
                }
            }
        }
    }

    @FXML
    private void findRoute() {
        String startName = startPointLabel.getText().replace("_", " ").trim();
        String endName = endPointLabel.getText().replace("_", " ").trim();

        RadioButton startButton = null;
        RadioButton endButton = null;

        for (RadioButton rb : getAllRadioButtons()) {
            String stationName = rb.getTooltip().getText().replace("_", " ").trim(); // Normalize name

            if (stationName.equalsIgnoreCase(startName)) {
                startButton = rb;
            }
            if (stationName.equalsIgnoreCase(endName)) {
                endButton = rb;
            }
        }

        if (startButton == null || endButton == null) {
            System.out.println("Start or End station not found!");
            return;
        }

        GraphNode<Station> startNode = stationHashMap.get(startButton);
        GraphNode<Station> endNode = stationHashMap.get(endButton);

        if (startNode == null || endNode == null) {
            System.out.println("Graph nodes not associated with buttons!");
            return;
        }

        ArrayList<ArrayList<GraphNode<Station>>> allPaths = graph.allPathsBetweenNodes(startNode, null, endNode);

        if (allPaths == null || allPaths.isEmpty()) {
            System.out.println("No paths found between stations.");
        } else {
            for (ArrayList<GraphNode<Station>> path : allPaths) {
                System.out.println("Path:");
                for (GraphNode<Station> node : path) {
                    System.out.print(node.getValue().getName() + " -> ");
                }
                System.out.println("END");
            }
        }
    }





}
