package com.troblecodings.launcher;

import com.troblecodings.launcher.assets.Assets;
import com.troblecodings.launcher.javafx.*;
import com.troblecodings.launcher.services.UserService;
import com.troblecodings.launcher.util.FileUtil;
import com.troblecodings.launcher.util.LauncherPaths;
import com.troblecodings.launcher.util.StartupUtil;
import javafx.animation.Transition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
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

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class Launcher extends Application {
    private static Logger logger;
    private static Launcher instance = null;

    private static BufferedImage[] images = {};

    public static HomeScene HOMESCENE;
    public static OptionsScene OPTIONSSCENE;
    public static LoginScene LOGINSCENE;
    public static MicrosoftLoginScene MICROSOFTLOGINSCENE;
    public static CreditsScene CREDITSSCENE;
    public static OptionalModsScene OPTIONALMODSSCENE;

    private Stage stage;

    private UserService userService;

    public UserService getUserService() {
        return userService;
    }

    public Launcher() {
        instance = this;
    }

    @Override
    public void init() throws IOException {
        LauncherPaths.init();
        // This needs to happen before any loggers have the chance to be configured.
        System.setProperty("app.root", FileUtil.SETTINGS.baseDir);

        FileUtil.init();
        FileUtil.readSettings();

        if (FileUtil.SETTINGS == null)
            FileUtil.SETTINGS = new FileUtil.SettingsData();

        logger = LogManager.getLogger(Launcher.class);
        logger.info("Initializing...");

        boolean update = true;

        Parameters params = getParameters();

        for (String param : params.getRaw()) {
            logger.debug("Iterating over parameter: " + param);

            if ("--no-update".equals(param)) {
                logger.warn("Updates disabled.");
                update = false;
            }

            if ("--debug".equals(param) || "-d".equals(param)) {
                Configurator.setRootLevel(Level.DEBUG);
                logger.debug("Debug logging enabled.");
            }
        }

        if (update)
            StartupUtil.update();

        FileUtil.migrateOldDirectory();

        logger.debug("Data directory: {}", FileUtil.SETTINGS.baseDir);
        logger.debug("Loading background images");

        CompletableFuture.runAsync(() -> {
            try {
                // loading images into list
                images = new BufferedImage[]{
                        ImageIO.read(Objects.requireNonNull(getClass().getResource("/background.png"))),
                        ImageIO.read(Objects.requireNonNull(getClass().getResource("/background_2.png"))),
                        ImageIO.read(Objects.requireNonNull(getClass().getResource("/background_3.png"))),
                        ImageIO.read(Objects.requireNonNull(getClass().getResource("/background_4.png"))),
                        ImageIO.read(Objects.requireNonNull(getClass().getResource("/background_5.png"))),
                };
            } catch (IOException e) {
                logger.error("Failed to load background images.", e);
            }
        });

        userService = new UserService();
    }

    @Override
    public void start(Stage stage) {
        this.stage = stage;

        MICROSOFTLOGINSCENE = new MicrosoftLoginScene();
        OPTIONSSCENE = new OptionsScene();
        HOMESCENE = new HomeScene();
        LOGINSCENE = new LoginScene();
        CREDITSSCENE = new CreditsScene();
        OPTIONALMODSSCENE = new OptionalModsScene();

        userService.loadLocalSession();
        userService.refreshSession();

        boolean authStatus = userService.isLoggedIn();
        stage.setScene(authStatus ? HOMESCENE : LOGINSCENE);

        stage.getIcons().add(Assets.getImage("icon.png"));

        Header.setVisibility(authStatus);

        stage.setWidth(1280);
        stage.setHeight(720);
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setTitle("GIRC Launcher");
        stage.show();
    }

    @Override
    public void stop() {
        logger.info("Stopping...");
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
                backgroundImg.setImage(SwingFXUtils.toFXImage(images[index], null));
            }
        };

        animation.play();

        stackpane.getChildren().add(backgroundImg);
        stackpane.getChildren().add(new Header(scene));
        stackpane.getChildren().add(new Footer(scene));
        scene.setFill(Color.TRANSPARENT);
        scene.getStylesheets().add(Assets.getStyleSheet("style.css"));
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
            logger.error("Error found but was passed null!");
            return;
        } else if (e.getMessage() == null)
            logger.trace("", e);
        else
            logger.trace(e.getMessage(), e);

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
