package com.troblecodings.launcher.javafx;

import com.troblecodings.launcher.Launcher;
import com.troblecodings.launcher.assets.Assets;
import com.troblecodings.launcher.util.StartupUtil;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

public class HomeScene extends Scene {
	
	private static StackPane stackpane = new StackPane();

	public HomeScene() {
		super(stackpane);
		Launcher.setupScene(this, stackpane);
		
		ImageView imagelogo = new ImageView(Assets.getImage("logo.png"));
		
		Button launchbutton = new Button();
		launchbutton.getStyleClass().add("launchbutton");
		launchbutton.setOnAction(event -> {
			if(StartupUtil.start() != null)
				return; // TODO
		});
		launchbutton.setTranslateY(270);
		
		stackpane.getChildren().addAll(imagelogo, launchbutton);
	}
	
}
