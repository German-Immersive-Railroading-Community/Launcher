package com.troblecodings.launcher.javafx;

import com.troblecodings.launcher.Launcher;

import javafx.application.Platform;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.StackPane;

public class Footer extends StackPane {

    private static final SimpleDoubleProperty bar = new SimpleDoubleProperty();

    public Footer(Scene sc) {
        ProgressBar progressbar = new ProgressBar();
        progressbar.setTranslateY(-10);
        progressbar.setPrefWidth(1280);
        progressbar.setProgress(0.001);
        bar.addListener((x, x2, x3) -> progressbar.setProgress(bar.get()));

        Button button = new Button("Lizensen und Kredits");
        button.setOnAction(event -> Launcher.setScene(getCorrectScene(sc)));

        this.getChildren().addAll(button, progressbar);

        this.setMaxHeight(85);
        StackPane.setAlignment(button, Pos.BOTTOM_CENTER);
        StackPane.setAlignment(progressbar, Pos.TOP_LEFT);
        StackPane.setAlignment(this, Pos.BOTTOM_LEFT);
    }

    public static void setProgress(double progress) {
        Platform.runLater(() -> bar.set(progress));
    }

    // Returns the correct scene based on the authentication status. Not authenticated -> LoginScene; Authenticated -> HomeScene.
    private static Scene getCorrectScene(Scene currentScene) {
        if (currentScene instanceof CreditsScene) {
            return Launcher.getInstance().getUserService().isLoggedIn() ? Launcher.HOMESCENE : Launcher.LOGINSCENE;
        }

        return Launcher.CREDITSSCENE;
    }

}
