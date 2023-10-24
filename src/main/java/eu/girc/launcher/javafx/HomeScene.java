package eu.girc.launcher.javafx;

import java.io.IOException;
import java.util.Optional;

import eu.girc.launcher.Launcher;
import eu.girc.launcher.util.StartupUtil;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;

public class HomeScene extends StackPane {

	private final Button launchButton;

	public HomeScene() {
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

				process.get().wait();
			} catch (final IOException | InterruptedException e) {
				Launcher.onError(e);
			} finally {
				launchButton.setDisable(false);
			}
		});
	}

}
