package com.troblecodings.launcher.javafx;

import com.troblecodings.launcher.Launcher;
import com.troblecodings.launcher.util.AuthUtil;
import com.troblecodings.launcher.util.FileUtil;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class LoginScene extends Scene {
	
	private static StackPane stackpane = new StackPane();

	public LoginScene() {
		super(stackpane);
		Launcher.setupScene(this, stackpane);
		
		VBox vbox = new VBox();
		vbox.setMaxHeight(340);
		vbox.setMaxWidth(500);
		stackpane.getChildren().add(vbox);
		
		Label usernamelabel = new Label("Username");
		usernamelabel.setStyle("-fx-padding: 0px 0px 10px 0px;");
		TextField textfield = new TextField();
		Label passwordlabel = new Label("Password");
		PasswordField passwordfield = new PasswordField();
		Button loginbutton = new Button();
		loginbutton.getStyleClass().add("loginbutton");
		loginbutton.setOnAction(event -> {
			if((FileUtil.DEFAULT = AuthUtil.auth(textfield.getText(), passwordfield.getText())) != null)
				Launcher.setScene(Launcher.HOMESCENE);
		});
		loginbutton.setTranslateY(40);
		vbox.getChildren().addAll(usernamelabel, textfield, passwordlabel, passwordfield, loginbutton);
	}

}
