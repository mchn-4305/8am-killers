package org.example.songsearchengine;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import java.util.List;

public class HomeController extends SceneController {
    @FXML
    private TableView<Song> songsTable;
    @FXML
    private TableColumn<Song, String> songIdColumn;
    @FXML
    private TableColumn<Song, String> songNameColumn;
    @FXML
    private TableColumn<Song, String> artistColumn;

    @FXML
    private TableView<Artist> artistsTable;
    @FXML
    private TableColumn<Artist, String> artistNameColumn;

    @FXML
    private TableView<Album> albumsTable;
    @FXML
    private TableColumn<Album, String> albumNameColumn;

    @FXML
    public void initialize() {
        super.initialize();
        populateSongsTable();
        populateArtistsTable();
        populateAlbumsTable();
    }

    private void populateSongsTable() {
        songIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        songNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        artistColumn.setCellValueFactory(new PropertyValueFactory<>("artist"));

        List<String> songsData = Backend.selectTop20Songs();
        ObservableList<Song> songsList = FXCollections.observableArrayList();

        for (String songData : songsData) {
            String[] parts = songData.split(": ");
            String id = parts[0];
            String[] titleAndArtist = parts[1].split(" by ");
            String title =titleAndArtist[0];
            String artist = titleAndArtist[1];

            songsList.add(new Song(id, title, artist));
        }

        songsTable.setItems(songsList);
    }

    private void populateArtistsTable() {
        artistNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        List<String> artistsData = Backend.selectTop20Artists();
        ObservableList<Artist> artistsList = FXCollections.observableArrayList();

        for (String artist : artistsData) {
            artistsList.add(new Artist(artist));
        }

        artistsTable.setItems(artistsList);
    }

    private void populateAlbumsTable() {
        albumNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        List<String> albumsData = Backend.selectTop20Albums();
        ObservableList<Album> albumsList = FXCollections.observableArrayList();

        for (String album : albumsData) {
            albumsList.add(new Album(album));
        }

        albumsTable.setItems(albumsList);
    }
}
