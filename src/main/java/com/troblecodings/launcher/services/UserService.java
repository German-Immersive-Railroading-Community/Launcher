package com.troblecodings.launcher.services;

import com.google.gson.JsonObject;
import com.troblecodings.launcher.Launcher;
import com.troblecodings.launcher.models.girjson.GirJson;
import com.troblecodings.launcher.util.LauncherPaths;
import javafx.application.Platform;
import net.lenni0451.commons.httpclient.HttpClient;
import net.raphimc.minecraftauth.MinecraftAuth;
import net.raphimc.minecraftauth.step.java.session.StepFullJavaSession;
import net.raphimc.minecraftauth.step.msa.StepMsaDeviceCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public final class UserService {
    private final HttpClient client;
    private final Logger logger;

    private volatile StepFullJavaSession.FullJavaSession javaSession;

    public UserService() {
        client = MinecraftAuth.createHttpClient();
        logger = LoggerFactory.getLogger(UserService.class);
    }

    public synchronized void loadSession() throws IOException {
        logger.debug("Trying to load session");
        if (!LauncherPaths.getSessionFile().toFile().exists()) {
            logger.debug("No prior session found");
            javaSession = null;
            return;
        }

        logger.debug("Session found");
        final JsonObject session = Launcher.GSON.fromJson(Files.newBufferedReader(LauncherPaths.getSessionFile()), JsonObject.class);
        javaSession = MinecraftAuth.JAVA_DEVICE_CODE_LOGIN.fromJson(session);

        logger.debug("Session expired: {}", javaSession.isExpired());
        logger.debug("Session expired or outdated: {}", javaSession.isExpiredOrOutdated());
        logger.info("Session loaded.");
    }

    public synchronized void saveSession() throws IOException {
        JsonObject json = MinecraftAuth.JAVA_DEVICE_CODE_LOGIN.toJson(javaSession);
        Files.writeString(LauncherPaths.getSessionFile(), Launcher.GSON.toJson(json));
        logger.debug("Session saved.");
    }

    public boolean isValidSession() {
        return javaSession != null && !javaSession.isExpired();
    }

    public synchronized void login(Consumer<StepMsaDeviceCode.MsaDeviceCode> deviceCodeCallback) throws Exception {
        if (javaSession != null && javaSession.isExpired()) {
            javaSession = MinecraftAuth.JAVA_DEVICE_CODE_LOGIN.refresh(client, javaSession);
            return;
        }

        javaSession = MinecraftAuth.JAVA_DEVICE_CODE_LOGIN.getFromInput(client, new StepMsaDeviceCode.MsaDeviceCodeCallback(msaDeviceCode -> {
            logger.debug("Device code: {}, Uri: {}", msaDeviceCode.getUserCode(), msaDeviceCode.getVerificationUri());
            Platform.runLater(() -> deviceCodeCallback.accept(msaDeviceCode));
        }));

        logger.info("Successfully logged in.");
        saveSession();
    }

    public synchronized void logout() throws IOException {
        javaSession = null;
        Files.deleteIfExists(LauncherPaths.getSessionFile());
        logger.info("Successfully logged out.");
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
