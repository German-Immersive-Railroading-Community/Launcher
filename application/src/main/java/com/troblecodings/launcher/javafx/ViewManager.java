package com.troblecodings.launcher.javafx;

import com.troblecodings.launcher.LauncherView;
import com.troblecodings.launcher.javafx.views.HomeView;
import com.troblecodings.launcher.javafx.views.SettingsView;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.control.Label;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

public class ViewManager {
    private final ObjectProperty<LauncherView> currentView = new SimpleObjectProperty<>(LauncherView.NONE);
    private final HashMap<LauncherView, Node> viewCache = new HashMap<>();
    private final Logger logger = LoggerFactory.getLogger(ViewManager.class);

    public LauncherView getCurrentView() {
        return currentView.get();
    }

    public void setCurrentView(LauncherView view) {
        if (view == currentView.get()) {
            logger.debug("Already on view {}", view);
            return;
        }

        logger.debug("Switching views: {} -> {}", currentView.get(), view);
        currentView.set(view);
    }

    public ReadOnlyObjectProperty<LauncherView> currentViewProperty() {
        return currentView;
    }

    public Node getViewNode( LauncherView view) {
        if(viewCache.containsKey(view)) {
            return viewCache.get(view);
        }

        viewCache.put(view, createView(view));
        return viewCache.get(view);
    }

    private Node createView( LauncherView view) {
        return switch (view) {
            case LauncherView.HOME -> new HomeView().build();
            case LauncherView.SETTINGS -> new SettingsView().build();
            default -> {
                var label = new Label("Scene not found!");
                label.setStyle("-fx-text-fill: crimson");
                yield label;
            }
        };
    }
}
