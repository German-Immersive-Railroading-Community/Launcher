package eu.girc.launcher.javafx;

import eu.girc.launcher.Launcher;
import eu.girc.launcher.SceneManager;
import eu.girc.launcher.View;
import javafx.geometry.Pos;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;

public class MicrosoftLoginScene extends StackPane {

    public MicrosoftLoginScene() {
        SceneManager.setupView(View.MSLOGIN, this);
        VBox vbox = new VBox();
        vbox.setMaxHeight(500);
        vbox.setMaxWidth(625);
        vbox.setAlignment(Pos.CENTER);
        getChildren().add(vbox);

        // Setup cookie manager
        final CookieManager manager = new CookieManager();
        manager.setCookiePolicy(CookiePolicy.ACCEPT_ORIGINAL_SERVER);
        CookieHandler.setDefault(manager);

        final ImageView trainImageView = new ImageView(Launcher.getImage("train2.png"));
        trainImageView.setTranslateX(760 - trainImageView.getImage().getWidth());
        trainImageView.setTranslateY(325 - trainImageView.getImage().getHeight());
        getChildren().add(trainImageView);
    }
}
