package com.troblecodings.launcher;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.troblecodings.launcher.assets.Assets;
import com.troblecodings.launcher.javafx.CreditsScene;
import com.troblecodings.launcher.javafx.Footer;
import com.troblecodings.launcher.javafx.Header;
import com.troblecodings.launcher.javafx.HomeScene;
import com.troblecodings.launcher.javafx.LoginScene;
import com.troblecodings.launcher.javafx.OptionsScene;
import com.troblecodings.launcher.util.AuthUtil;
import com.troblecodings.launcher.util.FileUtil;

import javafx.animation.Transition;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

public class Launcher extends Application {

	private static Stage stage;

	public static HomeScene HOMESCENE;
	public static OptionsScene OPTIONSSCENE;
	public static LoginScene LOGINSCENE;
	public static CreditsScene CREDITSSCENE;

	private static Logger LOGGER;

	private static final List<Image> images = new ArrayList<>();

	public static final Logger getLogger() {
		return LOGGER;
	}

	public static final void initializeLogger() {
		if (FileUtil.SETTINGS == null)
			FileUtil.SETTINGS = new FileUtil.SettingsData();

		System.setProperty("app.root", FileUtil.SETTINGS.baseDir);
		LOGGER = LogManager.getLogger("GIRC");
		LOGGER.info("Starting Launcher!");
	}

	public static final Thread SHUTDOWNHOOK = new Thread(FileUtil::saveSettings);

	@Override
	public void start(Stage stage) throws Exception {
		Footer.setProgress(0.001);

		// loading images into list
		images.add(Assets.getImage("background.png"));
		images.add(Assets.getImage("background_2.png"));
		images.add(Assets.getImage("background_3.png"));
		images.add(Assets.getImage("background_4.png"));
		images.add(Assets.getImage("background_5.png"));
		images.add(Assets.getImage("background.png"));

		OPTIONSSCENE = new OptionsScene();
		HOMESCENE = new HomeScene();
		LOGINSCENE = new LoginScene();
		CREDITSSCENE = new CreditsScene();

		Launcher.stage = stage;
		stage.getIcons().add(Assets.getImage("icon.png"));
		stage.setScene(AuthUtil.auth(null, null) == null ? LOGINSCENE : HOMESCENE);
		stage.setWidth(1280);
		stage.setHeight(720);
		stage.initStyle(StageStyle.TRANSPARENT);
		stage.show();
	}

	public static void setupScene(Scene scene, StackPane stackpane) {
		final ImageView backgroundImg = new ImageView();

		Transition animation = new Transition() {
			{
				setCycleDuration(Duration.seconds(10)); // total time for animation
				setRate(0.5); 
				setCycleCount(INDEFINITE);
			}

			@Override
			protected void interpolate(double fraction) {
				int index = (int) (fraction * (images.size() - 1));
				backgroundImg.setImage(images.get(index));
			}
		};

		animation.play();

		stackpane.getChildren().add(backgroundImg);
		stackpane.getChildren().add(new Header(scene));
		stackpane.getChildren().add(new Footer(scene));
		scene.setFill(Color.TRANSPARENT);
		scene.getStylesheets().add(Assets.getStyleSheet("style.css"));
	}

	public static Scene getScene() {
		return stage.getScene();
	}

	public static void setScene(Scene scene) {
		stage.setScene(scene);
	}

	public static Stage getStage() {
		return stage;
	}

	public static void onError(Throwable e) {
		// Decide what to do on error for now log
		if (e == null)
			LOGGER.error("Error found but was passed null!");
		else if (e.getMessage() == null)
			LOGGER.trace("", e);
		else
			LOGGER.trace(e.getMessage(), e);
	}

}
