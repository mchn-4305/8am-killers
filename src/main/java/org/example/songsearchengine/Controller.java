package org.example.songsearchengine;

import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Rectangle;

public class Controller {
    @FXML private AnchorPane scene;
    @FXML private Rectangle searchbar;
    @FXML private Rectangle navbar;

    @FXML
    public void initialize() {
        searchbar.widthProperty().bind(scene.widthProperty());
        navbar.heightProperty().bind(scene.heightProperty().subtract(searchbar.heightProperty()));
    }
}