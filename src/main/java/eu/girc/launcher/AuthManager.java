package eu.girc.launcher;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
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
import java.util.Optional;

public final class AuthManager {
    private static final Logger logger = LogManager.getLogger();

    private static final BooleanProperty loggedIn = new SimpleBooleanProperty(false);

    private static volatile User userSession = null;

    private AuthManager() { }

    public static boolean isLoggedIn() {
        return loggedIn.get();
    }

    /**
     * Used to retrieve the logged in property to bind to for other properties.
     *
     * @return The read-only logged-in boolean property instance.
     */
    public static ReadOnlyBooleanProperty loggedInProperty() {
        return loggedIn;
    }

    public static void login() throws IOException, AuthenticationException {
        // We are already logged in (or a session is already opened), so let's not disturb that.
        if (userSession != null) {
            if (!loggedIn.get()) loggedIn.set(true);
            return;
        }

        // There is no existing authentication file, which we need for this login path.
        if (!LauncherPaths.getAuthFile().toFile().exists()) {
            if (loggedIn.get()) loggedIn.set(false);
            return;
        }

        try (InputStream is = Files.newInputStream(LauncherPaths.getAuthFile())) {
            final AuthenticationFile authFile = AuthenticationFile.readCompressed(is);
            final Authenticator authenticator = Authenticator.of(authFile).shouldAuthenticate().build();

            try (OutputStream os = Files.newOutputStream(LauncherPaths.getAuthFile())) {
                try {
                    authenticator.run();
                } catch (final AuthenticationException aex) {
                    logger.error("Failed to login using an existing authentication file.", aex);
                    final AuthenticationFile file = authenticator.getResultFile();
                    if (file != null) file.writeCompressed(os);
                    if (loggedIn.get()) loggedIn.set(false);
                    return;
                }

                final AuthenticationFile file = authenticator.getResultFile();
                file.writeCompressed(os);
            }

            final Optional<User> user = authenticator.getUser();
            userSession = user.orElse(null);
            if (!loggedIn.get()) loggedIn.set(true);
        }
    }

    public static void login(final String authCode) throws IOException, AuthenticationException {
        if (userSession != null) {
            if (!loggedIn.get()) loggedIn.set(true);
            return;
        }

        try (OutputStream os = Files.newOutputStream(LauncherPaths.getAuthFile())) {
            final Authenticator authenticator = Authenticator.ofMicrosoft(authCode).shouldAuthenticate().build();

            try {
                // Run authentication
                authenticator.run();
            } catch (final AuthenticationException ex) {
                logger.error("Failed to authenticate!", ex);
                if (loggedIn.get()) loggedIn.set(false);
                return;
            }

            final AuthenticationFile file = authenticator.getResultFile();
            file.writeCompressed(os);

            final Optional<User> user = authenticator.getUser();
            userSession = user.orElse(null);
            if (!loggedIn.get()) loggedIn.set(true);
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

    public static String getUsername() {
        return userSession.name();
    }

    public static String getUuid() {
        return userSession.uuid();
    }

    public static String getAccessToken() {
        return userSession.accessToken();
    }

    public static String getUserType() {
        return userSession.type();
    }
}