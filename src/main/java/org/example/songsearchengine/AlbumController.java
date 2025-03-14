package org.example.songsearchengine;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;

public class AlbumController extends SceneController{

    @FXML
    private Label albumviewAlbumName;
    @FXML
    private Label albumviewArtistName;
    @FXML
    private TableView<Song> AlbumTable;
    @FXML
    private TableColumn<Song, String> AlbumSongID;
    @FXML
    private TableColumn<Song, String> AlbumSong;

    private String albumName; // Stores selected album

    public void setAlbum(String albumName) {
        this.albumName = albumName;
        albumviewAlbumName.setText(albumName);
        String artistName = Backend.getArtistByAlbum(albumName);
        albumviewArtistName.setText(artistName != null ? artistName : "Unknown Artist");
        loadAlbumSongs(); // Load songs once album is set
    }

    @FXML
    public void initialize() {
        super.initialize();
        AlbumSongID.setCellValueFactory(new PropertyValueFactory<>("id"));
        AlbumSong.setCellValueFactory(new PropertyValueFactory<>("name"));
    }

    private void loadAlbumSongs() {
        if (albumName != null) {
            List<Song> songs = Backend.getAllAlbumSongs(albumName);
            AlbumTable.getItems().setAll(songs);
        }
    }
}
