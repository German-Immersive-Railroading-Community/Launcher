package com.troblecodings.launcher;

import java.util.ArrayList;
import java.util.List;

import javafx.application.Platform;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.troblecodings.launcher.assets.Assets;
import com.troblecodings.launcher.javafx.CreditsScene;
import com.troblecodings.launcher.javafx.ErrorScene;
import com.troblecodings.launcher.javafx.Footer;
import com.troblecodings.launcher.javafx.Header;
import com.troblecodings.launcher.javafx.HomeScene;
import com.troblecodings.launcher.javafx.LoginScene;
import com.troblecodings.launcher.javafx.MicrosoftLoginScene;
import com.troblecodings.launcher.javafx.MojangLoginScene;
import com.troblecodings.launcher.javafx.OptionsScene;
import com.troblecodings.launcher.javafx.OptionalModsScene;
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
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

public class Launcher extends Application {
	
	private static Stage stage;
	
	public static HomeScene HOMESCENE;
	public static OptionsScene OPTIONSSCENE;
	public static LoginScene LOGINSCENE;
	public static MojangLoginScene MOJANGLOGINSCENE;
	public static MicrosoftLoginScene MICROSOFTLOGINSCENE;
	public static CreditsScene CREDITSSCENE;
	public static OptionalModsScene OPTIONALMODS;
	
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

	private static boolean BETA_MODE = false;

	public static void setBetaMode(boolean betaMode) {
		if(BETA_MODE == betaMode)
			return;

		Launcher.getLogger().info("Changed beta mode: " + BETA_MODE + " → " + betaMode);
		BETA_MODE = betaMode;
	}

	public static boolean getBetaMode() {
		return BETA_MODE;
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
		images.add(images.get(0));
		
		OPTIONSSCENE = new OptionsScene();
		HOMESCENE = new HomeScene();
		LOGINSCENE = new LoginScene();
		MOJANGLOGINSCENE = new MojangLoginScene();
		MICROSOFTLOGINSCENE = new MicrosoftLoginScene();
		CREDITSSCENE = new CreditsScene();
		OPTIONALMODS = new OptionalModsScene();
		
		Launcher.stage = stage;
		stage.getIcons().add(Assets.getImage("icon.png"));
		
		boolean authStatus = AuthUtil.checkSession();
		
		stage.setScene(authStatus ? HOMESCENE : LOGINSCENE);
		
		Header.setVisibility(authStatus);
		
		stage.setWidth(1280);
		stage.setHeight(720);
		stage.initStyle(StageStyle.TRANSPARENT);
		stage.setTitle("GIR Launcher");
		stage.show();
	}
	
	public static void setupScene(Scene scene, StackPane stackpane) {
		final ImageView backgroundImg = new ImageView();
		
		Transition animation = new Transition() {
			
			{
				setCycleDuration(Duration.seconds(20)); // total time for animation
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
	
	public static void onWarn(String string) {
		LOGGER.warn(string);
	}
	
	public static void onError(Throwable e) {
		// Return here since we cannot show any error.
		if (e == null) {
			LOGGER.error("Error found but was passed null!");
			return;
		} else if (e.getMessage() == null)
			LOGGER.trace("", e);
		else
			LOGGER.trace(e.getMessage(), e);
			
		// See if this can be made better, seems overly clunky-like to me, but any other method doesn't generate a stack-trace.
		// toString and getMessage only return the String representation of what the exception actually is.
		if (stage != null && stage.isShowing()) {
			try {
				StringWriter sw = new StringWriter();
				PrintWriter pw = new PrintWriter(sw);
				
				e.printStackTrace(pw);

				ErrorScene errorScene = new ErrorScene(sw.toString(), stage.getScene());

				Platform.runLater(() -> {
					Launcher.setScene(errorScene);
				});
				
				sw.close();
				pw.close();
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
	}
	
}
