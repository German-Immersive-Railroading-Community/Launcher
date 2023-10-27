package eu.girc.launcher.javafx;

import eu.girc.launcher.Launcher;
import eu.girc.launcher.SceneManager;
import eu.girc.launcher.View;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class ErrorScene extends StackPane {
    public ErrorScene(String error, View sourceView) {
        SceneManager.setupView(View.ERROR, this);

        final VBox vbox = new VBox();
        vbox.setMaxWidth(750);
        vbox.setMaxHeight(500);
        vbox.setAlignment(Pos.CENTER);
        vbox.setStyle("-fx-padding: 40px 80px;");

        getChildren().add(vbox);

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
        button.setOnAction(ev -> SceneManager.switchView(sourceView));
        button.getStyleClass().add("backbutton");

        vbox.getChildren().addAll(errorHeader, errorReason, button);

        final ImageView trainImageView = new ImageView(Launcher.getImage("train1.png"));
        trainImageView.setTranslateX(720 - trainImageView.getImage().getWidth());
        trainImageView.setTranslateY(250 - trainImageView.getImage().getHeight());
        getChildren().add(trainImageView);
    }
}
