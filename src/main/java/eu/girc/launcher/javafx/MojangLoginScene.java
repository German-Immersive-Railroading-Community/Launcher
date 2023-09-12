package eu.girc.launcher.javafx;

import eu.girc.launcher.Launcher;
import eu.girc.launcher.assets.Assets;
import eu.girc.launcher.util.AuthUtil;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import net.hycrafthd.minecraft_authenticator.login.AuthenticationException;

public class MojangLoginScene extends Scene {

	private static StackPane stackpane = new StackPane();

	public MojangLoginScene() {
		super(stackpane);
		Launcher.setupScene(this, stackpane);

		VBox vbox = new VBox();
		vbox.setMaxHeight(400);
		vbox.setMaxWidth(625);
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
		errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 20px");

		EventHandler<ActionEvent> evthl = evt -> loginCheck(textfield.getText(), passwordfield.getText(), errorLabel);
		loginbutton.setOnAction(evthl);
		passwordfield.setOnAction(evthl);
		textfield.setOnAction(evthl);
		loginbutton.setTranslateY(40);
		vbox.getChildren().addAll(usernamelabel, textfield, passwordlabel, passwordfield, errorLabel, loginbutton);

		final ImageView trainImageView = new ImageView(Assets.getImage("train2.png"));
		trainImageView.setTranslateX(760 - trainImageView.getImage().getWidth());
		trainImageView.setTranslateY(325 - trainImageView.getImage().getHeight());
		stackpane.getChildren().add(trainImageView);
	}

	private void loginCheck(final String mail, final String pw, final Label error) {
		if (mail.isEmpty()) {
			error.setText("Email is empty!");
			return;
		}
		if (pw.isEmpty()) {
			error.setText("Password is empty!");
			return;
		}

		new Thread(() -> {
			try {
				AuthUtil.mojangLogin(mail, pw);
				Platform.runLater(() -> {
					Header.setVisibility(true);
					Launcher.setScene(Launcher.HOMESCENE);
					error.setText("");
				});
			} catch (AuthenticationException ex) {
				Platform.runLater(() -> error.setText("Wrong credentials!"));
				Launcher.onError(ex);
			} catch (Exception ex) {
				Platform.runLater(() -> error.setText("Unexpected Error, try later!"));
				Launcher.onError(ex);
			}
		}).start();
	}

}
