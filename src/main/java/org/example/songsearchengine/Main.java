package org.example.songsearchengine;

import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("SignIn.fxml"));
            Scene scene = new Scene(fxmlLoader.load());

            String css = String.valueOf(this.getClass().getResource("/org/example/songsearchengine/app.css"));
            scene.getStylesheets().add(css);

            stage.setTitle("Song Search Engine");
            stage.setResizable(false);

            stage.setScene(scene);
            stage.show();
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        launch();
    }
}