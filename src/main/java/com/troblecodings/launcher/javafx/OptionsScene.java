package com.troblecodings.launcher.javafx;

import java.awt.Dimension;
import java.awt.Toolkit;

import com.troblecodings.launcher.Launcher;
import com.troblecodings.launcher.util.FileUtil;

import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class OptionsScene extends Scene {

	private static StackPane stackpane = new StackPane();

	public OptionsScene() {
		super(stackpane);
		Launcher.setupScene(this, stackpane);

		final ScrollPane sp = new ScrollPane();
		sp.setHbarPolicy(ScrollBarPolicy.NEVER);
		sp.setVbarPolicy(ScrollBarPolicy.ALWAYS);
		stackpane.getChildren().add(sp);

		final VBox vbox = new VBox();
		vbox.setStyle("");
		sp.setMaxHeight(400);
		sp.setMaxWidth(500);
		sp.setContent(vbox);

		final Label ramlabel = new Label("RAM");
		ramlabel.setStyle("-fx-padding: 0px 0px 10px 0px;");

		final ComboBox<String> ramcombobox = new ComboBox<String>();
		ramcombobox.getItems().addAll("1 GB", "2 GB", "4 GB", "6 GB", "8 GB", "10 GB", "16 GB", "20 GB", "24 GB");
		ramcombobox.setEditable(true);
		final int currentRam = FileUtil.SETTINGS.ram / 1000;
		ramcombobox.getItems().stream().filter(str -> str.startsWith(String.valueOf(currentRam))).findFirst()
				.ifPresent(ramcombobox.getSelectionModel()::select);
		ramcombobox.setOnAction(evt -> {
			try {
				final String ram = ramcombobox.getSelectionModel().getSelectedItem();
				FileUtil.SETTINGS.ram = Integer.valueOf(ram.substring(0, ram.length() - 3)) * 1000;
			} catch (Exception e) {
			}
		});

		final Label resolution = new Label("RESOLUTION");
		resolution.setStyle("-fx-padding: 10px 0px 10px 0px;");

		final ComboBox<String> resolutioncombobox = new ComboBox<String>();
		resolutioncombobox.setEditable(true);
		resolutioncombobox.getEditor().setText(FileUtil.SETTINGS.width + "x" + FileUtil.SETTINGS.height);
		ObservableList<String> list = resolutioncombobox.getItems();
		final Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
		for (int i = 2; i < 7; i++) {
			list.add(dimension.width * 2 / i + "x" + dimension.height * 2 / i);
		}
		resolutioncombobox.setOnAction(evt -> {
			String text = resolutioncombobox.getEditor().getText();
			try {
				if (text.contains("x")) {
					String[] arr = text.split("x");
					FileUtil.SETTINGS.width = Integer.valueOf(arr[0]);
					FileUtil.SETTINGS.height = Integer.valueOf(arr[1]);
				}
			} catch (Exception e) {
			}
		});
		
		final Label baseDir = new Label("DIRECTORY");
		baseDir.setStyle("-fx-padding: 10px 0px 10px 0px;");
		
		final TextField baseDirField = new TextField(FileUtil.SETTINGS.baseDir);
		baseDirField.setOnAction(evtl -> {
			FileUtil.moveBaseDir(baseDirField.getText());
		});
		
		final Button baseDirFinder = new Buten
		
		final HBox box = new HBox(20);
		box.getChildren().addAll(baseDir);

		vbox.getChildren().addAll(ramlabel, ramcombobox, resolution, resolutioncombobox, baseDir, baseDirField);

	}

}
