package org.example.songsearchengine;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;

public class PlaylistViewController extends SceneController {
    @FXML
    private Label playlistNameLabel;
    @FXML
    private TableView<Song> songsTable;
    @FXML
    private TableColumn<Song, String> songIdColumn;
    @FXML
    private TableColumn<Song, String> songNameColumn;
    @FXML
    private TableColumn<Song, String> artistColumn;

    private String playlistName;

    public void setPlaylistName(String name) {
        this.playlistName = name;
        playlistNameLabel.setText(name);
        loadSongs();
    }

    private void loadSongs() {
        List<Song> songs = Backend.getAllPlaylistSongs(playlistName);
        ObservableList<Song> songList = FXCollections.observableArrayList(songs);
        songsTable.setItems(songList);
    }

    @FXML
    public void initialize() {
        super.initialize();
        songIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        songNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        artistColumn.setCellValueFactory(new PropertyValueFactory<>("artist"));
    }
}
