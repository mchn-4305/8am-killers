package org.example.songsearchengine;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.text.BreakIterator;
import java.util.List;

public class ArtistPageController extends SceneController {

    @FXML
    private Label artistNameLabel;
    @FXML
    private TableView<Album> ArtistTable;
    @FXML
    private TableColumn<Album, String> ArtistAlbumIdColumn;
    @FXML
    private TableColumn<Album, String> ArtistAlbumNameColumn;


    private String artistName;
    private ObservableList<Album> albumsList = FXCollections.observableArrayList();

    public void setArtist(String artistName) {
        this.artistName = artistName;
        if (artistNameLabel != null) {
            artistNameLabel.setText(artistName);
        } else {
            System.out.println("❌ artistNameLabel is NULL");
        }
    }

    public void setAlbums(List<Album> albums) {
        if (ArtistTable != null) {
            albumsList.setAll(albums);
            ArtistTable.setItems(albumsList);
            ArtistTable.refresh();// ✅ Ensure table updates
        } else {
            System.out.println("❌ ArtistTable is NULL");
        }
    }

    @FXML
    public void initialize() {
        super.initialize();
        ArtistAlbumIdColumn.setCellValueFactory(new PropertyValueFactory<>("albumId"));
        ArtistAlbumNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        // If albums were set before initialize(), apply them now
        if (!albumsList.isEmpty()) {
            ArtistTable.setItems(albumsList);
        }
    }

}
