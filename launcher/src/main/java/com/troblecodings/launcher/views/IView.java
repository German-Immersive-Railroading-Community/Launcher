package com.troblecodings.launcher.views;

import javafx.scene.Parent;
import org.apache.logging.log4j.LogManager;

public interface IView {
    /**
     * Retrieves the view associated with the implementing class.
     *
     * @return the root node of the UI represented as a {@link Parent}.
     */
    Parent getView();

    default void initialize() {
        LogManager.getLogger(IView.class.getName()).debug("Initializing view: {}", this.getClass().getSimpleName());
    }

    default void onShow() {
        LogManager.getLogger(IView.class.getName()).debug("Showing view: {}", this.getClass().getSimpleName());
    }

    default void onHide() {
        LogManager.getLogger(IView.class.getName()).debug("Hiding view: {}", this.getClass().getSimpleName());
    }

    default void destroy() {
        LogManager.getLogger(IView.class.getName()).debug("Destroying view: {}", this.getClass().getSimpleName());
    }
}
