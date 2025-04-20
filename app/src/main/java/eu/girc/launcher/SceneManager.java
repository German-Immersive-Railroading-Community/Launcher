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

public class SceneManager {
    private LauncherScene currentScene;

    private final Logger logger;

    private final Stage stage;

    private final Map<LauncherScene, Scene> sceneCache;

    public SceneManager(Stage stage) {
        this.logger = LoggerFactory.getLogger(SceneManager.class);
        this.stage = stage;
        this.sceneCache = new HashMap<>();
    }

    public void switchScene(LauncherScene launcherScene) {
        if (currentScene == launcherScene) {
            logger.debug("Already on this scene!");
            return;
        }

        logger.debug("Switching scene: {} -> {}", currentScene, launcherScene);

        if (sceneCache.containsKey(launcherScene)) {
            logger.debug("Cache hit");
            stage.setScene(sceneCache.get(launcherScene));
            return;
        }

        try {
            logger.debug("Cache miss");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ui/" + launcherScene.getPath()));
            Parent root = loader.load();

            Object controller = loader.getController();
            if (controller instanceof IController ctr) {
                logger.debug("Able to set scene manager");
                ctr.setSceneManager(this);
            } else {
                logger.debug("Unable to set scene manager");
            }

            Scene scene = new Scene(root);
            stage.setScene(scene);
            sceneCache.put(launcherScene, scene);
        } catch (final Exception e) {
            logger.error("Failed to switch scenes: ", e);
        }
    }
}
