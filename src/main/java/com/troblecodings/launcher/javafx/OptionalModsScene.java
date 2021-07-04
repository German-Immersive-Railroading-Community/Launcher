package com.troblecodings.launcher.javafx;

import com.troblecodings.launcher.Launcher;
import com.troblecodings.launcher.util.FileUtil;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class OptionalModsScene extends Scene {

    private static final StackPane stackPane = new StackPane();
    private static final ArrayList<CheckBox> optionalMods = new ArrayList<>();
    private static final Path modsPath = Paths.get(FileUtil.SETTINGS.baseDir, "mods");
    private static final Path optionalModsPath = Paths.get(FileUtil.SETTINGS.baseDir, "optional-mods");

    public OptionalModsScene() {
        super(stackPane);

        Launcher.setupScene(this, stackPane);

        final VBox wrapperBox = new VBox();
        wrapperBox.setMaxHeight(400);
        wrapperBox.setMaxWidth(600);
        wrapperBox.setAlignment(Pos.CENTER);
        wrapperBox.setStyle("-fx-padding: 20px 0px;");

        final Label optionalModsLabel = new Label("Optional Mods");

        final ScrollPane sp = new ScrollPane();
        sp.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        sp.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);

        final VBox vbox = new VBox();
        sp.setMaxHeight(300);
        sp.setMaxWidth(500);
        sp.setContent(vbox);
        vbox.setPrefSize(sp.getMaxWidth(), sp.getMaxHeight());
        vbox.setStyle("-fx-padding: 0px;");

        RefreshOptionalMods(vbox);

        final HBox buttonHBox = new HBox(10);
        buttonHBox.setAlignment(Pos.CENTER);

        final Button backButton = new Button("Back");
        backButton.getStyleClass().add("optionButton");
        backButton.setOnAction(ev -> Launcher.setScene(Launcher.OPTIONSSCENE));

        final Button refreshButton = new Button("Refresh");
        refreshButton.getStyleClass().add("optionButton");
        refreshButton.setOnAction(ev -> RefreshOptionalMods(vbox));

        buttonHBox.getChildren().addAll(backButton, refreshButton);

        wrapperBox.getChildren().addAll(optionalModsLabel, sp, buttonHBox);

        stackPane.getChildren().add(wrapperBox);
    }

    private static void RefreshOptionalMods(VBox vBox) {
        try {
            optionalMods.forEach(mod -> {
                vBox.getChildren().remove(mod);
            });

            optionalMods.clear();

            Files.list(optionalModsPath).forEach(filePath -> {
                String fileName = filePath.toFile().getName();
                final CheckBox chkBox = new CheckBox();
                chkBox.setSelected(FileUtil.SETTINGS.optionalMods.contains(fileName));
                chkBox.setText(fileName.split("\\.")[0]);
                chkBox.setOnAction(ev -> {
                    if(chkBox.isIndeterminate())
                        return;

                    SetOptionalModState(chkBox.getText(), chkBox.isSelected());
                });
                optionalMods.add(chkBox);
                vBox.getChildren().add(chkBox);
            });
        } catch(IOException ioe) {
            Launcher.onError(ioe);
        }
    }

    // Only pass in the file name (e.g. OptiFine)!
    private static void SetOptionalModState(String modName, boolean state) {
        modName += ".jar";

        Path optionalModPath = optionalModsPath.resolve(modName);

        if(!Files.exists(optionalModPath))
        {
            Launcher.onError(new IOException("Could not find the mod " + optionalModPath));
            return;
        }

        Path modPath = modsPath.resolve(modName);

        try {
            if(Files.exists(modPath)) {
                Files.delete(modPath);
                FileUtil.SETTINGS.optionalMods.remove(modName);
            }

            if(state) {
                Files.copy(optionalModPath, modPath);
                FileUtil.SETTINGS.optionalMods.add(modName);
            }
        } catch(IOException ioe) {
            Launcher.onError(ioe);
        }
    }
}
