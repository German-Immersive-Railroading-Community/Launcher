package com.troblecodings.launcher.javafx;

import com.troblecodings.launcher.Launcher;
import com.troblecodings.launcher.assets.Assets;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Instant;
import java.util.Date;
import java.util.concurrent.CompletableFuture;

public class MicrosoftLoginScene extends Scene {
    private static final Logger log = LogManager.getLogger(MicrosoftLoginScene.class);
    private static final StackPane stackpane = new StackPane();

    private final TextField userCodeValueText = new TextField("Loading");
    private final Hyperlink verificationLink = new Hyperlink("Loading");
    private final Hyperlink directVerificationLink = new Hyperlink("Loading");

    public MicrosoftLoginScene() {
        super(stackpane);
        Launcher.setupScene(this, stackpane);
        this.getStylesheets().add(Assets.getStyleSheet("microsoftlogin.css"));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        VBox vbox = new VBox();
        vbox.setMaxHeight(500);
        vbox.setMaxWidth(625);
        vbox.setAlignment(Pos.CENTER);
        vbox.setOpaqueInsets(new Insets(0.0, 32.0, 0.0, 32.0));
        stackpane.getChildren().add(vbox);

        Label headerLabel = new Label("Login with Microsoft");
        headerLabel.setStyle("-fx-font-size: 24pt; -fx-padding: 0px 0px 60px 0px;");
        vbox.getChildren().add(headerLabel);

        Label userCodeLabel = new Label("User Code:");
        userCodeValueText.setStyle("-fx-background-color: transparent; -fx-background-insets: 0; -fx-background-radius: 0; -fx-padding: 0; -fx-cursor: text;");
        userCodeValueText.setStyle(":focused { -fx-background-color: transparent; -fx-cache: fraction; }");
        resizeFieldToText(userCodeValueText);
        HBox userCodeFlow = new HBox(userCodeLabel, spacer, userCodeValueText);
        userCodeFlow.setAlignment(Pos.BASELINE_LEFT);

        Label normalLink = new Label("Click on this link to enter the code:");
        verificationLink.setDisable(true);
        verificationLink.setOnAction(e -> Launcher.getInstance().getHostServices().showDocument(verificationLink.getText()));

        Label directLink = new Label("Or use direct link:");
        directVerificationLink.setDisable(true);
        directVerificationLink.setOnAction(e -> Launcher.getInstance().getHostServices().showDocument(directVerificationLink.getText()));

        vbox.getChildren().addAll(new HBox(userCodeFlow), new HBox(normalLink), new HBox(verificationLink), new HBox(directLink), new HBox(directVerificationLink));

        final ImageView trainImageView = new ImageView(Assets.getImage("train2.png"));
        trainImageView.setTranslateX(760 - trainImageView.getImage().getWidth());
        trainImageView.setTranslateY(325 - trainImageView.getImage().getHeight());
        stackpane.getChildren().add(trainImageView);
    }

    public void startFlow() {
        CompletableFuture.supplyAsync(() -> Launcher.getInstance().getUserService().login(msa -> {
            log.debug("Device code expires: {}", Date.from(Instant.ofEpochMilli(msa.getExpireTimeMs())));

            Platform.runLater(() -> {
                userCodeValueText.setText(msa.getUserCode());
                resizeFieldToText(userCodeValueText);
                verificationLink.setText(msa.getVerificationUri());
                directVerificationLink.setText(msa.getDirectVerificationUri());

                verificationLink.setDisable(false);
                directVerificationLink.setDisable(false);
            });
        })).thenAccept(result -> {
            if (result) {
                log.info("Since we are now logged in, switch to home scene.");
                Platform.runLater(() -> {
                    Header.setVisibility(true);
                    Launcher.setScene(Launcher.HOMESCENE);
                });

                return;
            }

            log.warn("Login returned false, switching back to login scene.");
            Platform.runLater(() -> Launcher.setScene(Launcher.LOGINSCENE));
        });
    }

    // Helper to calculate exact width (from previous discussion)
    private void resizeFieldToText(TextField tf) {
        Text textNode = new Text(tf.getText());
        textNode.setFont(tf.getFont()); // Match font
        double textWidth = textNode.getLayoutBounds().getWidth();
        tf.setPrefWidth(textWidth + 10);
    }
}
