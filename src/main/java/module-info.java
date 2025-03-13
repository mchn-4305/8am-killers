module org.example.songsearchengine {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires jbcrypt;
    requires mysql.connector.j;


    opens org.example.songsearchengine to javafx.fxml;
    exports org.example.songsearchengine;
}