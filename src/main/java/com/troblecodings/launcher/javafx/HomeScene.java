package com.troblecodings.launcher.javafx;

import com.troblecodings.launcher.Launcher;
import com.troblecodings.launcher.assets.Assets;
import com.troblecodings.launcher.util.StartupUtil;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

public class HomeScene extends Scene {
    private static final StackPane stackpane = new StackPane();

    public HomeScene() {
        super(stackpane);
        Launcher.setupScene(this, stackpane);

        ImageView imagelogo = new ImageView(Assets.getImage("logo.png"));

        Button launchbutton = new Button();
        launchbutton.getStyleClass().add("launchbutton");
        launchbutton.disabledProperty().addListener((obs, old, ne) -> {
            ColorAdjust l = new ColorAdjust(1, -1, 0, 0);
            launchbutton.setEffect(l);
            if (!launchbutton.isDisabled()) {
                launchbutton.setEffect(null);
            }
        });
        launchbutton.setOnAction(event -> {
            launchbutton.setDisable(true);
            new Thread(() -> {
                Process process;
                if ((process = StartupUtil.start()) == null) {
                    // TODO
                    launchbutton.setDisable(false);
                    return;
                }
                try {
                    process.waitFor();
                } catch (InterruptedException e) {
                    Launcher.onError(e);
                }
                launchbutton.setDisable(false);
            }).start();
        });
        launchbutton.setTranslateY(270);

        stackpane.getChildren().addAll(imagelogo, launchbutton);
    }

}
