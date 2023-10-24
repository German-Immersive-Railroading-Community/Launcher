package eu.girc.launcher.javafx;

import eu.girc.launcher.Launcher;
import eu.girc.launcher.SceneManager;
import eu.girc.launcher.View;
import eu.girc.launcher.util.AuthUtil;
import eu.girc.launcher.util.FileUtil;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.*;
import javafx.scene.control.TextField;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

import java.awt.*;
import java.io.File;

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

        // Ram

        final Label ramlabel = new Label("RAM");
        ramlabel.setStyle("-fx-padding: 0px 0px 10px 0px;");

        final ComboBox<String> ramcombobox = new ComboBox<>();
        ramcombobox.getItems().addAll("1 GB", "2 GB", "4 GB", "6 GB", "8 GB", "10 GB", "16 GB", "20 GB", "24 GB");
        ramcombobox.setEditable(true);
        final int currentRam = FileUtil.SETTINGS.ram / 1000;
        ramcombobox.getItems().stream().filter(str -> str.startsWith(String.valueOf(currentRam))).findFirst()
                .ifPresent(ramcombobox.getSelectionModel()::select);
        ramcombobox.setOnAction(evt -> {
            try {
                final String ram = ramcombobox.getSelectionModel().getSelectedItem();
                FileUtil.SETTINGS.ram = Integer.valueOf(ram.substring(0, ram.length() - 3)) * 1000;
            } catch (Exception e) {
            }
        });

        // Auflösung

        final Label resolution = new Label("Resolution");
        resolution.setStyle("-fx-padding: 20px 0px 10px 0px;");

        final ComboBox<String> resolutioncombobox = new ComboBox<String>();
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
            } catch (Exception e) {
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
            chooser.getExtensionFilters().add(new ExtensionFilter("Java executable", "java.exe"));
            File selectedFile = chooser.showOpenDialog(Launcher.getStage());
            if (selectedFile != null) {
                javaversionfield.setText(FileUtil.SETTINGS.javaPath = selectedFile.getPath());
            }
        });

        // folder

        final Label baseDir = new Label("Folder");
        baseDir.setStyle("-fx-padding: 20px 0px 10px 0px;");

        final TextField baseDirField = new TextField(FileUtil.SETTINGS.baseDir);
        baseDirField.setEditable(false);

        final Button baseDirFinder = new Button("Open Folder");
        baseDirFinder.getStyleClass().add("optionButton");
        baseDirFinder.setOnAction(evt -> {
            final DirectoryChooser chooser = new DirectoryChooser();
            chooser.setInitialDirectory(new File(FileUtil.SETTINGS.baseDir));
            final File fl = chooser.showDialog(Launcher.getStage());
            if (fl != null && fl.exists() && fl.isDirectory()) {
                baseDirField.setText(fl.toString());
                FileUtil.moveBaseDir(baseDirField.getText());
            }
        });

        final HBox hbox = new HBox(10);
        hbox.setPrefWidth(500);
        hbox.getChildren().addAll(baseDirField, baseDirFinder);

        final Button logout = new Button("Logout");
        logout.getStyleClass().add("optionButton");
        logout.setOnAction(evt -> {
            AuthUtil.logout();
            Header.setVisibility(false);
        });

        final Button resetconfigs = new Button("Reset");
        resetconfigs.getStyleClass().add("optionButton");
        resetconfigs.setOnAction(evt -> FileUtil.resetFiles());

        final Button optionalModsButton = new Button("Optional Mods");
        optionalModsButton.getStyleClass().add("optionButton");
        optionalModsButton.setOnAction(ev -> SceneManager.switchView(View.MODS));

        final HBox logouthbox = new HBox(10);
        logouthbox.setPrefWidth(hbox.getPrefWidth());
        logouthbox.getChildren().addAll(logout, resetconfigs, optionalModsButton);

        final Label lar = new Label("Logout & Reset");
        lar.setStyle("-fx-padding: 20px 0px 10px 0px;");

        final HBox javaversion1 = new HBox(10);
        javaversion1.setPrefWidth(hbox.getPrefWidth());
        javaversion1.getChildren().addAll(javaversionfield, javaversionbutton);

        vbox.getChildren().addAll(
                ramlabel,
                ramcombobox,
                resolution,
                resolutioncombobox,
                baseDir,
                hbox,
                lar,
                logouthbox,
                javaversion,
                javaversion1);

        ImageView settingsTrainView = new ImageView(Launcher.getImage("train3.png"));
        settingsTrainView.setScaleX(-1);
        settingsTrainView.setTranslateX((-1280 / 1.75) + settingsTrainView.getImage().getWidth());
        settingsTrainView.setTranslateY(360 - settingsTrainView.getImage().getHeight());
        getChildren().add(settingsTrainView);
    }
}
