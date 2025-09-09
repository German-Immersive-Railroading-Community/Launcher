package com.troblecodings.launcher.services;

import com.google.gson.JsonObject;
import com.troblecodings.launcher.Launcher;
import com.troblecodings.launcher.models.girjson.GirJson;
import com.troblecodings.launcher.util.LauncherPaths;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import net.lenni0451.commons.httpclient.HttpClient;
import net.raphimc.minecraftauth.MinecraftAuth;
import net.raphimc.minecraftauth.step.java.StepMCProfile;
import net.raphimc.minecraftauth.step.java.session.StepFullJavaSession;
import net.raphimc.minecraftauth.step.msa.StepMsaDeviceCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public final class UserService {
    private final HttpClient client;
    private final Logger logger;

    private boolean isLocalSessionPresent;

    private BooleanProperty loggedIn = new SimpleBooleanProperty(false);

    private volatile StepFullJavaSession.FullJavaSession javaSession;

    public UserService() {
        client = MinecraftAuth.createHttpClient();
        logger = LoggerFactory.getLogger(UserService.class);
    }

    public boolean isLoggedIn() {
        return loggedIn.get();
    }

    public void setLoggedIn(boolean value) {
        loggedIn.set(value);
    }

    public BooleanProperty loggedInProperty() {
        return loggedIn;
    }

    public synchronized void loadSession() throws IOException {
        logger.debug("Trying to load local session.");
        if (!LauncherPaths.getSessionFile().toFile().exists()) {
            logger.info("No local session found.");
            javaSession = null;
            isLocalSessionPresent = false;
            return;
        }

        logger.info("Local session found.");
        final JsonObject session = Launcher.GSON.fromJson(Files.newBufferedReader(LauncherPaths.getSessionFile()), JsonObject.class);
        javaSession = MinecraftAuth.JAVA_DEVICE_CODE_LOGIN.fromJson(session);

        logger.debug("Session expired: {}", javaSession.isExpired());
        // This is true after every application restart for some reason, oh well
        // Also, we know that the session didn't expire, because we explicitly checked before.
        logger.debug("Session outdated: {}", javaSession.isExpiredOrOutdated());

        if (!javaSession.isExpired()) {
            setLoggedIn(true);
        }

        logger.info("Local session loaded.");
    }

    public synchronized void saveSession() throws IOException {
        JsonObject json = MinecraftAuth.JAVA_DEVICE_CODE_LOGIN.toJson(javaSession);
        Files.writeString(LauncherPaths.getSessionFile(), Launcher.GSON.toJson(json));
        isLocalSessionPresent = true;
        setLoggedIn(true);
        logger.debug("Session saved.");
    }

    public boolean isLocalSessionPresent() {
        return isLocalSessionPresent;
    }

    public boolean isValidSession() {
        return javaSession != null && !javaSession.isExpired();
    }

    /**
     * Tries to authenticate with XBL.
     *
     * @param deviceCodeCallback This callback will be called on a non-JavaFX thread. You need to account for this if this callback is used in conjunction with UI.
     * @throws Exception Idk the login process may not like us very much sometimes
     */
    public synchronized void login(Consumer<StepMsaDeviceCode.MsaDeviceCode> deviceCodeCallback) throws Exception {
        if (isValidSession()) {
            javaSession = MinecraftAuth.JAVA_DEVICE_CODE_LOGIN.refresh(client, javaSession);
            return;
        }

        javaSession = MinecraftAuth.JAVA_DEVICE_CODE_LOGIN.getFromInput(client, new StepMsaDeviceCode.MsaDeviceCodeCallback(msaDeviceCode -> {
            logger.debug("Device code: {}, Uri: {}", msaDeviceCode.getUserCode(), msaDeviceCode.getVerificationUri());
            deviceCodeCallback.accept(msaDeviceCode);
        }));

        logger.info("Successfully logged in.");
        saveSession();
    }

    public synchronized void refresh() throws Exception {
        if (javaSession == null) {
            logger.info("Could not find local session to refresh.");
            return;
        }

        if (!javaSession.isExpired()) {
            logger.info("Valid local session, skipping refresh.");
            return;
        }

        logger.debug("Trying to refresh session.");
        javaSession = MinecraftAuth.JAVA_DEVICE_CODE_LOGIN.refresh(client, javaSession);
        logger.info("Successfully refreshed session.");
        saveSession();
    }

    public synchronized void logout() throws IOException {
        javaSession = null;
        Files.deleteIfExists(LauncherPaths.getSessionFile());
        logger.info("Successfully logged out.");
    }

    @Nullable
    public StepMCProfile.MCProfile getMcProfile() {
        if (javaSession == null) return null;
        return javaSession.getMcProfile();
    }

    public String[] makeArguments(final GirJson json) {
        if (javaSession == null)
            return null;

        Map<String, String> list = new HashMap<>();
        list.put("${auth_player_name}", javaSession.getMcProfile().getName());
        list.put("${version_name}", json.id());
        list.put("${game_directory}", LauncherPaths.getGameDataDir().toString());
        list.put("${assets_root}", LauncherPaths.getAssetsDir().toString());
        list.put("${assets_index_name}", json.assetIndex().id());
        list.put("${auth_uuid}", javaSession.getMcProfile().getId().toString());
        list.put("${auth_access_token}", javaSession.getPlayerCertificates().getMcToken().getAccessToken());
        list.put("${user_type}", javaSession.getPlayerCertificates().getMcToken().getTokenType());

        String[] arguments = json.minecraftArguments().split(" ");
        for (int i = 0; i < arguments.length; i++) {
            String newArg = list.get(arguments[i]);
            if (newArg != null)
                arguments[i] = newArg;
        }
        return arguments;
    }
}
