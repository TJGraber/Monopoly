module com.example.tj_monopoly {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.desktop;


    opens com.example.tj_monopoly to javafx.fxml;
    exports com.example.tj_monopoly;
}