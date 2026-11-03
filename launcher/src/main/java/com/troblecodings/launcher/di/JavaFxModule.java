package com.troblecodings.launcher.di;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.troblecodings.launcher.ViewManager;
import javafx.scene.layout.StackPane;

public class JavaFxModule extends AbstractModule {
    private final StackPane contentPane;

    public JavaFxModule(StackPane contentPane) {
        this.contentPane = contentPane;
    }

    @Override
    protected void configure() {
        bind(ViewManager.class).in(Singleton.class);
    }

    @Provides
    @Singleton
    @ContentHost
    StackPane contentPane() {
        return contentPane;
    }
}
