package com.troblecodings.launcher.services;

import net.raphimc.minecraftauth.java.model.MinecraftProfile;
import net.raphimc.minecraftauth.msa.model.MsaDeviceCode;
import org.json.JSONObject;

import java.io.IOException;
import java.util.function.Consumer;

public interface IUserService {
    void login(Consumer<MsaDeviceCode> callback) throws IOException;

    void logout() throws IOException;

    boolean isLoggedIn();

    MinecraftProfile getProfile() throws IOException;

    String[] createJavaArgs(JSONObject json);
}
