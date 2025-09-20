package com.troblecodings.launcher.javafx;

import com.troblecodings.launcher.Launcher;
import com.troblecodings.launcher.assets.Assets;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class LoginScene extends Scene {

	private static StackPane stackpane = new StackPane();

	public LoginScene() {
		super(stackpane);
		Launcher.setupScene(this, stackpane);

		VBox vbox = new VBox();
		vbox.setMaxHeight(400);
		vbox.setMaxWidth(625);
		vbox.setAlignment(Pos.CENTER);
		stackpane.getChildren().add(vbox);

		final Button microsoftLoginButton = new Button();
		microsoftLoginButton.getStyleClass().add("microsoftLoginButton");
		VBox.setMargin(microsoftLoginButton, new Insets(20, 0, 20, 0));
		microsoftLoginButton.setOnAction(event -> {
			Launcher.setScene(Launcher.MICROSOFTLOGINSCENE);
		});

		vbox.getChildren().addAll(microsoftLoginButton);
		
		final ImageView trainImageView = new ImageView(Assets.getImage("train2.png"));
		trainImageView.setTranslateX(760 - trainImageView.getImage().getWidth());
		trainImageView.setTranslateY(325 - trainImageView.getImage().getHeight());
		stackpane.getChildren().add(trainImageView);
	}
}
