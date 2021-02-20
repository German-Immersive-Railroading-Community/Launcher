package com.troblecodings.launcher.javafx;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;

public class Header extends StackPane {
	
	public Header() {
		Button optionsbutton = new Button();
		optionsbutton.getStyleClass().add("optionsbutton");
		optionsbutton.setOnAction(event -> onButtonClicked());
		
		Button closebutton = new Button();
		closebutton.getStyleClass().add("closebutton");
		closebutton.setOnAction(event -> System.exit(0));
		closebutton.setTranslateX(-20);
		closebutton.setTranslateY(20);
		StackPane.setAlignment(closebutton, Pos.TOP_RIGHT);
		
		this.getChildren().addAll(optionsbutton, closebutton);
		this.setMaxHeight(85);
		StackPane.setAlignment(this, Pos.TOP_CENTER);
	}
	
	private void onButtonClicked() {
		//TODO Display credits
	}

}
