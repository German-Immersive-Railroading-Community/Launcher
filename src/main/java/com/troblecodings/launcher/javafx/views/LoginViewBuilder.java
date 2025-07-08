package com.troblecodings.launcher.javafx.views;

import com.troblecodings.launcher.javafx.controllers.LoginController;
import com.troblecodings.launcher.javafx.models.LoginModel;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.Builder;

public class LoginViewBuilder implements Builder<Region> {

    private final LoginModel model;
    private final LoginController loginController;

    public LoginViewBuilder(LoginModel model, LoginController loginController) {
        this.model = model;
        this.loginController = loginController;
    }

    @Override
    public Region build() {
        final BorderPane mainPane = new BorderPane();
        mainPane.setTop(createTop());
        return mainPane;
    }

    private Node createTop() {
        final VBox topVBox = new VBox();
        final Label loginLabel = new Label("Login with Microsoft");
        loginLabel.setStyle("-fx-font-size: 13");
        topVBox.getChildren().add(loginLabel);
        return topVBox;
    }
}
