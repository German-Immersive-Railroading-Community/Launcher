package com.troblecodings.launcher;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.troblecodings.launcher.assets.Assets;
import com.troblecodings.launcher.javafx.views.MainView;
import com.troblecodings.launcher.models.AppSettings;
import com.troblecodings.launcher.services.UserService;
import com.troblecodings.launcher.util.LauncherPaths;
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
import org.apache.commons.lang3.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class Launcher extends Application {
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().serializeNulls().create();

    private static final Executor appExecutor = Executors.newScheduledThreadPool(1);

    private static Launcher instance = null;
    private static boolean experiments = true;

    private static final List<Image> images = new ArrayList<>();

    public static HomeScene HOMESCENE;
    public static OptionsScene OPTIONSSCENE;
    public static LoginScene LOGINSCENE;
    public static CreditsScene CREDITSSCENE;
    public static OptionalModsScene OPTIONALMODSSCENE;

    private Logger logger;
    private Stage stage;
    private AppSettings appSettings;
    private UserService userService;

    public Launcher() {
        instance = this;
    }

    @Override
    public void init() throws Exception {
        LauncherPaths.init();
        System.setProperty("girc.logsPath", LauncherPaths.getLogsDir().toString());

        logger = LoggerFactory.getLogger(Launcher.class);

        // Log some very basic system info, can help in debugging
        logger.info("{} v{}", LauncherConstants.APP_NAME, LauncherConstants.APP_VERSION);
        logger.info("OS: {} ({}), OS Version: {}", SystemUtils.OS_NAME, SystemUtils.OS_ARCH, SystemUtils.OS_VERSION);

        logger.debug("Loading settings...");

        try {
            this.appSettings = GSON.fromJson(Files.newBufferedReader(LauncherPaths.getSettingsFile()), AppSettings.class);
        } catch (final NoSuchFileException ignored) {
            logger.warn("Could not find settings, creating default.");
            appSettings = new AppSettings();
            logger.warn("Writing default settings to disk.");
            Files.writeString(LauncherPaths.getSettingsFile(), GSON.toJson(appSettings));
        } catch (final Exception e) {
            logger.error("Failed to load application settings:", e);
        }

        logger.debug("Settings loaded.");

        boolean update = appSettings.isAppUpdatesEnabled();

        Parameters params = getParameters();

        for (String param : params.getRaw()) {
            logger.debug("Iterating over parameter: {}", param);

            if ("--no-update".equals(param)) {
                update = false;
            }

            if ("-ex".equalsIgnoreCase(param)) {
                experiments = true;
            }
        }
        experiments = true;

        if (update) {
            logger.info("Checking for updates...");
            // StartupUitl.update();
        } else {
            logger.info("Updates are disabled.");
        }

        // loading images into list
        images.add(Assets.getImage("background.png"));
        images.add(Assets.getImage("background_2.png"));
        images.add(Assets.getImage("background_3.png"));
        images.add(Assets.getImage("background_4.png"));
        images.add(Assets.getImage("background_5.png"));
        images.add(images.getFirst());

        if (!experiments) {
            OPTIONSSCENE = new OptionsScene();
            HOMESCENE = new HomeScene();
            LOGINSCENE = new LoginScene();
            CREDITSSCENE = new CreditsScene();
            OPTIONALMODSSCENE = new OptionalModsScene();
        }

        userService = new UserService();
    }

    @Override
    public void start(Stage stage) throws IOException {
        this.stage = stage;

        appExecutor.execute(() -> {
            try {
                userService.loadSession();

                if (userService.isValidSession()) {
                    logger.info("Valid session detected.");
                    return;
                }

                userService.refresh();
            } catch (Exception e) {
                logger.error("Failed to refresh session!", e);
            }
        });

        if (!experiments) {
            boolean authStatus = userService.isValidSession();
            stage.setScene(authStatus ? HOMESCENE : LOGINSCENE);
            Header.setVisibility(authStatus);
        } else {
            final ViewManager viewManager = new ViewManager();
            final MainView mainView = new MainView(viewManager, userService);
            final Scene scene = new Scene(mainView.build());
            scene.getStylesheets().add(Objects.requireNonNull(Launcher.class.getResource("ok.css")).toExternalForm());
            //scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("style.css")).toExternalForm());
            stage.setScene(scene);
        }

        stage.getIcons().add(Assets.getImage("icon.png"));


        stage.setMinWidth(1280);
        stage.setMinHeight(720);

        stage.initStyle(StageStyle.DECORATED);
        stage.setTitle(LauncherConstants.APP_NAME);

        // TODO: temporary for multi-monitor development, remove me
        Platform.runLater(() -> {
            stage.setY(2600);
            stage.centerOnScreen();
            logger.warn("REMOVE ME");
        });

        stage.show();
    }

    @Override
    public void stop() throws IOException {
        logger.info("Exit requested");
        ((ScheduledExecutorService)appExecutor).shutdownNow();

        logger.info("Saving settings...");
        Files.writeString(LauncherPaths.getSettingsFile(), GSON.toJson(appSettings));

        logger.info("Goodbye!");
    }

    public UserService getUserService() {
        return userService;
    }

    public AppSettings getAppSettings() {
        return appSettings;
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
                int index = (int) (fraction * (images.size() - 1));
                backgroundImg.setImage(images.get(index));
            }
        };

        animation.play();

        stackpane.getChildren().add(backgroundImg);
        stackpane.getChildren().add(new Header(scene));
        stackpane.getChildren().add(new Footer(scene));
        scene.setFill(Color.TRANSPARENT);
        scene.getStylesheets().add(Objects.requireNonNull(Launcher.class.getResource("style.css")).toExternalForm());
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
            instance.logger.error("Error found but was passed null!");
            return;
        } else if (e.getMessage() == null)
            instance.logger.trace("", e);
        else
            instance.logger.trace(e.getMessage(), e);

        // See if this can be made better, seems overly clunky-like to me, but any other method doesn't generate a stack-trace.
        // toString and getMessage only return the String representation of what the exception actually is.
        if (getInstance().stage != null && getInstance().stage.isShowing()) {
            try (StringWriter sw = new StringWriter(); PrintWriter pw = new PrintWriter(sw)) {

                e.printStackTrace(pw);

                ErrorScene errorScene = new ErrorScene(sw.toString(), getInstance().stage.getScene());

                Platform.runLater(() -> Launcher.setScene(errorScene));
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

    public static Executor getAppExecutor() {
        return appExecutor;
    }

    public static void main(String[] args) {
        Launcher.launch(args);
    }
}
