package com.troblecodings.launcher.javafx;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;

import com.troblecodings.launcher.Launcher;
import com.troblecodings.launcher.assets.Assets;
import com.troblecodings.launcher.util.FileUtil;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.util.StringConverter;

public class OptionsScene extends Scene {

    private static final StackPane stackpane = new StackPane();

    public OptionsScene() {
        super(stackpane);
        Launcher.setupScene(this, stackpane);

        final ScrollPane sp = new ScrollPane();
        sp.setHbarPolicy(ScrollBarPolicy.NEVER);
        sp.setVbarPolicy(ScrollBarPolicy.ALWAYS);
        stackpane.getChildren().add(sp);

        final VBox vbox = new VBox();
        sp.setMaxHeight(400);
        sp.setMaxWidth(650);
        sp.setContent(vbox);
        vbox.setPrefSize(sp.getMaxWidth(), sp.getMaxHeight());

        // Ram

        final Label ramlabel = new Label("RAM");
        ramlabel.setStyle("-fx-padding: 0px 0px 0px 0px;");
        vbox.getChildren().add(ramlabel);

        final Slider ramSlider = new Slider(4*1024, 24*1024, FileUtil.SETTINGS.ram);
        ramSlider.setShowTickLabels(true);
        ramSlider.setShowTickMarks(true);
        ramSlider.setMajorTickUnit(4*1024);
        ramSlider.setMinorTickCount(3);
        ramSlider.setBlockIncrement(1024);
        ramSlider.setSnapToTicks(true);
        ramSlider.setLabelFormatter(new StringConverter<Double>() {
            @Override
            public String toString(Double n) {
                return String.format("%.0f MB", n);
            }

            @Override
            public Double fromString(String s) {
                return 0.0;
            }
        });

        Label infoLabel = new Label("Selected Memory: " + ((int) ramSlider.getValue()) + " MB");
        infoLabel.setStyle("-fx-font-size: 12pt; -fx-font-weight: normal;");

        ramSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            int ramValue = newVal.intValue();
            FileUtil.SETTINGS.ram = ramValue;
            infoLabel.setText("Selected Memory: " + ramValue + " MB");
        });

        vbox.getChildren().add(infoLabel);
        vbox.getChildren().add(ramSlider);

        // Auflösung

        final Label resolution = new Label("Resolution");
        resolution.setStyle("-fx-padding: 20px 0px 10px 0px;");

        final ComboBox<String> resolutioncombobox = new ComboBox<>();
        resolutioncombobox.setEditable(true);
        resolutioncombobox.getEditor().setText(FileUtil.SETTINGS.width + "x" + FileUtil.SETTINGS.height);
        ObservableList<String> list = resolutioncombobox.getItems();
        final Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        for (int i = 2; i < 7; i++) {
            list.add(dimension.width * 2 / i + "x" + dimension.height * 2 / i);
        }
        resolutioncombobox.setOnAction(evt -> {
            String text = resolutioncombobox.getEditor().getText();
            try {
                if (text.contains("x")) {
                    String[] arr = text.split("x");
                    FileUtil.SETTINGS.width = Integer.parseInt(arr[0]);
                    FileUtil.SETTINGS.height = Integer.parseInt(arr[1]);
                }
            } catch (Exception ignored) {
            }
        });

        // javaversion selection

        final Label javaversion = new Label("Javaversion");
        javaversion.setStyle("-fx-padding: 20px 0px 10px 0px;");

        final TextField javaversionfield = new TextField(FileUtil.SETTINGS.javaPath);
        javaversionfield.setEditable(false);

        final Button javaversionbutton = new Button("Open Folder");
        javaversionbutton.getStyleClass().add("optionButton");
        javaversionbutton.setOnAction(evt -> {

            final FileChooser chooser = new FileChooser();
            chooser.getExtensionFilters().add(new ExtensionFilter("Java executable", "java.exe", "java"));
            File selectedFile = chooser.showOpenDialog(Launcher.getStage());
            if (selectedFile != null) {
                javaversionfield.setText(FileUtil.SETTINGS.javaPath = selectedFile.getPath());
            }
        });

        // folder

//        final Label baseDir = new Label("Folder");
//        baseDir.setStyle("-fx-padding: 20px 0px 10px 0px;");
//
//        final TextField baseDirField = new TextField(FileUtil.SETTINGS.baseDir);
//        baseDirField.setEditable(false);
//
//        final Button baseDirFinder = new Button("Open Folder");
//        baseDirFinder.getStyleClass().add("optionButton");
//        baseDirFinder.setOnAction(evt -> {
//            final DirectoryChooser chooser = new DirectoryChooser();
//            chooser.setInitialDirectory(new File(FileUtil.SETTINGS.baseDir));
//            final File fl = chooser.showDialog(Launcher.getStage());
//            if (fl != null && fl.exists() && fl.isDirectory()) {
//                baseDirField.setText(fl.toString());
//                FileUtil.moveBaseDir(baseDirField.getText());
//            }
//        });
//
        final HBox hbox = new HBox(10);
        hbox.setPrefWidth(500);
//        hbox.getChildren().addAll(baseDirField, baseDirFinder);

        final Button logout = new Button("Logout");
        logout.getStyleClass().add("optionButton");
        logout.setOnAction(evt -> {
            try {
                Launcher.getInstance().getUserService().logout();
                Platform.runLater(() -> Launcher.setScene(Launcher.LOGINSCENE));
            } catch (IOException e) {
                Launcher.onError(e);
            }

            Header.setVisibility(false);
        });

        final Button resetconfigs = new Button("Reset");
        resetconfigs.getStyleClass().add("optionButton");
        resetconfigs.setOnAction(evt -> FileUtil.resetFiles());


        final Button optionalModsButton = new Button("Optional Mods");
        optionalModsButton.getStyleClass().add("optionButton");
        optionalModsButton.setOnAction(ev -> Launcher.setScene(Launcher.OPTIONALMODSSCENE));

        final HBox logouthbox = new HBox(10);
        logouthbox.setPrefWidth(hbox.getPrefWidth());
        logouthbox.getChildren().addAll(logout, resetconfigs, optionalModsButton);

        final Label lar = new Label("Logout & Reset");
        lar.setStyle("-fx-padding: 20px 0px 10px 0px;");

        final HBox javaversion1 = new HBox(10);
        javaversion1.setPrefWidth(hbox.getPrefWidth());
        javaversion1.getChildren().addAll(javaversionfield, javaversionbutton);

        vbox.getChildren().addAll(resolution, resolutioncombobox, hbox, lar, logouthbox,
                javaversion, javaversion1);

        ImageView settingsTrainView = new ImageView(Assets.getImage("train3.png"));
        settingsTrainView.setScaleX(-1);
        settingsTrainView.setTranslateX((-1280 / 1.75) + settingsTrainView.getImage().getWidth());
        settingsTrainView.setTranslateY(360 - settingsTrainView.getImage().getHeight());
        stackpane.getChildren().add(settingsTrainView);
    }
}
