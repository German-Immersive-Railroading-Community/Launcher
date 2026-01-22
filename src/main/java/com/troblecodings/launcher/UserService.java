package com.troblecodings.launcher;

import com.google.gson.JsonObject;
import com.troblecodings.launcher.util.FileUtil;
import com.troblecodings.launcher.util.LauncherPaths;
import net.lenni0451.commons.httpclient.HttpClient;
import net.raphimc.minecraftauth.MinecraftAuth;
import net.raphimc.minecraftauth.java.JavaAuthManager;
import net.raphimc.minecraftauth.java.model.MinecraftProfile;
import net.raphimc.minecraftauth.java.model.MinecraftToken;
import net.raphimc.minecraftauth.msa.model.MsaDeviceCode;
import net.raphimc.minecraftauth.msa.service.impl.DeviceCodeMsaAuthService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

/**
 * New user service, using device code authentication. Fancy!
 */
public final class UserService {
    private static final Logger log = LogManager.getLogger(UserService.class);

    private final HttpClient client;

    private JavaAuthManager session = null;

    public UserService() {
        this.client = MinecraftAuth.createHttpClient("MinecraftAuth: GIRC Launcher");
    }

    /**
     * Checks if the user is logged in by validating if {@link UserService#session} is not null and if the
     * session has a valid Minecraft token.
     *
     * @return true if the user is logged in; false otherwise.
     */
    public boolean isLoggedIn() {
        return session != null && session.getMinecraftToken().hasValue();
    }

    /**
     * Tries to load a local session from disk.
     */
    public void loadLocalSession() {
        if (!Files.exists(LauncherPaths.getSessionFilePath())) {
            log.info("No local session found.");
            return;
        }

        JsonObject json;

        try (BufferedReader reader = Files.newBufferedReader(LauncherPaths.getSessionFilePath())) {
            json = FileUtil.GSON.fromJson(reader, JsonObject.class);
        } catch (final IOException e) {
            log.error("Failed loading local session!", e);
            return;
        }

        session = JavaAuthManager.fromJson(client, json);
        session.getChangeListeners().add(this::saveSession);
    }

    /**
     * Log in to Minecraft services.
     * <p>
     * This operation should be run from a background thread, as device code response polling is a blocking operation.
     *
     * @param callback Callback with device code information, executed on the JavaFX Application thread.
     * @return true on successful login or if already logged in; false otherwise.
     */
    public boolean login(Consumer<MsaDeviceCode> callback) {
        if (session != null && session.getMinecraftToken().hasValue()) {
            log.info("Already logged in.");
            return true;
        }

        JavaAuthManager.Builder builder = JavaAuthManager.create(client);

        try {
            session = builder.login(DeviceCodeMsaAuthService::new, callback);
            // Get these here already so that we don't have to do any refresh operations.
            session.getMinecraftToken().getUpToDate();
            session.getMinecraftProfile().getUpToDate();
            session.getChangeListeners().add(this::saveSession);
            saveSession();
        } catch (TimeoutException timeout) {
            log.warn("Timeout while waiting for device code!", timeout);
        } catch (IOException | InterruptedException e) {
            log.error("Failed to login!", e);
        }

        return session != null && session.getMinecraftToken().hasValue();
    }

    /**
     * Performs "logout" of a Minecraft session.
     * <p>
     * Note: this basically just deletes the session file and sets {@link UserService#session} to null.
     *
     * <p>
     * TODO: Check if there is a better way to log out.
     *
     * @throws IOException on I/O error when deleting session file.
     */
    public void logout() throws IOException {
        if (session == null) {
            log.debug("No session to logout.");
            return;
        }

        session = null;
        Files.deleteIfExists(LauncherPaths.getSessionFilePath());

        log.info("Successfully logged out.");
    }

    /**
     * Saves the current session. If there is no current session, this method returns.
     * <p>
     * Intended to be called by registering a callback with {@link JavaAuthManager}'s getChangeListeners.
     */
    public void saveSession() {
        if (session == null) return;

        JsonObject json = JavaAuthManager.toJson(session);

        try {
            Files.write(LauncherPaths.getSessionFilePath(), FileUtil.GSON.toJson(json).getBytes(StandardCharsets.UTF_8));
        } catch (final IOException e) {
            log.error("Failed saving session locally!", e);
        }
    }

    private String getOrDefault(final JSONObject json, final String id, final String def) {
        if (json.has(id))
            return json.getString(id);
        log.warn("Couldn't find {} in {}! Using default!", id, json.toString());
        return def;
    }

    public String[] make(JSONObject json) {
        if (session == null || !session.getMinecraftProfile().hasValue()) return null;

        MinecraftProfile profile = session.getMinecraftProfile().getCached();
        MinecraftToken token = session.getMinecraftToken().getCached();

        Map<String, String> list = new HashMap<>();
        list.put("${auth_player_name}", profile.getName());
        list.put("${version_name}", getOrDefault(json, "id", "1.12.2"));
        list.put("${game_directory}", FileUtil.SETTINGS.baseDir);
        list.put("${assets_root}", FileUtil.ASSET_DIR);
        final JSONObject obj = json.getJSONObject("assetIndex");
        list.put("${assets_index_name}", getOrDefault(obj, "id", "1.12"));
        list.put("${auth_uuid}", profile.getId().toString());
        list.put("${auth_access_token}", token.getToken());
        list.put("${user_type}", token.getType());

        String[] args = json.getString("minecraftArguments").split(" ");
        for (int i = 0; i < args.length; i++) {
            String newArgs = list.get(args[i]);
            if (newArgs != null) args[i] = newArgs;
        }

        return args;
    }
}
