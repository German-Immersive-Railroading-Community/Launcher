package com.troblecodings.launcher.javafx.controllers;

import com.troblecodings.launcher.javafx.models.LoginModel;
import com.troblecodings.launcher.javafx.views.LoginViewBuilder;
import javafx.scene.layout.Region;

public class LoginController {
    private final LoginModel model;
    private final LoginViewBuilder viewBuilder;

    public LoginController() {
        model = new LoginModel();
        viewBuilder = new LoginViewBuilder(model, this);
    }

    public Region getView() {
        return viewBuilder.build();
    }
}
