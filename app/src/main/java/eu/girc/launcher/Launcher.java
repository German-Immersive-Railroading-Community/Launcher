package eu.girc.launcher;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import eu.girc.launcher.models.AppSettings;
import eu.girc.launcher.utils.Locations;
import javafx.application.Application;
import javafx.stage.Stage;
import org.apache.commons.lang3.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.NoSuchFileException;

public class Launcher extends Application {
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private Logger logger;

    private AppSettings settings;

    @Override
    public void init() throws Exception {
        Locations.ensureDirsCreated();
        System.setProperty("girc.logsPath", Locations.getLogsPath().toString());

        logger = LoggerFactory.getLogger(Launcher.class);

        // Log some very basic system info, can help in debugging
        logger.info("GIRC-Launcher v2.0.0");
        logger.info("OS: {} ({}), OS Version: {}", SystemUtils.OS_NAME, SystemUtils.OS_ARCH, SystemUtils.OS_VERSION);

        logger.debug("Loading settings...");

        try {
            var settings = Files.readString(Locations.getSettingsPath());
            this.settings = GSON.fromJson(settings, AppSettings.class);
        } catch (final NoSuchFileException nsfe) {
            logger.warn("Could not find settings, creating default.");
            this.settings = new AppSettings();
            logger.warn("Writing default settings to disk.");
            Files.writeString(Locations.getSettingsPath(), GSON.toJson(this.settings));
        } catch (final Exception e) {
            logger.error("Failed to load application settings:", e);
            System.exit(1);
        }

        logger.debug("Settings loaded.");
    }

    @Override
    public void start(Stage stage) throws Exception {
        logger.debug("Setting up stage...");

        stage.setTitle("GIRC-Launcher v2.0.0");
        stage.setResizable(true);
        stage.setMinWidth(1280);
        stage.setMinHeight(720);

        logger.debug("Ready!");

        SceneManager.init(stage);
        SceneManager.switchScene(LauncherScene.HOME);

        stage.show();
    }

    @Override
    public void stop() throws Exception {
        logger.info("Exiting application, saving settings to disk.");
        Files.writeString(Locations.getSettingsPath(), GSON.toJson(this.settings));
        logger.info("Goodbye!");
    }

    public static void main(String[] args) {
        launch(args);
    }
}
