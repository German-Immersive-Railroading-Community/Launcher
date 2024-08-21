package eu.girc.launcher.utils;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;

public final class NodeUtils {
    public static void setAnchors(Node node, Insets insets) {
        if (insets.getTop() >= 0) {
            AnchorPane.setTopAnchor(node, insets.getTop());
        }
        if (insets.getRight() >= 0) {
            AnchorPane.setRightAnchor(node, insets.getRight());
        }
        if (insets.getBottom() >= 0) {
            AnchorPane.setBottomAnchor(node, insets.getBottom());
        }
        if (insets.getLeft() >= 0) {
            AnchorPane.setLeftAnchor(node, insets.getLeft());
        }
    }
}
