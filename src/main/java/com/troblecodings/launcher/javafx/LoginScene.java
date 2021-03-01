package com.troblecodings.launcher.javafx;

import com.troblecodings.launcher.Launcher;
import com.troblecodings.launcher.util.AuthUtil;
import com.troblecodings.launcher.util.FileUtil;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
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

		Label usernamelabel = new Label("Email");
		usernamelabel.setStyle("-fx-padding: 0px 0px 10px 0px;");
		TextField textfield = new TextField();
		textfield.setPromptText("Minecraft Account Email");
		Label passwordlabel = new Label("Password");
		PasswordField passwordfield = new PasswordField();
		Button loginbutton = new Button();
		loginbutton.getStyleClass().add("loginbutton");
		
		Label errorLabel = new Label("");

		EventHandler<ActionEvent> evthl = evt -> loginCheck(textfield.getText(), passwordfield.getText(), passwordlabel);
		loginbutton.setOnAction(evthl);
		passwordfield.setOnAction(evthl);
		textfield.setOnAction(evthl);
		loginbutton.setTranslateY(40);
		vbox.getChildren().addAll(usernamelabel, textfield, passwordlabel, passwordfield, errorLabel, loginbutton);
	}

	private void loginCheck(final String mail, final String pw, final Label error) {
		if (pw.isEmpty()) {
			error.setText("Password is empty!");
			return;
		}
		if (mail.isEmpty()) {
			error.setText("Email is empty!");
			return;
		}

		if ((FileUtil.DEFAULT = AuthUtil.auth(mail, pw)) != null) {
			Launcher.setScene(Launcher.HOMESCENE);
			error.setText("");
			return;
		}
		
		error.setText("Wrong credentials!");
	}

}
