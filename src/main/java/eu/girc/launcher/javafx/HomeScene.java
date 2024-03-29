package eu.girc.launcher.javafx;

import eu.girc.launcher.Launcher;
import eu.girc.launcher.SceneManager;
import eu.girc.launcher.StartupUtil;
import eu.girc.launcher.View;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;

import java.util.Optional;

public class HomeScene extends StackPane {

    private final Button launchButton;

    public HomeScene() {
        SceneManager.setupView(View.HOME, this);
        ImageView imagelogo = new ImageView(Launcher.getImage("logo.png"));

        launchButton = new Button();
        launchButton.getStyleClass().add("launchbutton");
        launchButton.disabledProperty().addListener((obs, old, ne) -> {
            ColorAdjust l = new ColorAdjust(1, -1, 0, 0);
            launchButton.setEffect(l);
            if (!launchButton.isDisabled()) {
                launchButton.setEffect(null);
            }
        });
        launchButton.setOnMouseClicked(this::onLaunchButtonClicked);
        launchButton.setTranslateY(270);

        getChildren().addAll(imagelogo, launchButton);
    }

    private void onLaunchButtonClicked(MouseEvent event) {
        launchButton.setDisable(true);
        new Thread(() -> {
            try {
                final Optional<Process> process = StartupUtil.startClient();
                if (process.isEmpty()) {
                    return;
                }

                Process localProcess = process.get();
                localProcess.onExit().thenRun(() -> Platform.runLater(() -> launchButton.setDisable(false))).get();
            } catch (final Exception e) {
                Launcher.onError(e);
            } finally {
                launchButton.setDisable(false);
            }
        }, "Modded Minecraft Client").start();
    }
}
