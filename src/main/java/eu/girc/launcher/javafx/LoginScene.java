package eu.girc.launcher.javafx;

import eu.girc.launcher.Launcher;
import eu.girc.launcher.SceneManager;
import eu.girc.launcher.View;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class LoginScene extends StackPane {

    public LoginScene() {
        VBox vbox = new VBox();
        vbox.setMaxHeight(400);
        vbox.setMaxWidth(625);
        vbox.setAlignment(Pos.CENTER);
        getChildren().add(vbox);

        final Button microsoftLoginButton = new Button();
        microsoftLoginButton.getStyleClass().add("microsoftLoginButton");
        VBox.setMargin(microsoftLoginButton, new Insets(20, 0, 20, 0));
        microsoftLoginButton.setOnAction(event -> SceneManager.switchView(View.MSLOGIN));

        vbox.getChildren().add(microsoftLoginButton);

        final ImageView trainImageView = new ImageView(Launcher.getImage("train2.png"));
        trainImageView.setTranslateX(760 - trainImageView.getImage().getWidth());
        trainImageView.setTranslateY(325 - trainImageView.getImage().getHeight());
        getChildren().add(trainImageView);
    }
}
