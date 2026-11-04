package com.troblecodings.launcher;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.troblecodings.launcher.di.ContentPane;
import com.troblecodings.launcher.views.IView;
import com.troblecodings.launcher.views.ViewId;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

public final class ViewManager {
    private static final Logger LOGGER = LogManager.getLogger(ViewManager.class.getName());

    private final StackPane contentPane;
    private final Map<ViewId, Provider<IView>> providers;
    private final Map<ViewId, IView> cachedViews = new EnumMap<>(ViewId.class);

    private ViewId currentViewId;
    private IView currentView;

    @Inject
    public ViewManager(@ContentPane StackPane contentPane, Map<ViewId, Provider<IView>> providers) {
        this.contentPane = Objects.requireNonNull(contentPane);
        this.providers = providers;
    }

    public void show(ViewId viewId) {
        if (currentViewId == viewId) {
            LOGGER.debug("View {} is already shown", viewId);
            return;
        }

        if (currentView != null) {
            currentView.onHide();
        }

        IView nextView = cachedViews.computeIfAbsent(viewId, _ -> {
            LOGGER.debug("Creating and caching view {}", viewId);
            var provider = providers.get(viewId);
            if(provider == null) {
                LOGGER.error("No provider found for view {}", viewId);
                throw new IllegalArgumentException("No provider found for view " + viewId);
            }

            var view = provider.get();
            view.initialize();
            return view;
        });

        currentViewId = viewId;
        currentView = nextView;

        Node nextRoot = nextView.getView();
        contentPane.getChildren().setAll(nextRoot);
        nextView.onShow();
        LOGGER.debug("View {} shown", viewId);
    }

    public ViewId getCurrentView() {
        return currentViewId;
    }

    public void clearCache(ViewId viewId) {
        if (viewId == currentViewId) {
            throw new IllegalStateException("Cannot clear cache for current view");
        }

        IView removed = cachedViews.remove(viewId);

        if (removed != null) {
            removed.onHide();
            removed.destroy();
        }
    }

    public void destroy() {
        if (currentView != null) {
            currentView.onHide();
        }

        cachedViews.values().forEach(IView::destroy);
        cachedViews.clear();
        currentViewId = null;
        currentView = null;
        contentPane.getChildren().clear();
    }
}
