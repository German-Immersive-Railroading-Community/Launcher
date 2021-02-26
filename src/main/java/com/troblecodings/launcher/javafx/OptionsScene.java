package com.troblecodings.launcher.javafx;

import com.troblecodings.launcher.Launcher;
import com.troblecodings.launcher.util.FileUtil;

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
		ramcombobox.getItems().addAll("1 GB", "2 GB", "4 GB", "6 GB", "8 GB", "10 GB", "16 GB", "20 GB", "24 GB");
		final int currentRam = FileUtil.SETTINGS.ram / 1000;
		ramcombobox.getItems().stream().filter(str -> str.startsWith(String.valueOf(currentRam))).findFirst()
				.ifPresent(ramcombobox.getSelectionModel()::select);
		ramcombobox.setOnAction(evt -> {
			final String ram = ramcombobox.getSelectionModel().getSelectedItem();
			FileUtil.SETTINGS.ram = Integer.valueOf(ram.substring(0, ram.length() - 3)) * 1000;
		});

		vbox.getChildren().addAll(ramlabel, ramcombobox);

	}

}
