package com.troblecodings.launcher.javafx;

import java.util.ArrayList;
import java.util.function.Supplier;

import com.troblecodings.launcher.Launcher;

import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

public class Header extends StackPane {

    private static double xOffset = 0;
    private static double yOffset = 0;

    private static enum EnumPages {
        HOME(() -> Launcher.HOMESCENE), OPTIONS(() -> Launcher.OPTIONSSCENE);

        public final Supplier<Scene> supplier;

        private EnumPages(Supplier<Scene> run) {
            this.supplier = run;
        }
    }

    private static final ArrayList<Button> _buttons = new ArrayList<>();

    public Header(Scene scene) {
        HBox hbox = new HBox();
        if (!(scene instanceof LoginScene)) {
            for (EnumPages page : EnumPages.values()) {
                Button btn = new Button();
                btn.getStyleClass().add("navbar");
                btn.setText(page.name().toUpperCase());
                btn.setOnAction(evt -> {
                    if (!Launcher.getInstance().getUserService().isValidSession()) return;
                    Launcher.setScene(page.supplier.get());
                });
                hbox.getChildren().add(btn);
                HBox.setMargin(btn, new Insets(20, 20, 20, 20));
                _buttons.add(btn);
            }
        }
        hbox.setAlignment(Pos.CENTER);

        Button closebutton = new Button();
        closebutton.getStyleClass().add("closebutton");
        closebutton.setOnAction(event -> Launcher.getStage().close());
        closebutton.setTranslateX(-20);
        closebutton.setTranslateY(20);
        StackPane.setAlignment(closebutton, Pos.TOP_RIGHT);

        this.getChildren().addAll(hbox, closebutton);
        this.setMaxHeight(85);
        StackPane.setAlignment(this, Pos.TOP_CENTER);
        initializeEvents();
    }

    private void initializeEvents() {
        this.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                xOffset = event.getSceneX();
                yOffset = event.getSceneY();
            }
        });
        this.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Launcher.getStage().setX(event.getScreenX() - xOffset);
                Launcher.getStage().setY(event.getScreenY() - yOffset);
            }
        });
    }

    public static void setVisibility(boolean isVisible) {
        for (Button btn : _buttons) {
            btn.setVisible(isVisible);
        }
    }

}
