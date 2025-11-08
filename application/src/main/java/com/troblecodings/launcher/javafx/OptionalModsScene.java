package com.troblecodings.launcher.javafx;

import com.troblecodings.launcher.Launcher;
import com.troblecodings.launcher.util.LauncherPaths;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OptionalModsScene extends Scene {
    private static final StackPane stackPane = new StackPane();
    private static final List<CheckBox> optionalMods = new ArrayList<>();
    private static final Path modsPath = LauncherPaths.getGameDataDir().resolve("mods");
    // this was optional-mods before
    private static final Path optionalModsPath = LauncherPaths.getGameDataDir().resolve("optional_mods");

    public OptionalModsScene() {
        super(stackPane);
        Launcher.setupScene(this, stackPane);

        var wrapperBox = new VBox();
        wrapperBox.maxWidth(600.0);
        wrapperBox.maxHeight(400.0);
        wrapperBox.setAlignment(Pos.CENTER);
        wrapperBox.setStyle("-fx-padding: 20px 0px;");

        var optModsLabel = new Label("Optional Mods");

        var scrollPane = new ScrollPane();
        scrollPane.maxWidth(500.0);
        scrollPane.maxHeight(300.0);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);

        var vbox = new VBox();
        vbox.prefWidth(scrollPane.getMaxWidth());
        vbox.prefHeight(scrollPane.getMaxHeight());
        vbox.setStyle("-fx-padding: 0px;");
        scrollPane.setContent(vbox);
        refreshOptMods(vbox);

        var buttonHBox = new HBox(10.0);
        buttonHBox.setAlignment(Pos.CENTER);

        var backButton = new Button("Back");
        backButton.getStyleClass().add("optionButton");
        backButton.setOnAction(_ -> Launcher.setScene(Launcher.OPTIONSSCENE));

        var refreshButton = new Button("Refresh");
        refreshButton.getStyleClass().                add("optionButton");

        refreshButton.setOnAction(_ -> refreshOptMods(vbox));

        buttonHBox.getChildren().addAll(backButton, refreshButton);
        wrapperBox.getChildren().addAll(optModsLabel, scrollPane, buttonHBox);
        stackPane.getChildren().add(wrapperBox);
    }

    private void refreshOptMods(VBox vBox) {
        try {
            if (!Files.exists(optionalModsPath)) Files.createDirectories(optionalModsPath);

            optionalMods.forEach(mod ->
                    vBox.getChildren().remove(mod)
            );

            optionalMods.clear();

            Files.list(optionalModsPath).forEach(filePath -> {
                var fileName = filePath.toFile().getName();
                var chkBox = new CheckBox();
                chkBox.setSelected(Launcher.getInstance().getAppSettings().getOptionalMods().contains(fileName));
                chkBox.setText(Arrays.stream(fileName.split("\\.jar$")).dropWhile(String::isEmpty).toList().getFirst());
                chkBox.setOnAction(ev -> {
                    if (chkBox.isIndeterminate()) return;
                    setOptionalModState(chkBox.getText(), chkBox.isSelected());
                });
                optionalMods.add(chkBox);
                vBox.getChildren().add(chkBox);
            });
        } catch (IOException ioe) {
            Launcher.onError(ioe);
        }
    }

    // Only pass in the file name (e.g. OptiFine)!
    private void setOptionalModState(String modName, Boolean state) {
        modName += ".jar";

        var optionalModPath = optionalModsPath.resolve(modName);

        if (!Files.exists(optionalModPath)) {
            Launcher.onError(new IOException("Could not find the mod $optionalModPath"));
            return;
        }

        var modPath = modsPath.resolve(modName);

        try {
            if (Files.exists(modPath)) {
                Files.delete(modPath);
                Launcher.getInstance().getAppSettings().getOptionalMods().remove(modName);
            }

            if (state) {
                Files.copy(optionalModPath, modPath);
                Launcher.getInstance().getAppSettings().getOptionalMods().add(modName);
            }
        } catch (IOException ioe){
            Launcher.onError(ioe);
        }
    }
}