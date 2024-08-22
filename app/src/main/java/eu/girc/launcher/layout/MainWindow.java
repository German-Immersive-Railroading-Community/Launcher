package eu.girc.launcher.layout;

import atlantafx.base.controls.ModalPane;
import eu.girc.launcher.utils.NodeUtils;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;

public class MainWindow extends AnchorPane {

    @FXML
    public StackPane body;
    
    @FXML
    public ModalPane modalPane;

    public MainWindow() {
    }
    
    public void initialize() {
        NodeUtils.setAnchors(body, Insets.EMPTY);
    }
}
