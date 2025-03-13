package org.example.songsearchengine;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;

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
    @FXML
    private TableColumn<Song, Void> actionColumn;

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
    }

    private void removeSongFromPlaylist(Song song) {
        if (Backend.deletePlaylist_Songs(playlistName, Integer.parseInt(song.getId()))) {
            songList.remove(song);
        }
    }
}
