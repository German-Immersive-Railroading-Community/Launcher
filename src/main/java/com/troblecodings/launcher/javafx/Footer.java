package com.troblecodings.launcher.javafx;

import com.troblecodings.launcher.Launcher;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.StackPane;

public class Footer extends StackPane {
	
	private static ProgressBar progressBar = null;
	
	public Footer() {
		if(progressBar == null) {
			progressBar = new ProgressBar();
			progressBar.setTranslateY(20);
			StackPane.setAlignment(progressBar, Pos.BOTTOM_LEFT);
		}
		
		Button button = new Button("Lizensen und Kredits");
		button.setOnAction(event -> onButtonClicked());
		
		this.getChildren().addAll(button, progressBar);
		this.setMaxHeight(85);
		StackPane.setAlignment(this, Pos.BOTTOM_LEFT);
		StackPane.setAlignment(button, Pos.BOTTOM_CENTER);
	}
	
	private void onButtonClicked() {
		Launcher.setScene(Launcher.CREDITSSCENE);
	}
	
	public static void setProgress(double progress) {
		progressBar.setProgress(progress);
	}

}
