package com.troblecodings.launcher.javafx.views;

import com.troblecodings.launcher.javafx.ViewManager;
import com.troblecodings.launcher.services.UserService;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;
import javafx.util.Builder;

public class MainView implements Builder<Parent> {
    private final ViewManager viewManager;
    private final UserService userService;

    private final Sidebar sidebar;
    private final LoginView loginView;

    private final BorderPane mainPane;

    public MainView(ViewManager viewManager, UserService userService) {
        this.viewManager = viewManager;
        this.userService = userService;

        sidebar = new Sidebar(viewManager, userService);
        loginView = new LoginView(viewManager, userService);

        mainPane = new BorderPane();
        mainPane.setStyle("-fx-background-color: hsb(30.0, 8.7%, 9.02%)");
    }

    @Override
    public Parent build() {
        viewManager.currentViewProperty().addListener((_,_,newView) -> {
            mainPane.setCenter(viewManager.getViewNode(newView));
        });

        mainPane.setLeft(sidebar.build());
        mainPane.setCenter(new HomeView().build());

        return mainPane;
    }
}
