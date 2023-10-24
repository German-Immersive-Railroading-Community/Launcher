package eu.girc.launcher;

import eu.girc.launcher.javafx.*;
import javafx.scene.Parent;
import javafx.scene.Scene;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public final class SceneManager {
    private static final Logger logger = LogManager.getLogger();
    private static final Map<View, Parent> viewCache = new HashMap<>();

    private static Scene currentScene;

    private SceneManager() {
    }

    public static void setScene(Scene scene) {
        currentScene = scene;
    }

    public static void switchView(View view) {
        if (currentScene == null) {
            logger.error("SceneManager was not assigned a Scene!");
            return;
        }

        logger.debug("Switching view to {}", view);
        Parent root;

        if (viewCache.containsKey(view)) {
            logger.debug("Found view in view cache.");
            root = viewCache.get(view);
        } else {
            logger.debug("Couldn't find view in view cache, constructing.");
            switch (view) {
                case HOME:
                    root = new HomeScene();
                    break;
                case OPTIONS:
                    root = new OptionsScene();
                    break;
                case LOGIN:
                    root = new LoginScene();
                    break;
                case MSLOGIN:
                    root = new MicrosoftLoginScene();
                    break;
                case CREDITS:
                    root = new CreditsScene();
                    break;
                case MODS:
                    root = new OptionalModsScene();
                    break;
                default:
                    logger.error("Tried switching to unsupported view {}", view);
                    return;
            }

            viewCache.put(view, root);
        }

        currentScene.setRoot(root);
    }
}
