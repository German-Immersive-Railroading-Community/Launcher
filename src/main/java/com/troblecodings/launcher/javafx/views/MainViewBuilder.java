package com.troblecodings.launcher.javafx.views;

import com.troblecodings.launcher.javafx.Footer;
import com.troblecodings.launcher.javafx.controllers.MainController;
import com.troblecodings.launcher.javafx.models.MainModel;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.util.Builder;

public class MainViewBuilder implements Builder<Region> {
    private final MainModel model;
    private final MainController controller;

    private HeaderViewBuilder header;
    private Footer footer;

    public MainViewBuilder(MainModel model, MainController controller) {
        this.model = model;
        this.controller = controller;
    }

    @Override
    public Region build() {
        final BorderPane mainPane = new BorderPane();

        mainPane.setTop(new HeaderViewBuilder().build());

        return mainPane;
    }
}
