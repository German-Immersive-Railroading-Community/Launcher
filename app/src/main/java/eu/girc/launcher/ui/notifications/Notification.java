package eu.girc.launcher.ui.notifications;

import javafx.scene.control.Control;

/**
 * Base class of a notification
 *
 * ProgressNotification requires a Task and shows it's progress to the end user.
 */
public abstract class Notification extends Control {
    public abstract String getText();
    public abstract void setText(String text);

    protected Notification() {
        autosize();

    }
}
