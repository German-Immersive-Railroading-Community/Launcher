package com.troblecodings.launcher.util;

import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.json.JSONObject;

import com.google.gson.JsonParseException;
import com.troblecodings.launcher.Launcher;

import net.hycrafthd.minecraft_authenticator.login.AuthenticationException;
import net.hycrafthd.minecraft_authenticator.login.AuthenticationFile;
import net.hycrafthd.minecraft_authenticator.login.Authenticator;
import net.hycrafthd.minecraft_authenticator.login.User;

public class AuthUtil {
	
	private static volatile User SESSION;
	
	public static boolean checkSession() {
		if (SESSION != null) {
			return true;
		}
		
		final AuthenticationFile file;
		
		try {
			file = CryptoUtil.readEncrypted(FileUtil.REMEMBERFILE, AuthenticationFile.class);
		} catch (JsonParseException ex) {
			return false;
		}
		
		if (file == null) {
			return false;
		}
		
		try {
			final Authenticator authenticator = Authenticator.of(file).shouldAuthenticate().run();
			refreshAuthFile(authenticator.getResultFile());
			
			if (authenticator.getUser().isPresent()) {
				SESSION = authenticator.getUser().get();
				return true;
			}
		} catch (AuthenticationException ex) {
		}
		
		return false;
	}
	
	public static void mojangLogin(String user, String password) throws AuthenticationException {
		final Authenticator authenticator = Authenticator.ofYggdrasil(UUID.randomUUID().toString(), user, password).shouldAuthenticate().run();
		refreshAuthFile(authenticator.getResultFile());
		
		SESSION = authenticator.getUser().get();
	}
	
	public static void microsoftLogin(String authCode) throws AuthenticationException {
		final Authenticator authenticator = Authenticator.ofMicrosoft(authCode).shouldAuthenticate().run();
		refreshAuthFile(authenticator.getResultFile());
		
		SESSION = authenticator.getUser().get();
	}
	
	public static void refreshAuthFile(AuthenticationFile file) {
		CryptoUtil.saveEncrypted(FileUtil.REMEMBERFILE, file);
	}
	
	public static void logout() {
		try {
			Files.deleteIfExists(FileUtil.REMEMBERFILE);
		} catch (IOException ex) {
			Launcher.onError(ex);
		}
		SESSION = null;
		Launcher.setScene(Launcher.LOGINSCENE);
	}
	
	private static final String DEFAULT_ARGS = "--username ${auth_player_name} --version ${version_name} --gameDir ${game_directory} --assetsDir ${assets_root} --assetIndex ${assets_index_name} --uuid ${auth_uuid} --accessToken ${auth_access_token} --userType ${user_type} --tweakClass net.minecraftforge.fml.common.launcher.FMLTweaker --versionType Forge";
	
	private static final String getOrDefault(final JSONObject json, final String id, final String def) {
		if (json.has(id))
			return json.getString(id);
		Launcher.getLogger().warn("Couldn't find %s in %s! Using default!", id, json.toString());
		return def;
	}
	
	public static String[] make(final JSONObject json) {
		final User user = SESSION;
		if (user == null)
			return null;
		Map<String, String> list = new HashMap<>();
		list.put("${auth_player_name}", user.getName());
		list.put("${version_name}", getOrDefault(json, "id", "1.12.2"));
		list.put("${game_directory}", FileUtil.SETTINGS.baseDir);
		list.put("${assets_root}", FileUtil.ASSET_DIR);
		final JSONObject obj = json.getJSONObject("assetIndex");
		list.put("${assets_index_name}", getOrDefault(obj, "id", "1.12"));
		list.put("${auth_uuid}", user.getUuid().toString());
		list.put("${auth_access_token}", user.getAccessToken());
		list.put("${user_type}", user.getType());
		
		String[] arguments = getOrDefault(json, "minecraftArguments", DEFAULT_ARGS).split(" ");
		for (int i = 0; i < arguments.length; i++) {
			String newArg = list.get(arguments[i]);
			if (newArg != null)
				arguments[i] = newArg;
		}
		return arguments;
	}
	
}
