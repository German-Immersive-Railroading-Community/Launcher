package eu.girc.launcher;

import eu.girc.launcher.ui.IController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public final class SceneManager {
    private static LauncherScene currentScene;

    private static final Logger logger = LoggerFactory.getLogger(SceneManager.class);

    private static Stage stage;

    private static final Map<LauncherScene, Scene> sceneCache = new HashMap<>();

    private SceneManager() { }

    public static void init(Stage primaryStage) {
        logger.debug("Initializing SceneManager");
        stage = primaryStage;
    }

    public static void switchScene(LauncherScene launcherScene) {
        if (currentScene == launcherScene) {
            logger.debug("Already on this scene!");
            return;
        }

        logger.debug("Switching scene: {} -> {}", currentScene, launcherScene);
        currentScene = launcherScene;

        if (sceneCache.containsKey(launcherScene)) {
            logger.debug("Cache hit");
            stage.setScene(sceneCache.get(launcherScene));
            return;
        }

        try {
            logger.debug("Cache miss");
            FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource("ui/" + launcherScene.getPath()));
            Parent root = loader.load();

            Scene scene = new Scene(root);
            stage.setScene(scene);
            sceneCache.put(launcherScene, scene);
        } catch (final Exception e) {
            logger.error("Failed to switch scenes: ", e);
        }
    }
}
