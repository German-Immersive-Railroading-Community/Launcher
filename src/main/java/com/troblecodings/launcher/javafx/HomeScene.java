package com.troblecodings.launcher.javafx;

import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

public class HomeScene extends Scene {
	
	private static StackPane stackpane = new StackPane();

	public HomeScene() {
		super(stackpane);
		Label label = new Label("GIR");
		stackpane.getChildren().add(label);
	}

}
