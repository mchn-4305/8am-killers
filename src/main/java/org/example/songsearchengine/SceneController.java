package org.example.songsearchengine;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class SceneController {
    @FXML
    private TextField searchField;

    @FXML
    private ListView<String> searchResults;

    private Timer timer = new Timer(true);

    public void initialize() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.trim().isEmpty()) {
                updateSearchResults(newValue);
                searchResults.setVisible(true);
            } else {
                searchResults.getItems().clear();
                searchResults.setVisible(false);
            }
        });

        if(searchResults!=null){
            searchResults.setOnMouseClicked(event -> {
                String selectedItem = searchResults.getSelectionModel().getSelectedItem();
                if (selectedItem != null) {
                    searchField.setText(selectedItem);
                    searchResults.setVisible(false);
                }
            });
        }
    }

    private void updateSearchResults(String query) {
        timer.cancel();
        timer = new Timer(true);

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                List<String> results = Backend.searchDB(query);
                Platform.runLater(() -> searchResults.getItems().setAll(results));
            }
        }, 300);
    }

    public void switchScene(ActionEvent event, String fxmlFile){
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource(fxmlFile));
            Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(fxmlLoader.load());
            stage.setScene(scene);
            stage.show();

        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    public void switchToHome(ActionEvent event){
        switchScene(event, "Home.fxml");
    }

    public void switchToPlaylistView(ActionEvent event){
        switchScene(event, "PlaylistView.fxml");
    }

    public void switchToPlaylistForm(ActionEvent event){
        switchScene(event, "PlaylistForm.fxml");
    }
}
