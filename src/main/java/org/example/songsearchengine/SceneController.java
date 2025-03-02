package org.example.songsearchengine;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SceneController {
    private Stage stage;
    private Scene scene;

    public void switchToHome(ActionEvent event){
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("Home.fxml"));
            stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            scene = new Scene(fxmlLoader.load());
            stage.setScene(scene);
            stage.show();

        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    public void switchToPlaylistView(ActionEvent event){
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("PlaylistView.fxml"));
            stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            scene = new Scene(fxmlLoader.load());
            stage.setScene(scene);
            stage.show();

        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }
}
