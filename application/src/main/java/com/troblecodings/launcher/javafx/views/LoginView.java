package com.troblecodings.launcher.javafx.views;

import com.troblecodings.launcher.javafx.ViewManager;
import com.troblecodings.launcher.services.UserService;
import javafx.scene.Parent;
import javafx.util.Builder;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class LoginView implements Builder<Parent> {
    private final ViewManager viewManager;
    private final UserService userService;

    @Override
    public Parent build() {
        return null;
    }
}
