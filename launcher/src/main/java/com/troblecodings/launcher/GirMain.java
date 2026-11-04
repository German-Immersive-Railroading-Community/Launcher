package com.troblecodings.launcher;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.troblecodings.launcher.di.GirModule;
import com.troblecodings.launcher.di.JavaFxModule;
import com.troblecodings.launcher.di.ViewModule;
import com.troblecodings.launcher.services.IUserServiceKt;
import com.troblecodings.launcher.util.FileUtil;
import com.troblecodings.launcher.utils.LauncherPaths;
import com.troblecodings.launcher.views.MainWindow;
import com.troblecodings.launcher.views.NotificationOverlay;
import com.troblecodings.launcher.views.ViewId;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.concurrent.ExecutorService;

public final class GirMain extends Application {
    private static final Logger LOGGER;

    private Injector injector;
    private ViewManager viewManager;
    private IUserServiceKt userService;
    private ExecutorService executorService;

    @Override
    public void init() throws Exception {
        LOGGER.info("GIR-Launcher v{}", BuildConfig.APP_VERSION);

        // all unser zeug liegt hier, also bail wenn pfad-init nicht erfolgreich
        try {
            LauncherPaths.init();
        } catch (final IOException ioe) {
            LOGGER.fatal("Failed to initialise launcher paths.", ioe);
            Platform.exit();
        }

    }

    @Override
    public void start(Stage stage) throws Exception {
        try {
            StackPane contentPane = new StackPane();
            injector = Guice.createInjector(new GirModule(), new JavaFxModule(contentPane), new ViewModule());

            this.viewManager = injector.getInstance(ViewManager.class);
            this.userService = injector.getInstance(IUserServiceKt.class);
            userService.loadSession();
            this.executorService = injector.getInstance(ExecutorService.class);

            var notificationManager = injector.getInstance(NotificationManager.class);

            MainWindow mainWindow = injector.getInstance(MainWindow.class);
            mainWindow.setCenter(new NotificationOverlay(notificationManager, contentPane));

            var scene = new Scene(mainWindow, 760, 520);

            stage.setTitle("GIR Launcher v" + BuildConfig.APP_VERSION);
            stage.setMinWidth(760);
            stage.setMinHeight(520);
            stage.setScene(scene);

            stage.show();
            viewManager.show(ViewId.HOME);
        } catch (Throwable e) {
            LOGGER.fatal("Error during application startup:", e);
            showStartupErrorDialog(e);
            Platform.exit();
        }
    }

    @Override
    public void stop() throws Exception {
        executorService.close();

    }

    /**
     * Displays a startup error dialog offering immediate troubleshooting actions:
     * opening the log file, copying the path to clipboard, and viewing the stacktrace.
     *
     * @param throwable The fatal exception caught during startup.
     */
    private void showStartupErrorDialog(Throwable throwable) {
        Path logFilePath = LauncherPaths.getLogsDir().resolve("launcher.log");

        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Launch Error");
        alert.setHeaderText("The application encountered a critical error during startup.");
        alert.setContentText("Log file location:\n" + logFilePath.toAbsolutePath());

        // Define action buttons
        ButtonType openLogButton = new ButtonType("Open Log", ButtonBar.ButtonData.LEFT);
        ButtonType copyPathButton = new ButtonType("Copy Path", ButtonBar.ButtonData.LEFT);
        ButtonType copyStackTraceButton = new ButtonType("Copy Stack Trace", ButtonBar.ButtonData.LEFT);
        ButtonType exitButton = new ButtonType("Exit", ButtonBar.ButtonData.OK_DONE);

        alert.getButtonTypes().setAll(openLogButton, copyPathButton, copyStackTraceButton, exitButton);

        // Build expandable stacktrace details area
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        throwable.printStackTrace(pw);
        String exceptionText = sw.toString();

        Label label = new Label("Exception stacktrace:");
        TextArea textArea = new TextArea(exceptionText);
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);

        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(label, 0, 0);
        expContent.add(textArea, 0, 1);

        alert.getDialogPane().setExpandableContent(expContent);

        // Handle button interactions
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent()) {
            if (result.get() == openLogButton) {
                openLogTarget(logFilePath);
            } else if (result.get() == copyPathButton) {
                copyToClipboard(logFilePath.toAbsolutePath().toString());
            } else if (result.get() == copyStackTraceButton) {
                copyToClipboard(exceptionText);
            }
        }
    }

    /**
     * Attempts to open the log file or its parent directory using the system default application.
     */
    private void openLogTarget(Path logFilePath) {
        try {
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.OPEN)) {
                if (Files.exists(logFilePath)) {
                    Desktop.getDesktop().open(logFilePath.toFile());
                } else if (Files.exists(logFilePath.getParent())) {
                    Desktop.getDesktop().open(logFilePath.getParent().toFile());
                }
            }
        } catch (Exception ex) {
            LOGGER.error("Failed to open log file automatically", ex);
        }
    }

    /**
     * Copies the specified path string to the system clipboard.
     */
    private void copyToClipboard(String text) {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        content.putString(text);
        clipboard.setContent(content);
    }

    static {
        // This needs to happen before any loggers have the chance to be configured.
        System.setProperty("app.root", FileUtil.SETTINGS.baseDir);

        LOGGER = LogManager.getLogger(GirMain.class.getName());
    }
}
