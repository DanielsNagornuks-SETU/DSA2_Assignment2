module applied_computing.setu {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;

    opens applied_computing.setu to javafx.fxml;
    exports applied_computing.setu;
}
