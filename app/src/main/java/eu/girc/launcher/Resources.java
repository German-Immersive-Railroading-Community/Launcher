package eu.girc.launcher;

import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.Objects;
import java.util.prefs.Preferences;

public final class Resources {
    public static final String BASE_MODULE_PATH = "/eu/girc/launcher/";

    public static InputStream getResourceAsStream(String resource) {
        String path = resolve(resource);
        return Objects.requireNonNull(
                Launcher.class.getResourceAsStream(resolve(path)),
                "Resource not found: " + path
                                     );
    }

    public static URI getResource(String resource) {
        String path = resolve(resource);
        URL url = Objects.requireNonNull(Launcher.class.getResource(resolve(path)), "Resource not found: " + path);
        return URI.create(url.toExternalForm());
    }

    public static String resolve(String resource) {
        Objects.requireNonNull(resource);
        return resource.startsWith("/") ? resource : BASE_MODULE_PATH + resource;
    }

    public static String getPropertyOrEnv(String propertyKey, String envKey) {
        return System.getProperty(propertyKey, System.getenv(envKey));
    }

    public static Preferences getPreferences() {
        return Preferences.userRoot().node("atlantafx");
    }
    
    
}
