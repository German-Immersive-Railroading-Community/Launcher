package eu.girc.launcher.javafx;

import eu.girc.launcher.AuthManager;
import eu.girc.launcher.Launcher;
import eu.girc.launcher.SceneManager;
import eu.girc.launcher.View;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.geometry.Pos;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebHistory;
import javafx.scene.web.WebView;
import net.hycrafthd.minecraft_authenticator.login.AuthenticationException;
import net.hycrafthd.minecraft_authenticator.login.Authenticator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;

public class MicrosoftLoginScene extends StackPane {
    private static final Logger logger = LogManager.getLogger();

    private static WebEngine engine;

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

        final WebView webView = new WebView();
        webView.setMinWidth(600);
        engine = webView.getEngine();

        engine.setJavaScriptEnabled(true);
        engine.setOnError(err -> logger.error("WebEngine encountered error: {}", err.getMessage(), err.getException()));
        engine.load(Authenticator.microsoftLogin().toString());

        webView.getEngine().getHistory().getEntries().addListener(this::loginCheck);

        vbox.getChildren().addAll(webView);

        final ImageView trainImageView = new ImageView(Launcher.getImage("train2.png"));
        trainImageView.setTranslateX(760 - trainImageView.getImage().getWidth());
        trainImageView.setTranslateY(325 - trainImageView.getImage().getHeight());
        getChildren().add(trainImageView);
    }

    private void loginCheck(ListChangeListener.Change<? extends WebHistory.Entry> event) {
        if (event.next() && event.wasAdded()) {
            for (WebHistory.Entry entry : event.getAddedSubList()) {
                if (entry.getUrl().startsWith(Authenticator.microsoftLoginRedirect())) {
                    final String authCode = entry.getUrl().substring(entry.getUrl().indexOf("=") + 1, entry.getUrl().indexOf("&"));

                    try {
                        AuthManager.login(authCode);
                        Platform.runLater(() -> SceneManager.switchView(View.HOME));
                    } catch (final IOException | AuthenticationException ex) {
                        //						 Platform.runLater(() -> error.setText("Wrong credentials!"));
                        engine.reload();
                        Launcher.onError(ex);
                    }
                }
            }
        }
    }
}
