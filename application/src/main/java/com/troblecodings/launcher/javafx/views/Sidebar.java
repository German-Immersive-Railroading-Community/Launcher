package com.troblecodings.launcher.javafx.views;

import com.troblecodings.launcher.javafx.ViewManager;
import com.troblecodings.launcher.services.UserService;
import javafx.scene.layout.StackPane;
import javafx.util.Builder;

public class Sidebar implements Builder<StackPane> {
    public Sidebar(ViewManager viewManager, UserService userService) {

    }

    @Override
    public StackPane build() {
        return null;
    }
}
