package com.troblecodings.launcher.javafx;

import com.troblecodings.launcher.Launcher;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
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
		
		Label ramlabel = new Label("RAM");
		ramlabel.setStyle("-fx-padding: 0px 0px 10px 0px;");

		ComboBox<String> ramcombobox = new ComboBox<String>();
		ramcombobox.getItems().addAll("1 GB", "2 GB", "4GB", "6 GB", "8GB", "10 GB", "16 GB", "20 GB", "24 GB");
		ramcombobox.setPromptText("1 GB");
		
		vbox.getChildren().addAll(ramlabel, ramcombobox);
		
		
	}

}
