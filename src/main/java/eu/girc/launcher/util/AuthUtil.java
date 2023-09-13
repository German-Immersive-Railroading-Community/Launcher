package eu.girc.launcher.util;

import eu.girc.launcher.Launcher;
import net.hycrafthd.minecraft_authenticator.login.AuthenticationException;
import net.hycrafthd.minecraft_authenticator.login.AuthenticationFile;
import net.hycrafthd.minecraft_authenticator.login.Authenticator;
import net.hycrafthd.minecraft_authenticator.login.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class AuthUtil {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final String DEFAULT_ARGS_TEMPLATE = "--username ${auth_player_name} --version ${version_name} --gameDir ${game_directory} --assetsDir ${assets_root} --assetIndex ${assets_index_name} --uuid ${auth_uuid} --accessToken ${auth_access_token} --userType ${user_type} --tweakClass net.minecraftforge.fml.common.launcher.FMLTweaker --versionType Forge";
    private static volatile User userSession;

    private AuthUtil() {
    }

    public static void microsoftLogin(final String authCode) throws AuthenticationException {
        if (userSession != null) {
            return;
        }

        try (OutputStream os = Files.newOutputStream(FileUtil.REMEMBERFILE)) {
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

        if (!FileUtil.REMEMBERFILE.toFile().exists()) {
            return false;
        }

        try (InputStream is = Files.newInputStream(FileUtil.REMEMBERFILE)) {
            final AuthenticationFile authFile = AuthenticationFile.readCompressed(is);
            final Authenticator authenticator = Authenticator.of(authFile).shouldAuthenticate().build();

            try (OutputStream os = Files.newOutputStream(FileUtil.REMEMBERFILE)) {
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
        } catch (final IOException ioex) {
            LOGGER.error("Exception during authentication!", ioex);
            return false;
        }
    }

    public static void logout() {
        try {
            Files.deleteIfExists(FileUtil.REMEMBERFILE);
        } catch (final IOException ioex) {
            Launcher.onError(ioex);
        }
        userSession = null;
        Launcher.setScene(Launcher.LOGINSCENE);
    }

    private static String getOrDefault(final JSONObject json, final String id, final String def) {
        if (json.has(id)) {
            return json.getString(id);
        }

        LOGGER.warn("Couldn't find {} in {}! Using default value {}!", id, json.toString(), def);
        return def;
    }

    public static String[] make(final JSONObject json) {
        final User user = userSession;

        if (user == null) {
            return null;
        }

        Map<String, String> list = new HashMap<>();
        list.put("${auth_player_name}", user.name());
        list.put("${version_name}", getOrDefault(json, "id", "1.12.2"));
        list.put("${game_directory}", FileUtil.SETTINGS.baseDir);
        list.put("${assets_root}", FileUtil.ASSET_DIR);

        final JSONObject obj = json.getJSONObject("assetIndex");
        list.put("${assets_index_name}", getOrDefault(obj, "id", "1.12"));
        list.put("${auth_uuid}", user.uuid());
        list.put("${auth_access_token}", user.accessToken());
        list.put("${user_type}", user.type());

        String[] arguments = getOrDefault(json, "minecraftArguments", DEFAULT_ARGS_TEMPLATE).split(" ");
        for (int i = 0; i < arguments.length; i++) {
            String newArg = list.get(arguments[i]);
            if (newArg != null) {
                arguments[i] = newArg;
            }
        }
        return arguments;
    }

}
