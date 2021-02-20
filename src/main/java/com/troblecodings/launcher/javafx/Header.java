package com.troblecodings.launcher.javafx;

import com.troblecodings.launcher.Launcher;

import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;

public class Header extends StackPane {
	
	 private static double xOffset = 0;
	 private static double yOffset = 0;
	
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
		initializeEvents();
	}
	
	private void initializeEvents() {
		this.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                xOffset = event.getSceneX();
                yOffset = event.getSceneY();
            }
        });
        this.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Launcher.getStage().setX(event.getScreenX() - xOffset);
                Launcher.getStage().setY(event.getScreenY() - yOffset);
            }
        });
	}
	
	private void onButtonClicked() {
		//TODO Display credits
	}

}
