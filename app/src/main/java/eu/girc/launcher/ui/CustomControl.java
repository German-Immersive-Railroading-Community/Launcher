package eu.girc.launcher.ui;

import javafx.scene.control.Control;
import org.slf4j.LoggerFactory;

public abstract class CustomControl extends Control {
    public CustomControl() {
        LoggerFactory.getLogger(getClass()).debug("CustomControl created");
    }
}
