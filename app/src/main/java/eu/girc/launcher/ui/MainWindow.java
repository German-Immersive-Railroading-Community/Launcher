package eu.girc.launcher.ui;

import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;

public class MainWindow extends AnchorPane {
    @FXML
    public GridPane contentGridPane;

    public void initialize() {
        contentGridPane.setPrefSize(1920, 1080);
    }
}
