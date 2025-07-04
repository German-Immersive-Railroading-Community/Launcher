package com.troblecodings.launcher.services;

import com.google.gson.JsonObject;
import com.troblecodings.launcher.Launcher;
import com.troblecodings.launcher.util.LauncherPaths;
import net.lenni0451.commons.httpclient.HttpClient;
import net.raphimc.minecraftauth.MinecraftAuth;
import net.raphimc.minecraftauth.step.java.session.StepFullJavaSession;
import net.raphimc.minecraftauth.step.msa.StepMsaDeviceCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
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

        logger.debug("Session expired or outdated: {}", javaSession.isExpiredOrOutdated());
    }

    public synchronized void saveSession() throws IOException {
        JsonObject json = MinecraftAuth.JAVA_DEVICE_CODE_LOGIN.toJson(javaSession);
        Files.writeString(LauncherPaths.getSessionFile(), Launcher.GSON.toJson(json));
    }

    public synchronized void login(Consumer<String> deviceCodeCallback) throws Exception {
        if (javaSession != null && javaSession.isExpiredOrOutdated()) {
            javaSession = MinecraftAuth.JAVA_DEVICE_CODE_LOGIN.refresh(client, javaSession);
            return;
        }

        javaSession = MinecraftAuth.JAVA_DEVICE_CODE_LOGIN.getFromInput(client, new StepMsaDeviceCode.MsaDeviceCodeCallback(msaDeviceCode -> {
            // Method to generate a verification URL and a code for the user to enter on that page
            System.out.println("Go to " + msaDeviceCode.getVerificationUri());
            System.out.println("Enter code " + msaDeviceCode.getUserCode());

            // There is also a method to generate a direct URL without needing the user to enter a code
            System.out.println("Go to " + msaDeviceCode.getDirectVerificationUri());
        }));
        System.out.println("Username: " + javaSession.getMcProfile().getName());
        System.out.println("Access token: " + javaSession.getMcProfile().getMcToken().getAccessToken());
        System.out.println("Player certificates: " + javaSession.getPlayerCertificates());

        saveSession();
    }
}
