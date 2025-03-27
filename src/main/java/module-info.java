module com.example.clocks {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.clocks to javafx.fxml;
    exports com.example.clocks;
}