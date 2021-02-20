package com.troblecodings.launcher;

import com.troblecodings.launcher.assets.Assets;
import com.troblecodings.launcher.javafx.HomeScene;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Launcher extends Application {
	
	private static Stage stage;

	public static void main(String[] args) {
		launch(args);
	}
	
	@Override
	public void start(Stage stage) throws Exception {
		Launcher.stage = stage;
		stage.setScene(new HomeScene());
		stage.setWidth(1280);
		stage.setHeight(720);
		stage.initStyle(StageStyle.TRANSPARENT);
		stage.show();
	}
	
	public static void setupScene(Scene scene, StackPane stackpane) {
		//TODO setHeader
		//TODO setFooter
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
	
}
