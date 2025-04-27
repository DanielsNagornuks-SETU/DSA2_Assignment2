package applied_computing.setu;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

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
    private boolean isStartPointSelected = false;
    private boolean isMultipleSelectionAllowed = false;// Flag for multiple selection
    private boolean isVisitedButtonSelected = false;
    private boolean isAvoidingButtonSelected = false;

    @FXML
    private void initialize() {
        setAllRadioButtonsDisabled(true);
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
        }
        else {
            targetVBox = stationsAvoidVbox;
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




}
