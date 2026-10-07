package com.troblecodings.launcher;

import com.troblecodings.launcher.assets.Assets;
import com.troblecodings.launcher.javafx.*;
import com.troblecodings.launcher.services.UpdateService;
import com.troblecodings.launcher.services.UserService;
import com.troblecodings.launcher.util.FileUtil;
import com.troblecodings.launcher.util.Version;
import javafx.animation.Transition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.concurrent.CompletableFuture;

public class Launcher extends Application {
    private static final Logger log = LogManager.getLogger(Launcher.class);

    public static final Version VERSION = new Version(System.getProperty("app.version"));

    private static Launcher instance = null;

    private static Image[] images = {};

    public static HomeScene HOMESCENE;
    public static OptionsView OPTIONS_VIEW;
    public static LoginScene LOGINSCENE;
    public static MicrosoftLoginScene MICROSOFTLOGINSCENE;
    public static CreditsScene CREDITSSCENE;
    public static OptionalModsScene OPTIONALMODSSCENE;

    private Stage stage;

    private UserService userService;


    private final UpdateService updateService = new UpdateService();

    public UserService getUserService() {
        return userService;
    }

    public Launcher() {
        instance = this;
    }

    @Override
    public void init() throws IOException {
        log.info("Starting Launcher v{}...", System.getProperty("app.version"));

        FileUtil.init();
        FileUtil.readSettings();

        if (FileUtil.SETTINGS == null)
            FileUtil.SETTINGS = new FileUtil.SettingsData();

        boolean update = true;
        Parameters params = getParameters();

        for (String param : params.getRaw()) {
            log.debug("Iterating over parameter: " + param);

            if ("--no-update".equals(param)) {
                log.warn("Updates disabled.");
                update = false;
            }

            if ("--debug".equals(param) || "-d".equals(param)) {
                Configurator.setRootLevel(Level.DEBUG);
                log.debug("Debug logging enabled.");
            }
        }

        boolean updatesAvailable = updateService.checkForUpdates();

        // TODO: Turn this into a dialog
        if (update && updatesAvailable) {
            try {
                updateService.doUpdate();
            } catch (final Exception ex) {
                log.error("Failed to update!", ex);
            }
        }

        FileUtil.migrateOldDirectory();

        log.debug("Data directory: {}", FileUtil.SETTINGS.baseDir);
        log.debug("Loading background images");

        String[] imagesToLoad = {
                "/images/background.png",
                "/images/background_2.png",
                "/images/background_3.png",
                "/images/background_4.png",
                "/images/background_5.png"
        };

        images = new Image[imagesToLoad.length];

        CompletableFuture.runAsync(() -> {
            try {
                for(int i = 0; i < imagesToLoad.length; i++) {
                    log.debug("Loading image {}", imagesToLoad[i]);
                    images[i] = new Image(imagesToLoad[i]);
                }
            } catch (Exception ex) {
                log.error("Failed to load background images:", ex);
            }
        });

        userService = new UserService();
    }

    @Override
    public void start(Stage stage) {
        this.stage = stage;

        MICROSOFTLOGINSCENE = new MicrosoftLoginScene();
        OPTIONS_VIEW = new OptionsView();
        HOMESCENE = new HomeScene();
        LOGINSCENE = new LoginScene();
        CREDITSSCENE = new CreditsScene();
        OPTIONALMODSSCENE = new OptionalModsScene();

        userService.loadLocalSession();
        userService.refreshSession();

        boolean authStatus = userService.isLoggedIn();
        stage.setScene(authStatus ? HOMESCENE : LOGINSCENE);

        stage.getIcons().add(Assets.getImage("images/icon.png"));

        Header.setVisibility(authStatus);

        stage.setWidth(1280);
        stage.setHeight(720);
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setTitle("GIRC Launcher");
        stage.show();
    }

    @Override
    public void stop() {
        log.info("Stopping...");
        FileUtil.saveSettings();
    }

    public static void setupScene(Scene scene, StackPane stackpane) {
        final ImageView backgroundImg = new ImageView();

        Transition animation = new Transition() {
            {
                setCycleDuration(Duration.seconds(20)); // total time for animation
                setRate(0.5);
                setCycleCount(INDEFINITE);
            }

            @Override
            protected void interpolate(double fraction) {
                if (images.length == 0) return;

                int index = (int) (fraction * (images.length - 1));
                backgroundImg.setImage(images[index]);
            }
        };

        animation.play();

        stackpane.getChildren().add(backgroundImg);
        stackpane.getChildren().add(new Header(scene));
        stackpane.getChildren().add(new Footer(scene));
        scene.setFill(Color.TRANSPARENT);
        scene.getStylesheets().add(Assets.getStyleSheet("css/style.css"));
    }

    public static Scene getScene() {
        return getInstance().stage.getScene();
    }

    public static void setScene(Scene scene) {
        getInstance().stage.setScene(scene);
    }

    public static Stage getStage() {
        return getInstance().stage;
    }

    public static void onError(Throwable e) {
        // Return here since we cannot show any error.
        if (e == null) {
            log.error("Error found but was passed null!");
            return;
        } else if (e.getMessage() == null)
            log.trace("", e);
        else
            log.trace(e.getMessage(), e);

        // See if this can be made better, seems overly clunky-like to me, but any other method doesn't generate a stack-trace.
        // toString and getMessage only return the String representation of what the exception actually is.
        if (getInstance().stage != null && getInstance().stage.isShowing()) {
            try {
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);

                e.printStackTrace(pw);

                ErrorScene errorScene = new ErrorScene(sw.toString(), getInstance().stage.getScene());

                Platform.runLater(() -> Launcher.setScene(errorScene));

                sw.close();
                pw.close();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }

    /**
     * Gets the Launcher instance.
     *
     * @return The Launcher instance.
     */
    public static Launcher getInstance() {
        return instance;
    }
}
