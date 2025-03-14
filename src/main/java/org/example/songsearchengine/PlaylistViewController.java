package org.example.songsearchengine;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.List;

public class PlaylistViewController extends SceneController {
    @FXML
    private Label playlistNameLabel;
    @FXML
    private TableView<Song> songsTable;
    @FXML
    private TableView<Song> AddSongTable;
    @FXML
    private TableColumn<Song, String> songIdColumn;
    @FXML
    private TableColumn<Song, String> songNameColumn;
    @FXML
    private TableColumn<Song, String> artistColumn;
    @FXML
    private TableColumn<Song, Void> actionColumn;
    @FXML
    private TableColumn<Song, String> AddSongsongIdColumn;

    @FXML
    private TableColumn<Song, String> AddSongsongnameColumn;

    @FXML
    private TableColumn<Song, String> AddSongartistColumn;


    @FXML
    private VBox songSelection;



    private ObservableList<Song> songList;

    private String playlistName;

    public void setPlaylistName(String name) {
        this.playlistName = name;
        playlistNameLabel.setText(name);
        loadSongs();
    }

    private void loadSongs() {
        List<Song> songs = Backend.getAllPlaylistSongs(playlistName);
        songList = FXCollections.observableArrayList(songs);
        songsTable.setItems(songList);
    }

    @FXML
    public void initialize() {
        super.initialize();
        songIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        songNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        artistColumn.setCellValueFactory(new PropertyValueFactory<>("artist"));
        actionColumn.setCellFactory(param -> new TableCell<>() {
            private final Button removeButton = new Button("Remove");

            {
                removeButton.setOnAction(event -> {
                    Song song = getTableView().getItems().get(getIndex());
                    removeSongFromPlaylist(song);
                });

                removeButton.setStyle("-fx-background-color: red; -fx-text-fill: white;");
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(new HBox(removeButton));
                }
            }
        });

        // Setup columns for AddSongTable
        AddSongsongIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        AddSongsongnameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        AddSongartistColumn.setCellValueFactory(new PropertyValueFactory<>("artist"));

        // Load available songs into AddSongTable
        loadAvailableSongs();
    }

    private void removeSongFromPlaylist(Song song) {
        if (Backend.deletePlaylist_Songs(playlistName, Integer.parseInt(song.getId()))) {
            songList.remove(song);
        }
    }

    @FXML
    private void showSongSelection() {
        songSelection.setVisible(true);
    }

    @FXML
    private void hideSongSelction(){
        songSelection.setVisible(false);
    }

    private void loadAvailableSongs(){
        List<Song> availableSongs = Backend.getAllAvailableSongs(); // Fetch songs from backend
        ObservableList<Song> songObservableList = FXCollections.observableArrayList(availableSongs);
        AddSongTable.setItems(songObservableList);
    }

    @FXML
    private void handleTableSelection(MouseEvent event) {
        if (event.getClickCount() == 2) { // Detects double-click
            Song selectedSong = AddSongTable.getSelectionModel().getSelectedItem();
            if (selectedSong != null) {
                // ✅ Insert into database first
                boolean success = Backend.insertPlaylist_Songs(playlistName, Integer.parseInt(selectedSong.getId()));

                if (success) {
                    // ✅ If successful, add song to the playlist table and remove it from available songs
                    songsTable.getItems().add(selectedSong);
                    AddSongTable.getItems().remove(selectedSong);
                } else {
                    showAlert("Error", "Failed to add song to the playlist.");
                }
            }
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
