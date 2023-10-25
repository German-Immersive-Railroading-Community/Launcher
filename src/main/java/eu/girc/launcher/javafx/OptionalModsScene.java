package eu.girc.launcher.javafx;

import eu.girc.launcher.Launcher;
import eu.girc.launcher.LauncherPaths;
import eu.girc.launcher.SceneManager;
import eu.girc.launcher.View;
import eu.girc.launcher.util.FileUtil;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.awt.Desktop;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.stream.Stream;

public class OptionalModsScene extends StackPane {

    private static final ArrayList<CheckBox> optionalMods = new ArrayList<>();
    private static final Path modsPath = LauncherPaths.getModsDir();
    private static final Path optionalModsPath = LauncherPaths.getConfigDir().resolve("optional-mods");

    public OptionalModsScene() {
        SceneManager.setupView(View.MODS, this);

        try {
            Files.createDirectories(optionalModsPath);
        } catch (IOException ignored) {
        }

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
        backButton.setOnAction(ev -> SceneManager.switchView(View.OPTIONS));

        final Button refreshButton = new Button("Refresh");
        refreshButton.getStyleClass().add("optionButton");
        refreshButton.setOnAction(ev -> RefreshOptionalMods(vbox));

        buttonHBox.getChildren().addAll(backButton, refreshButton);

        wrapperBox.getChildren().addAll(optionalModsLabel, sp, buttonHBox);
        getChildren().add(wrapperBox);
    }

    private static void RefreshOptionalMods(VBox vBox) {
        try {
            if (!Files.exists(optionalModsPath)) Files.createDirectories(optionalModsPath);

            optionalMods.forEach(mod -> vBox.getChildren().remove(mod));
            optionalMods.clear();

            try (final Stream<Path> pathStream = Files.list(optionalModsPath)) {
                pathStream.forEach(filePath -> {
                    String fileName = filePath.getFileName().toString();
                    final CheckBox chkBox = new CheckBox();
                    chkBox.setSelected(FileUtil.SETTINGS.optionalMods.contains(fileName));
                    chkBox.setText(fileName.split("\\.jar$")[0]);
                    chkBox.setOnAction(ev -> {
                        if (chkBox.isIndeterminate()) return;

                        SetOptionalModState(chkBox.getText(), chkBox.isSelected());
                    });
                    optionalMods.add(chkBox);
                    vBox.getChildren().add(chkBox);
                });
            }
        } catch (IOException ioe) {
            Launcher.onError(ioe);
        }
    }

    // Only pass in the file name (e.g. OptiFine)!
    private static void SetOptionalModState(String modName, boolean state) {
        modName += ".jar";

        Path optionalModPath = optionalModsPath.resolve(modName);

        if (!Files.exists(optionalModPath)) {
            Launcher.onError(new IOException("Could not find the mod " + optionalModPath));
            return;
        }

        Path modPath = modsPath.resolve(modName);

        try {
            if (Files.exists(modPath)) {
                Files.delete(modPath);
                FileUtil.SETTINGS.optionalMods.remove(modName);
            }

            if (state) {
                Files.copy(optionalModPath, modPath);
                FileUtil.SETTINGS.optionalMods.add(modName);
            }
        } catch (IOException ioe) {
            Launcher.onError(ioe);
        }
    }
}
