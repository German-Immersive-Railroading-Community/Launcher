package eu.girc.launcher.ui;

import eu.girc.launcher.LauncherScene;
import eu.girc.launcher.Resources;
import eu.girc.launcher.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

public class HeaderController extends HBox implements IController {
    @FXML
    private Button homeButton;

    @FXML
    private Button settingsButton;

    private SceneManager sceneManager;

    @FXML
    private ImageView logoImage;

    public void initialize() {
        logoImage.setImage(new Image(Resources.getResourceAsStream("images/girc_logo_w_1.png")));

        homeButton.setOnAction(_ -> navigateTo(LauncherScene.HOME));
        settingsButton.setOnAction(_ -> navigateTo(LauncherScene.SETTINGS));
    }

    private void navigateTo(LauncherScene destination) {
        sceneManager.switchScene(destination);
    }
}
