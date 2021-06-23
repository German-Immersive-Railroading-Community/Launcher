package com.troblecodings.launcher;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.troblecodings.launcher.assets.Assets;
import com.troblecodings.launcher.javafx.CreditsScene;
import com.troblecodings.launcher.javafx.ErrorScene;
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

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

public class Launcher extends Application {
	
	private static Stage stage;
	
	public static HomeScene HOMESCENE;
	public static OptionsScene OPTIONSSCENE;
	public static LoginScene LOGINSCENE;
	public static CreditsScene CREDITSSCENE;
	
	private static Logger LOGGER;
	
	public static final Logger getLogger() {
		return LOGGER;
	}
	
	public static final void initializeLogger() {
		if(FileUtil.SETTINGS == null)
			FileUtil.SETTINGS = new FileUtil.SettingsData();

		System.setProperty("app.root", FileUtil.SETTINGS.baseDir);
		LOGGER = LogManager.getLogger("GIRC");
		LOGGER.info("Starting Launcher!");
	}
	
	public static final Thread SHUTDOWNHOOK = new Thread(FileUtil::saveSettings);
		
	@Override
	public void start(Stage stage) throws Exception {
		Footer.setProgress(0.001);

		OPTIONSSCENE = new OptionsScene();
		HOMESCENE = new HomeScene();
		LOGINSCENE = new LoginScene();
		CREDITSSCENE = new CreditsScene();

		Launcher.stage = stage;
		stage.getIcons().add(Assets.getImage("icon.png"));
		stage.setScene(AuthUtil.auth(null, null) == null ? LOGINSCENE:HOMESCENE);
		stage.setWidth(1280);
		stage.setHeight(720);
		stage.initStyle(StageStyle.TRANSPARENT);
		stage.setTitle("GIR Launcher");
		stage.show();
	}
	
	public static void setupScene(Scene scene, StackPane stackpane) {
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
		// Return here since we cannot show any error.
		if(e == null)
		{
			LOGGER.error("Error found but was passed null!");
			return;
		}
		else if(e.getMessage() == null)
			LOGGER.trace("", e);
		else
			LOGGER.trace(e.getMessage(), e);

		// See if this can be made better, seems overly clunky-like to me, but any other method doesn't generate a stack-trace.
		// toString and getMessage only return the String representation of what the exception actually is.
		if(stage.isShowing()) {
			try {
				StringWriter sw = new StringWriter();
				PrintWriter pw = new PrintWriter(sw);

				e.printStackTrace(pw);

				ErrorScene errorScene = new ErrorScene(sw.toString(), stage.getScene());
				Launcher.setScene(errorScene);

				sw.close();
				pw.close();
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
	}
	
}
