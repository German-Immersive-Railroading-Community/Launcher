package com.troblecodings.launcher;

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

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Launcher extends Application {
	
	private static Stage stage;
	
	public static final HomeScene HOMESCENE = new HomeScene();
	public static OptionsScene OPTIONSSCENE;
	public static final LoginScene LOGINSCENE = new LoginScene();
	public static final CreditsScene CREDITSSCENE = new CreditsScene();
	
	private static Logger LOGGER;
	
	public static final Logger getLogger() {
		return LOGGER;
	}
	
	public static void main(String[] args) {
		Runtime.getRuntime().addShutdownHook(new Thread(FileUtil::saveSettings));
		FileUtil.readSettings();
		System.setProperty("app.root", FileUtil.SETTINGS.baseDir);
		LOGGER = LogManager.getLogger("GIRC");
		LOGGER.info("Starting Launcher!");
		FileUtil.init();
		OPTIONSSCENE = new OptionsScene();
		launch(args);
	}
	
	@Override
	public void start(Stage stage) throws Exception {
		Launcher.stage = stage;
		stage.setScene(AuthUtil.auth(null, null) == null ? LOGINSCENE:HOMESCENE);
		stage.setWidth(1280);
		stage.setHeight(720);
		stage.initStyle(StageStyle.TRANSPARENT);
		stage.show();
	}
	
	public static void setupScene(Scene scene, StackPane stackpane) {
		stackpane.getChildren().add(new Header(scene));
		stackpane.getChildren().add(new Footer());
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
		LOGGER.trace(e.getMessage(), e);
	}
	
}
