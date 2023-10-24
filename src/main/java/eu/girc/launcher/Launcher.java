package eu.girc.launcher;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import eu.girc.launcher.javafx.*;
import eu.girc.launcher.util.AuthUtil;
import eu.girc.launcher.util.FileUtil;
import javafx.animation.Transition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Launcher extends Application {
    /**
     * Global GSON instance.
     */
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private static final List<Image> images = new ArrayList<>();

    public static HomeScene HOMESCENE;
    public static OptionsScene OPTIONSSCENE;
    public static LoginScene LOGINSCENE;
    public static MicrosoftLoginScene MICROSOFTLOGINSCENE;
    public static CreditsScene CREDITSSCENE;
    public static OptionalModsScene OPTIONALMODSSCENE;

    private static Launcher instance = null;
    private final Logger LOGGER;
    private Stage stage;

    public Launcher() {
        instance = this;

        LauncherPaths.build();
        System.setProperty("config_dir", LauncherPaths.getConfigDir().toString());

        LOGGER = LogManager.getLogger();
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
        scene.getStylesheets().add(getStyleSheet("style.css"));
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
            getInstance().LOGGER.error("Error found but was passed null!");
            return;
        } else if (e.getMessage() == null)
            getInstance().LOGGER.error("", e);
        else
            getInstance().LOGGER.error(e.getMessage(), e);

        // See if this can be made better, seems overly clunky-like to me, but any other
        // method doesn't generate a stack-trace.
        // toString and getMessage only return the String representation of what the
        // exception actually is.
        if (getInstance().stage != null && getInstance().stage.isShowing()) {
            try (final StringWriter sw = new StringWriter(); final PrintWriter pw = new PrintWriter(sw)) {
                e.printStackTrace(pw);
                ErrorScene errorScene = new ErrorScene(sw.toString(), getInstance().stage.getScene());
                Platform.runLater(() -> Launcher.setScene(errorScene));
            } catch (final IOException ioe) {
                getLogger().error("Failed to present error.", ioe);
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
        return getInstance().LOGGER;
    }

    public static String getStyleSheet(String name) {
        return Objects.requireNonNull(Launcher.class.getResource(name)).toExternalForm();
    }

    /**
     * @param name The name of the image to retrieve.
     * @return The image, or null if not found or an exception occurs.
     */
    public static Image getImage(String name) {
        try (InputStream imageStream = Launcher.class.getResourceAsStream("images/" + name)) {
            if (imageStream == null) {
                return null;
            }

            return new Image(imageStream);
        } catch (final IOException ioe) {
            ioe.printStackTrace();
        }

        return null;
    }

    public static InputStream getResourceAsStream(String name) {
        return Launcher.class.getResourceAsStream(name);
    }

    @Override
    public void init() {
        LOGGER.info("GIRC-Launcher v{}", BuildConfig.VERSION);
        FileUtil.readSettings();

        if (FileUtil.SETTINGS == null)
            FileUtil.SETTINGS = new FileUtil.SettingsData();

        // boolean update = false;

        Parameters params = getParameters();

        for (String param : params.getRaw()) {
            LOGGER.info("Iterating over parameter: " + param);

            // if ("--no-update".equals(param)) {
            // LOGGER.info("Skipping updates!");
            // update = false;
            // }
        }

        // if (update)
        // StartupUtil.update();

        // loading images into list
        images.add(getImage("background.png"));
        images.add(getImage("background_2.png"));
        images.add(getImage("background_3.png"));
        images.add(getImage("background_4.png"));
        images.add(getImage("background_5.png"));
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
        boolean authStatus = AuthUtil.login();
        final Scene scene = new Scene(new Pane());

        SceneManager.setScene(scene);
        SceneManager.switchView(authStatus ? View.HOME: View.LOGIN);

        stage.getIcons().add(getImage("icon.png"));

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
}
