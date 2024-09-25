package eu.girc.launcher;

import atlantafx.base.theme.PrimerDark;
import atlantafx.base.theme.PrimerLight;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import eu.girc.launcher.models.AppSettings;
import eu.girc.launcher.utils.LPaths;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Files;

public class Launcher extends Application {
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private Logger logger;

    private AppSettings settings;

    @Override
    public void init() throws Exception {
        LPaths.initialise();
        LPaths.ensureDirsCreated();
        
        System.setProperty("girc.logsPath", LPaths.getLogsPath().toString());

        logger = LogManager.getLogger(Launcher.class);

        logger.info("GIRC-Launcher v2.0.0");
        logger.debug("Loading settings...");

        try {
            var settings = Files.readString(LPaths.getSettingsPath());
            this.settings = GSON.fromJson(settings, AppSettings.class);
        } catch (final Exception e) {
            logger.error("Failed to load application settings:", e);
            logger.error("Falling back to default settings.");
            this.settings = new AppSettings();
            logger.warn("Trying to write default settings.");
            Files.writeString(LPaths.getSettingsPath(), GSON.toJson(this.settings));
        }
        
        logger.debug("Settings loaded.");
    }

    @Override
    public void start(Stage stage) throws Exception {
        // Set the AtlantaFX stylesheets
        // Required to be in this method, and not init(), since Application::setUserAgentStylesheet(...)
        // needs to be called on the JavaFX Application Thread.
        Application.setUserAgentStylesheet(new PrimerLight().getUserAgentStylesheet());
        Application.setUserAgentStylesheet(new PrimerDark().getUserAgentStylesheet());
        
        var scene = new Scene(new AnchorPane());
        
        stage.setScene(scene);
        stage.setResizable(true);
        stage.setTitle("GIRC-Launcher v2.0.0");


        stage.show();
    }

    @Override
    public void stop() throws Exception {
        // Save settings on program exit.
        Files.writeString(LPaths.getSettingsPath(), GSON.toJson(this.settings));
    }
}
