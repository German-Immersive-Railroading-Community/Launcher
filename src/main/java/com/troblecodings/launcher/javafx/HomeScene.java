package com.troblecodings.launcher.javafx;

import com.troblecodings.launcher.Launcher;
import com.troblecodings.launcher.assets.Assets;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

public class HomeScene extends Scene {
	
	private static StackPane stackpane = new StackPane();

	public HomeScene() {
		super(stackpane);
		Launcher.setupScene(this, stackpane);
		ImageView image = new ImageView(Assets.getImage("logo.png"));
		stackpane.getChildren().add(image);
	}

}
