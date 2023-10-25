package eu.girc.launcher.javafx;

import eu.girc.launcher.SceneManager;
import eu.girc.launcher.View;
import eu.girc.launcher.util.AuthUtil;
import javafx.application.Platform;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.StackPane;

public class Footer extends StackPane {

    private static final SimpleDoubleProperty bar = new SimpleDoubleProperty();

    public Footer(View view) {
        ProgressBar progressbar = new ProgressBar();
        progressbar.setTranslateY(-10);
        progressbar.setPrefWidth(1280);
        progressbar.setProgress(0.001);
        bar.addListener((x, x2, x3) -> progressbar.setProgress(bar.get()));

        Button button = new Button("Lizensen und Kredits");
        button.setOnAction(event -> SceneManager.switchView(getCorrectView(view)));

        this.getChildren().addAll(button, progressbar);

        this.setMaxHeight(85);
        StackPane.setAlignment(button, Pos.BOTTOM_CENTER);
        StackPane.setAlignment(progressbar, Pos.TOP_LEFT);
        StackPane.setAlignment(this, Pos.BOTTOM_LEFT);
    }

    // Returns the correct scene based on the authentication status. Not authenticated -> LoginScene; Authenticated -> HomeScene.
    // TODO: Completely refactor this.
    private static View getCorrectView(View currentView) {
        if (currentView == View.CREDITS) {
            return AuthUtil.login() ? View.HOME : View.LOGIN;
        }

        return View.CREDITS;
    }

    public static void setProgress(double progress) {
        Platform.runLater(() -> bar.set(progress));
    }
}
