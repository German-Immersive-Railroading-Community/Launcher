package com.troblecodings.launcher.javafx;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;

public class Footer extends StackPane {
	
	public Footer() {
		Button button = new Button("Lizensen und Kredits");
		button.setOnAction(event -> onButtonClicked());
		button.setTranslateY(20);
		
		this.getChildren().add(button);
		this.setMaxHeight(85);
		StackPane.setAlignment(this, Pos.BOTTOM_CENTER);
	}
	
	private void onButtonClicked() {
		//TODO Display credits
	}

}
