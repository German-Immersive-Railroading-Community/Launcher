package eu.girc.launcher.util;

import eu.girc.launcher.Launcher;
import eu.girc.launcher.LauncherPaths;
import eu.girc.launcher.SceneManager;
import eu.girc.launcher.View;
import eu.girc.launcher.models.GirJson;
import net.hycrafthd.minecraft_authenticator.login.AuthenticationException;
import net.hycrafthd.minecraft_authenticator.login.AuthenticationFile;
import net.hycrafthd.minecraft_authenticator.login.Authenticator;
import net.hycrafthd.minecraft_authenticator.login.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class AuthUtil {

    private static final Logger LOGGER = LogManager.getLogger();

    private static volatile User userSession;

    private AuthUtil() { }

    public static void microsoftLogin(final String authCode) throws AuthenticationException {
        if (userSession != null) {
            return;
        }

        try (OutputStream os = Files.newOutputStream(LauncherPaths.getAuthFile())) {
            final Authenticator authenticator = Authenticator.ofMicrosoft(authCode).shouldAuthenticate().build();

            try {
                // Run authentication
                authenticator.run();
            } catch (final AuthenticationException ex) {
                LOGGER.error("Failed to authenticate!", ex);
                return;
            }

            final AuthenticationFile file = authenticator.getResultFile();
            file.writeCompressed(os);

            final Optional<User> user = authenticator.getUser();
            userSession = user.orElse(null);
        } catch (final IOException ioe) {
            LOGGER.error("Failed to complete login from Microsoft.", ioe);
        }
    }

    /**
     * Tries to log in using an already existing authentication file.
     *
     * @return true if logged in; otherwise false
     */
    public static boolean login() {
        if (userSession != null) {
            return true;
        }

        if (!LauncherPaths.getAuthFile().toFile().exists()) {
            return false;
        }

        try (InputStream is = Files.newInputStream(LauncherPaths.getAuthFile())) {
            final AuthenticationFile authFile = AuthenticationFile.readCompressed(is);
            final Authenticator authenticator = Authenticator.of(authFile).shouldAuthenticate().build();

            try (OutputStream os = Files.newOutputStream(LauncherPaths.getAuthFile())) {
                try {
                    authenticator.run();
                } catch (final AuthenticationException aex) {
                    LOGGER.error("Failed to login using an existing authentication file.", aex);
                    final AuthenticationFile file = authenticator.getResultFile();
                    if (file != null) {
                        file.writeCompressed(os);
                    }
                    return false;
                }

                final AuthenticationFile file = authenticator.getResultFile();
                file.writeCompressed(os);
            }

            final Optional<User> user = authenticator.getUser();
            userSession = user.orElse(null);
            return true;
        } catch (final IOException ioe) {
            LOGGER.error("Exception during authentication!", ioe);
            return false;
        }
    }

    public static void logout() {
        try {
            Files.deleteIfExists(LauncherPaths.getAuthFile());
        } catch (final IOException ioe) {
            Launcher.onError(ioe);
        }
        userSession = null;
        SceneManager.switchView(View.LOGIN);
    }

    public static String[] make(final GirJson girJson) {
        final User user = userSession;

        if (user == null) {
            return null;
        }

        Map<String, String> list = new HashMap<>();
        list.put("${auth_player_name}", user.name());
        list.put("${version_name}", girJson.id());
        list.put("${game_directory}", FileUtil.SETTINGS.baseDir);
        list.put("${assets_root}", FileUtil.ASSET_DIR);

        list.put("${assets_index_name}", girJson.assetIndex().id());
        list.put("${auth_uuid}", user.uuid());
        list.put("${auth_access_token}", user.accessToken());
        list.put("${user_type}", user.type());

        String[] arguments = girJson.minecraftArguments().split(" ");
        for (int i = 0; i < arguments.length; i++) {
            String newArg = list.get(arguments[i]);
            if (newArg != null) {
                arguments[i] = newArg;
            }
        }
        return arguments;
    }
}
