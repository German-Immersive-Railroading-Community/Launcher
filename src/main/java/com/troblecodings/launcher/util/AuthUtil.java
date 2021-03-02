package com.troblecodings.launcher.util;

import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import com.troblecodings.launcher.Launcher;

import javafx.application.Platform;
import net.cydhra.nidhogg.MojangClient;
import net.cydhra.nidhogg.YggdrasilAgent;
import net.cydhra.nidhogg.YggdrasilClient;
import net.cydhra.nidhogg.data.AccountCredentials;
import net.cydhra.nidhogg.data.Profile;
import net.cydhra.nidhogg.data.Session;

public class AuthUtil {

	private static YggdrasilClient client = new YggdrasilClient();
	private static MojangClient mclient = new MojangClient();

	public static String[] START_PARAMS = null;

	public static Session auth(final String username, final String passw) {
		Session session = FileUtil.DEFAULT;

		if (session == null) {
			if (username == null || passw == null)
				return null;
			session = client.login(new AccountCredentials(username, passw), YggdrasilAgent.MINECRAFT);
			CryptoUtil.saveEncrypted(FileUtil.REMEMBERFILE, session);
		}

		try {
			if (client.validate(session))
				return session;
		} catch (Throwable e) {
			try {
				client.refresh(session);
				CryptoUtil.saveEncrypted(FileUtil.REMEMBERFILE, session);
				return session;
			} catch (Throwable ex) {
				Platform.runLater(() -> {
					FileUtil.DEFAULT = null;
					Launcher.setScene(Launcher.LOGINSCENE);
				});
				Launcher.onError(ex);
			}
			Launcher.onError(e);
		}
		return session;
	}
	
	public static void logout() {
		client.invalidate(FileUtil.DEFAULT);
		FileUtil.DEFAULT = null;
		try {
			Files.deleteIfExists(FileUtil.REMEMBERFILE);
		} catch (IOException e) {
			Launcher.onError(e);
		}
		Launcher.setScene(Launcher.LOGINSCENE);
	}

	private static final String DEFAULT_ARGS = "--username ${auth_player_name} --version ${version_name} --gameDir ${game_directory} --assetsDir ${assets_root} --assetIndex ${assets_index_name} --uuid ${auth_uuid} --accessToken ${auth_access_token} --userType ${user_type} --tweakClass net.minecraftforge.fml.common.launcher.FMLTweaker --versionType Forge";

	private static final String getOrDefault(final JSONObject json, final String id, final String def) {
		if (json.has(id))
			return json.getString(id);
		Launcher.getLogger().warn("Couldn't find %s in %s! Using default!", id, json.toString());
		return def;
	}

	public static String[] make(final Session session, final JSONObject json) {
		if(session == null)
			return null;
		Profile profile = mclient.getProfileByUUID(session.getUuid());
		Map<String, String> list = new HashMap<>();
		list.put("${auth_player_name}", profile.getName());
		list.put("${version_name}", getOrDefault(json, "id", "1.12.2"));
		list.put("${game_directory}", FileUtil.SETTINGS.baseDir);
		list.put("${assets_root}", FileUtil.ASSET_DIR);
		final JSONObject obj = json.getJSONObject("assetIndex");
		list.put("${assets_index_name}", getOrDefault(obj, "id", "1.12"));
		list.put("${auth_uuid}", session.getUuid().toString());
		list.put("${auth_access_token}", session.getAccessToken());
		list.put("${user_type}", "mojang");

		String[] arguments = getOrDefault(json, "minecraftArguments", DEFAULT_ARGS).split(" ");
		for (int i = 0; i < arguments.length; i++) {
			String newArg = list.get(arguments[i]);
			if (newArg != null)
				arguments[i] = newArg;
		}
		return arguments;
	}

}
