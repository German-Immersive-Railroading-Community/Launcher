package com.troblecodings.launcher.javafx;

import com.troblecodings.launcher.Launcher;

import javafx.application.Platform;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.StackPane;

public class Footer extends StackPane {
	
	private static SimpleDoubleProperty bar = new SimpleDoubleProperty();
	
	public Footer() {
		ProgressBar progressbar = new ProgressBar();
		progressbar.setTranslateY(-10);
		progressbar.setPrefWidth(1280);
		bar.addListener((x, x2, x3) -> progressbar.setProgress(bar.get()));

		Button button = new Button("Lizensen und Kredits");
		button.setOnAction(event -> onButtonClicked());
				
		this.getChildren().addAll(button, progressbar);
		
		this.setMaxHeight(85);
		StackPane.setAlignment(button, Pos.BOTTOM_CENTER);
		StackPane.setAlignment(progressbar, Pos.TOP_LEFT);
		StackPane.setAlignment(this, Pos.BOTTOM_LEFT);
	}
	
	private void onButtonClicked() {
		Launcher.setScene(Launcher.CREDITSSCENE);
	}
	
	public static void setProgress(double progress) {
		System.out.println(progress);
		Platform.runLater(() -> { System.out.println(progress); bar.set(progress);});
	}

}
