package com.troblecodings.launcher.javafx;

import com.troblecodings.launcher.Launcher;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class OptionsScene extends Scene {
	
	private static StackPane stackpane = new StackPane();

	public OptionsScene() {
		super(stackpane);
		Launcher.setupScene(this, stackpane);
		
		VBox vbox = new VBox();
		vbox.setMaxHeight(400);
		vbox.setMaxWidth(500);
		stackpane.getChildren().add(vbox);
	}

}
