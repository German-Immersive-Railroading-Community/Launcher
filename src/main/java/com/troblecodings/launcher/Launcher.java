package com.troblecodings.launcher;

import com.troblecodings.launcher.javafx.HomeScene;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

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
		stage.show();
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
