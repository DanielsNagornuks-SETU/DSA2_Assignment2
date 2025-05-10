package applied_computing.setu;

import javafx.animation.TranslateTransition;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
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
    @FXML
    private ImageView imageView;
    @FXML
    private ChoiceBox<String> selectedModeChoiceBox;
    @FXML
    private Button routeCycleButton;
    @FXML
    private TextField costPenaltyField;
    ArrayList<ArrayList<GraphNode<Station>>> multiplePathArrayList;
    private int pathCounter = 0;
    private Graph graph = new Graph();
    private boolean isStartPointSelected = false;
    private boolean isMultipleSelectionAllowed = false;
    private boolean isVisitedButtonSelected = false;
    private boolean isAvoidingButtonSelected = false;
    private ArrayList<Station> stationList;
    private HashMap<RadioButton, GraphNode<Station>> stationHashMap;
    Map<GraphNode<Station>, RadioButton> reverseMap = new HashMap<>();
    private HashSet<GraphNode<Station>> waypointStations = new HashSet<>();
    private HashSet<GraphNode<Station>> stationsToAvoid = new HashSet<>();

    @FXML
    private void initialize() {
        setAllRadioButtonsDisabled(true);
        centerMap();
        stationList = StationManager.loadStations();
        stationHashMap = StationManager.getRadioButtonMap(getAllRadioButtons(), stationList);
        for (Map.Entry<RadioButton, GraphNode<Station>> entry : stationHashMap.entrySet()) {
            reverseMap.put(entry.getValue(), entry.getKey());
        }
        connectNodes();
        costPenaltyField.setVisible(false);
        routeCycleButton.setVisible(false);
        selectedModeChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue.equals("Select all routes")) {
                routeCycleButton.setVisible(true);
                costPenaltyField.setVisible(false);
                findRoute();
            }
            else if(newValue.equals("Shortest route")) {
                costPenaltyField.setVisible(true);
                routeCycleButton.setVisible(false);
                findRoute();
            }
            else{
                costPenaltyField.setVisible(false);
                routeCycleButton.setVisible(false);
                findRoute();
            }
        });
        costPenaltyField.setOnKeyReleased(event -> {findRoute();});

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

    private void connectNodes() {
        for (Map.Entry<RadioButton, GraphNode<Station>> entry : stationHashMap.entrySet()) {
            GraphNode<Station> node = entry.getValue();
            RadioButton key = entry.getKey();
            node.setAdjacencyList(getAdjacencyListForANode(node));
        }
    }

    private HashMap<GraphNode<Station>, Double> getAdjacencyListForANode(GraphNode<Station> node) {
        HashMap<GraphNode<Station>, Double> adjacencyList = new HashMap<>();

        Station currentStation = node.getValue();
        if (currentStation == null) {
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
            System.out.println("Error reading adjacency list: " + e.getMessage());
        }

        return adjacencyList;
    }


    private GraphNode<Station> findGraphNodeByName(String name) {
        for (GraphNode<Station> node : stationHashMap.values()) {
            Station station = node.getValue();
            if (station != null && station.getName().equalsIgnoreCase(name)) {
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
        selectedModeChoiceBox.setDisable(false);
        clearLines();
        toGrayScale(false);
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
                findRoute();
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
                findRoute();
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
        String formattedStationName = selectedRadioButton.getTooltip().getText().trim();

        VBox targetVBox;
        HashSet<GraphNode<Station>> targetStations;
        // Check the source of the event and determine the corresponding VBox
        if (isVisitedButtonSelected) {
            targetVBox = stationsVBox; // Use stationsVBox if visitedStationsButton is pressed
            targetStations = waypointStations;
            selectedRadioButton.setStyle("-fx-mark-color: green;");
        } else {
            targetVBox = stationsAvoidVbox;
            targetStations = stationsToAvoid;
            selectedRadioButton.setStyle("-fx-mark-color: red;");
        }

        // If multiple selection is allowed
        if (isMultipleSelectionAllowed) {
            // Create a new label for the station
            Label stationLabel = new Label(formattedStationName);
            GraphNode<Station> stationNode = new GraphNode<>(new Station(formattedStationName, new byte[]{}));
            // Check if the RadioButton is selected or deselected
            if (selectedRadioButton.isSelected()) {
                // Add the label to the corresponding VBox (if not already added)
                if (!isLabelInVBox(stationLabel, targetVBox)) {
                    targetVBox.getChildren().add(stationLabel);
                    targetStations.add(stationNode);
                }
            } else {
                // Remove the label from the corresponding VBox if the RadioButton is deselected
                removeLabelFromVBox(formattedStationName, targetVBox);
                targetStations.remove(stationNode);
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
        findRoute();
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
        String startName = startPointLabel.getText();
        String endName = endPointLabel.getText();

        RadioButton startButton = null;
        RadioButton endButton = null;

        for (RadioButton rb : getAllRadioButtons()) {
            String stationName = rb.getTooltip().getText(); // Normalize name

            if (stationName.equalsIgnoreCase(startName)) {
                startButton = rb;
            }
            if (stationName.equalsIgnoreCase(endName)) {
                endButton = rb;
            }
        }

        if (startButton == null || endButton == null) {
            return;
        }

        GraphNode<Station> startNode = stationHashMap.get(startButton);
        GraphNode<Station> endNode = stationHashMap.get(endButton);

        if (startNode == null || endNode == null) {
            return;
        }
        if(selectedModeChoiceBox.getValue().equals("Select all routes")){
            multiplePathArrayList = graph.allPathsBetweenNodes(startNode, null, endNode);
            drawLines(multiplePathArrayList.get(0));
        }
        else if(selectedModeChoiceBox.getValue().equals("Least nodes")){
            ArrayList<GraphNode<Station>> startPath = new ArrayList<>();
            startPath.add(startNode);
            Queue<ArrayList<GraphNode<Station>>> partialPaths = new LinkedList<>();
            partialPaths.add(startPath);

            ArrayList<GraphNode<Station>> shortest = graph.shortestPathByNodes(partialPaths, null, endNode);
            drawLines(shortest);
        } else if (selectedModeChoiceBox.getValue().equals("Shortest route")) {
            if(costPenaltyField.getText().isEmpty()){
                drawLines(graph.shortestPathBetweenStations(startNode, endNode, 0, waypointStations, stationsToAvoid));

            }
            else{
                drawLines(graph.shortestPathBetweenStations(startNode, endNode, Double.parseDouble(costPenaltyField.getText()), waypointStations, stationsToAvoid));
            }}
        toGrayScale(true);
    }

    @FXML
    private void cycleMultiplePaths() {
        if (multiplePathArrayList == null || multiplePathArrayList.isEmpty()) return;
        drawLines(multiplePathArrayList.get(pathCounter));
        pathCounter++;
        if (pathCounter >= multiplePathArrayList.size()) {
            pathCounter = 0;
        }
    }

    @FXML
    private void drawLines(ArrayList<GraphNode<Station>> solutionPath) {
        clearLines();
        if(solutionPath == null || solutionPath.isEmpty()) return;
        for (int i = 0; i < solutionPath.size() - 1; i++) {
            GraphNode<Station> fromNode = solutionPath.get(i);
            GraphNode<Station> toNode = solutionPath.get(i + 1);
            RadioButton fromButton = reverseMap.get(fromNode);
            RadioButton toButton = reverseMap.get(toNode);
            if (fromButton != null && toButton != null) {
                double startX = fromButton.getLayoutX() + fromButton.getWidth() / 2;
                double startY = fromButton.getLayoutY() + fromButton.getHeight() / 2;
                double endX = toButton.getLayoutX() + toButton.getWidth() / 2;
                double endY = toButton.getLayoutY() + toButton.getHeight() / 2;
                Line line = new Line(startX, startY, endX, endY);
                line.setStroke(Color.RED);
                line.setStrokeWidth(5);
                pane.getChildren().add(line);
                line.setMouseTransparent(true);
            }
        }
    }

    @FXML
    private void clearLines() {
        pane.getChildren().removeIf(node -> node instanceof Line);
    }

    private void toGrayScale(boolean state) {
        if(state) {
            imageView.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("grayImage.png"))));
        }
        else{
            imageView.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("U-Bahn_Wien.png"))));
        }
    }


}
