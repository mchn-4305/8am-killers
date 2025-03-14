package org.example.songsearchengine;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;


public class PlaylistFormController extends SceneController{
    @FXML
    private TextField playlistNameField;
    @FXML
    private Button submitBtn;

    public void submit(ActionEvent event){
        String playlistName = playlistNameField.getText();
        Backend.insertPlaylist(playlistName);
        switchToPlaylistView(event, playlistName);
    }
    @FXML
    public void initialize() {
        super.initialize();
    }
}