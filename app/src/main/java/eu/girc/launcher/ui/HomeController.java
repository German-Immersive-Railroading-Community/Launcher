package eu.girc.launcher.ui;

import eu.girc.launcher.SceneManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.layout.VBox;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class HomeController extends VBox implements IController {

    private SceneManager manager;

    @FXML
    private Button launchButton;

    public void initialize() {
        launchButton.setOnMouseClicked(_ -> {
            launchButton.setDisable(true);
            CompletableFuture.runAsync(() -> {
                Platform.runLater(() -> {
                    launchButton.setDisable(false);
                });
            }, CompletableFuture.delayedExecutor(2, TimeUnit.SECONDS));
        });

        launchButton.disabledProperty().addListener((_, _, newValue) -> {
            if (newValue) {
                ColorAdjust colorAdjust = new ColorAdjust(1, -1, 0, 0);
                launchButton.setEffect(colorAdjust);
            } else {
                launchButton.setEffect(null);
            }
        });
    }

    @Override
    public void setSceneManager(SceneManager manager) {
        this.manager = manager;
    }
}
