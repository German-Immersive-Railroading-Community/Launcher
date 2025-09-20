package com.troblecodings.launcher;

import com.troblecodings.launcher.assets.Assets;
import com.troblecodings.launcher.javafx.*;
import com.troblecodings.launcher.util.AuthUtil;
import com.troblecodings.launcher.util.FileUtil;
import com.troblecodings.launcher.util.StartupUtil;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Launcher extends Application {
    private static Logger logger;
    private static Launcher instance = null;

    private static final List<Image> images = new ArrayList<>();

    public static HomeScene HOMESCENE;
    public static OptionsScene OPTIONSSCENE;
    public static LoginScene LOGINSCENE;
    public static MicrosoftLoginScene MICROSOFTLOGINSCENE;
    public static CreditsScene CREDITSSCENE;
    public static OptionalModsScene OPTIONALMODSSCENE;

    private Stage stage;

    public Launcher() {
        instance = this;
    }

    @Override
    public void init() {
        FileUtil.init();
        FileUtil.readSettings();

        if (FileUtil.SETTINGS == null)
            FileUtil.SETTINGS = new FileUtil.SettingsData();

        System.setProperty("app.root", FileUtil.SETTINGS.baseDir);
        logger = LogManager.getLogger("GIRC");
        logger.info("Initializing...");

        boolean update = true;

        Parameters params = getParameters();

        for (String param : params.getRaw()) {
            logger.info("Iterating over parameter: {param}", param);

            if ("--no-update".equals(param)) {
                logger.info("Skipping updates!");
                update = false;
            } else {
                logger.warn("unknown parameter: {param}", param);
            }
        }

        if (update)
            StartupUtil.update();

        // loading images into list
        images.add(Assets.getImage("background.png"));
        images.add(Assets.getImage("background_2.png"));
        images.add(Assets.getImage("background_3.png"));
        images.add(Assets.getImage("background_4.png"));
        images.add(Assets.getImage("background_5.png"));
        images.add(images.get(0));
        
        OPTIONSSCENE = new OptionsScene();
        HOMESCENE = new HomeScene();
        LOGINSCENE = new LoginScene();
        CREDITSSCENE = new CreditsScene();
        OPTIONALMODSSCENE = new OptionalModsScene();
    }

    @Override
    public void start(Stage stage) {
        this.stage = stage;
        MICROSOFTLOGINSCENE = new MicrosoftLoginScene();

        boolean authStatus = AuthUtil.checkSession();
        stage.setScene(authStatus ? HOMESCENE : LOGINSCENE);

        stage.getIcons().add(Assets.getImage("icon.png"));

        Header.setVisibility(authStatus);

        stage.setWidth(1280);
        stage.setHeight(720);
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setTitle("GIR Launcher");
        stage.show();
    }

    @Override
    public void stop() {
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
                int index = (int) (fraction * (images.size() - 1));
                backgroundImg.setImage(images.get(index));
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

    public static Logger getLogger() {
        return logger;
    }
}
