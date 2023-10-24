package eu.girc.launcher.javafx;

import eu.girc.launcher.Launcher;
import eu.girc.launcher.SceneManager;
import eu.girc.launcher.View;
import eu.girc.launcher.util.AuthUtil;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

import java.util.ArrayList;
import java.util.function.Supplier;

public class Header extends StackPane {

    private static final ArrayList<Button> _buttons = new ArrayList<>();
    private static double xOffset = 0;
    private static double yOffset = 0;

    public Header(View view) {
        HBox hbox = new HBox();
        if (view != View.LOGIN) {
            for (EnumPages page : EnumPages.values()) {
                Button btn = new Button();
                btn.getStyleClass().add("navbar");
                btn.setText(page.name().toUpperCase());
                btn.setOnAction(evt -> {
                    if (!AuthUtil.login()) return;
                    SceneManager.switchView(page.supplier.get());
                });
                hbox.getChildren().add(btn);
                HBox.setMargin(btn, new Insets(20, 20, 20, 20));
                _buttons.add(btn);
            }
        }
        hbox.setAlignment(Pos.CENTER);

        Button closebutton = new Button();
        closebutton.getStyleClass().add("closebutton");
        closebutton.setOnAction(event -> System.exit(0));
        closebutton.setTranslateX(-20);
        closebutton.setTranslateY(20);
        StackPane.setAlignment(closebutton, Pos.TOP_RIGHT);

        this.getChildren().addAll(hbox, closebutton);
        this.setMaxHeight(85);
        StackPane.setAlignment(this, Pos.TOP_CENTER);
        initializeEvents();
    }

    public static void setVisibility(boolean isVisible) {
        for (Button btn : _buttons) {
            btn.setVisible(isVisible);
        }
    }

    private void initializeEvents() {
        this.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });
        this.setOnMouseDragged(event -> {
            Launcher.getStage().setX(event.getScreenX() - xOffset);
            Launcher.getStage().setY(event.getScreenY() - yOffset);
        });
    }

    private enum EnumPages {
        HOME(() -> View.HOME), OPTIONS(() -> View.OPTIONS);

        public final Supplier<View> supplier;

        EnumPages(Supplier<View> run) {
            this.supplier = run;
        }
    }
}
