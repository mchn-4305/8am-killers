package org.example.songsearchengine;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("Main.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        String css = String.valueOf(this.getClass().getResource("/org/example/songsearchengine/app.css"));
        scene.getStylesheets().add(css);

        stage.setTitle("Song Search Engine");
        stage.setFullScreen(true);
        stage.setResizable(false);
        
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}