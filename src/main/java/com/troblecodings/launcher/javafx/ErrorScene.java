package com.troblecodings.launcher.javafx;

import com.troblecodings.launcher.Launcher;
import com.troblecodings.launcher.assets.Assets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class ErrorScene extends Scene {
    private static StackPane _stackPane = new StackPane();

    public ErrorScene(String error, Scene sourceScene) {
        super(_stackPane);

        Launcher.setupScene(this, _stackPane);

        final VBox vbox = new VBox();
        vbox.setMaxWidth(750);
        vbox.setMaxHeight(500);
        vbox.setAlignment(Pos.CENTER);
        vbox.setStyle("-fx-padding: 40px 80px;");

        _stackPane.getChildren().add(vbox);

        final Label errorHeader = new Label();
        errorHeader.setText("Error");
        errorHeader.setStyle("-fx-text-fill: red; -fx-padding: 0px 0px 30px 0px; -fx-font-size: 24px;");
        //errorHeader.setAlignment(Pos.CENTER);

        final TextArea errorReason = new TextArea(error);
        errorReason.setWrapText(true);
        errorReason.setEditable(false);
        errorReason.setPrefHeight(300);
        errorReason.setStyle("-fx-font-size: 13px; -fx-text-inner-color: white; -fx-padding: 0px 0px 30px 0px;");

        final Button button = new Button();
        button.setOnAction(ev -> {
            _stackPane = new StackPane();
            Launcher.setScene(sourceScene);
        });
        button.getStyleClass().add("loginbutton");

        vbox.getChildren().addAll(errorHeader, errorReason, button);

        final ImageView trainImageView = new ImageView(Assets.getImage("train1.png"));
        trainImageView.setTranslateX(720 - trainImageView.getImage().getWidth());
        trainImageView.setTranslateY(250 - trainImageView.getImage().getHeight());
        _stackPane.getChildren().add(trainImageView);
    }
}
