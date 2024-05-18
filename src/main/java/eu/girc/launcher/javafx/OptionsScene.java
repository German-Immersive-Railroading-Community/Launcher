package eu.girc.launcher.javafx;

import eu.girc.launcher.SceneManager;
import eu.girc.launcher.View;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class OptionsScene extends StackPane {

    public OptionsScene() {
        SceneManager.setupView(View.OPTIONS, this);
        final ScrollPane sp = new ScrollPane();
        sp.setHbarPolicy(ScrollBarPolicy.NEVER);
        sp.setVbarPolicy(ScrollBarPolicy.ALWAYS);
        getChildren().add(sp);

        final VBox vbox = new VBox();
        sp.setMaxHeight(400);
        sp.setMaxWidth(650);
        sp.setContent(vbox);
        vbox.setPrefSize(sp.getMaxWidth(), sp.getMaxHeight());
    }
}
