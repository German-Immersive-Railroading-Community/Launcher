package com.troblecodings.launcher.javafx.controllers;

import com.troblecodings.launcher.javafx.models.MainModel;
import com.troblecodings.launcher.javafx.views.MainViewBuilder;
import javafx.scene.layout.Region;

public class MainController {
    private final MainModel mainModel;
    private final MainViewBuilder mainViewBuilder;

    public MainController() {
        mainModel = new MainModel();
        mainViewBuilder = new MainViewBuilder(mainModel, this);
    }

    public Region getView() {
        return mainViewBuilder.build();
    }
}
