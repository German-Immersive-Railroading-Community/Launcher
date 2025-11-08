package com.troblecodings.launcher.javafx.views;

import com.troblecodings.launcher.javafx.ViewManager;
import com.troblecodings.launcher.services.UserService;
import javafx.scene.Parent;
import javafx.util.Builder;

public class LoginView implements Builder<Parent> {
    private final ViewManager viewManager;
    private final UserService userService;

    public LoginView(ViewManager viewManager, UserService userService) {
        this.viewManager = viewManager;
        this.userService = userService;
    }

    @Override
    public Parent build() {
        return;
    }
}
