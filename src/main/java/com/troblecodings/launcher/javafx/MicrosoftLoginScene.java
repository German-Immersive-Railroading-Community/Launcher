package com.troblecodings.launcher.javafx;

import com.troblecodings.launcher.Launcher;
import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class MicrosoftLoginScene extends Scene {
    private static final StackPane stackPane = new StackPane();
    private final Thread loginThread;

    public MicrosoftLoginScene() {
        super(stackPane);
        Launcher.setupScene(this, stackPane);

        final Label deviceCodeLabel = new Label();
        final Button verificationUriButton = new Button("Click me");
        verificationUriButton.setVisible(false);
        final Label verificationUriLabel = new Label();

        final VBox vbox = new VBox();
        vbox.setMaxHeight(400);
        vbox.setMaxWidth(625);
        vbox.setAlignment(Pos.CENTER);
        vbox.getChildren().addAll(deviceCodeLabel, verificationUriLabel, verificationUriButton);
        stackPane.getChildren().add(vbox);

        final Task<Void> loginTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                // Callback is running on the JavaFX thread
                Launcher.getInstance().getUserService().login(deviceCode -> {
                    deviceCodeLabel.setText(deviceCode.getUserCode());
                    verificationUriButton.setOnMouseClicked(ev -> Launcher.getInstance().getHostServices().showDocument(deviceCode.getVerificationUri()));
                    verificationUriButton.setVisible(true);
                    verificationUriLabel.setText(String.format("Click the button below or manually open the site %s to enter your device code.", deviceCode.getVerificationUri()));
                });

                return null;
            }
        };

        loginTask.setOnSucceeded(ev -> Launcher.setScene(Launcher.HOMESCENE));
        loginThread = new Thread(loginTask);
    }

    public void run() {
        loginThread.start();
    }
}
