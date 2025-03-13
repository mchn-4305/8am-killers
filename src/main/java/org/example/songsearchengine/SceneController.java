package org.example.songsearchengine;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import javafx.scene.layout.VBox;

public class SceneController {
    @FXML
    private TextField searchField;
    @FXML
    private ListView<String> searchResults;
    @FXML
    private ScrollPane plscroll;
    @FXML
    private ListView<String> playlistListView;
    @FXML
    private VBox playlistContainer;

    public static HashMap<String, String> playlistIdMap = new HashMap<>();

    private Timer timer = new Timer(true);

    public void initialize() {
        loadPlaylists();
        adjustListViewSize();

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

    private void loadPlaylists(){
        VBox.setVgrow(plscroll, Priority.ALWAYS);

        plscroll.setVisible(false);

        ObservableList<String> playlists = FXCollections.observableArrayList(Backend.getUserPlaylists());

        if(playlists.isEmpty()){
            plscroll.setVisible(false);
            plscroll.setPrefHeight(0);
            return;
        }
        else{
            plscroll.setVisible(true);
        }

        playlistListView.setCellFactory(param -> new ListCell<String>() {
            private final Button playlistButton = new Button();

            @Override
            protected void updateItem(String playlistName, boolean empty) {
                super.updateItem(playlistName, empty);

                if (empty || playlistName == null) {
                    setGraphic(null);
                } else {
                    playlistButton.setText(playlistName);
                    playlistButton.setOnAction(event -> switchToPlaylistView(event, playlistName));
                    playlistButton.setPrefWidth(100.0);
                    setGraphic(playlistButton);
                }
            }
        });
        playlistListView.setItems(playlists);
    }

    private void adjustListViewSize() {
        double newHeight = playlistListView.getItems().size() * 30;
        playlistListView.setPrefHeight(newHeight);
        playlistContainer.setPrefHeight(newHeight);
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

    public void switchToPlaylistView(ActionEvent event, String playlistName){
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("PlaylistView.fxml"));
            Parent root = fxmlLoader.load();

            PlaylistViewController controller = fxmlLoader.getController();
            controller.setPlaylistName(playlistName);

            Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void switchToPlaylistForm(ActionEvent event){
        switchScene(event, "PlaylistForm.fxml");
    }

    public void switchToAlbum(ActionEvent event){
        switchScene(event, "AlbumView.fxml");
    }
}
