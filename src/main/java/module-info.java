module org.example.songsearchengine {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens org.example.songsearchengine to javafx.fxml;
    exports org.example.songsearchengine;
}